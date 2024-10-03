/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load a custom HTML body.
 * Only those HTML contents stored in the JSON schema's "data/static" node can be displayed.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.GallerySaver
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.lib.external.ZoomableBox

class ScreenGaleriView : ComponentActivity() {

    // The pager state.
    private lateinit var horizontalPagerState: PagerState

    // The coroutine scope.
    private lateinit var scope: CoroutineScope

    // The screen title.
    private var currentScreenTopBarTitle = mutableStateOf("")

    // The calulated top padding.
    private var calculatedTopPadding = 0.dp
    private var calculatedBottomPadding = 0.dp

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        val horizontalPageCount = GlobalSchema.targetAlbumContent!!.length()
        horizontalPagerState = rememberPagerState ( pageCount = {horizontalPageCount}, initialPage = GlobalSchema.galleryViewerStartPage )
        scope = rememberCoroutineScope()

        Box (Modifier.fillMaxSize()) {
            // Let the top and bottom bars be below the scrim.
            Scaffold (
                topBar = { getTopBar() },
                bottomBar = { getBottomBar() },
                floatingActionButton =  { getFloatingActionButton() },
                floatingActionButtonPosition = FabPosition.Center
            ) {
                calculatedTopPadding = it.calculateTopPadding()
                calculatedBottomPadding = it.calculateBottomPadding()

                // Display the necessary content.
                Box ( Modifier.padding(top = calculatedTopPadding, bottom = calculatedBottomPadding) ) {
                    getMainContent()
                }
            }

            // The download progress circle.
            if (GlobalSchema.showScreenGaleriViewDownloadProgress.value) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))  // Semi-transparent scrim
                    .clickable(onClick = { /* Disable user input during progression. */ }),
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            // Show some alert dialogs.
            if (GlobalSchema.showScreenGaleriViewAlertDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        GlobalSchema.showScreenGaleriViewAlertDialog.value = false
                    },
                    title = { Text(GlobalSchema.txtScreenGaleriViewAlertDialogTitle) },
                    text = { Text(GlobalSchema.txtScreenGaleriViewAlertDialogSubtitle) },
                    confirmButton = {
                        Button(onClick = { GlobalSchema.showScreenGaleriViewAlertDialog.value = false }) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
            }
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
            GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_GALERI_YEAR
            scope.launch {
                GlobalSchema.fragmentGalleryListScrollState!!.scrollToItem(horizontalPagerState.currentPage)
            }
        }

    }

    @Composable
    private fun getBottomBar() {
        BottomAppBar {
            Row(Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    /* Go to previous image. */
                    scope.launch {
                        val currentPage = horizontalPagerState.currentPage
                        if (currentPage - 1 >= 0) {
                            horizontalPagerState.animateScrollToPage(currentPage - 1)
                        }
                    }
                }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(25.dp)) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, "")
                }
                Spacer(Modifier.weight(3f))
                Button(onClick = {
                    /* Go to the next image. */
                    scope.launch {
                        val currentPage = horizontalPagerState.currentPage
                        val maxPage = horizontalPagerState.pageCount
                        if (currentPage + 1 <= maxPage) {
                            horizontalPagerState.animateScrollToPage(currentPage + 1)
                        }
                    }
                }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(25.dp)) {
                    Icon(Icons.AutoMirrored.Default.ArrowForward, "")
                }
            }
        }
    }

    @Composable
    private fun getFloatingActionButton() {
        val ctx = LocalContext.current
        FloatingActionButton (
            onClick = {
                val currentPhotoObject = GlobalSchema.targetAlbumContent!!.getJSONObject(horizontalPagerState.currentPage)
                val name = currentPhotoObject.getString("name")
                val date = currentPhotoObject.getString("date")
                val id = currentPhotoObject.getString("id")

                // Obtain the download URL.
                val downloadURL = StringFormatter().getGoogleDriveDownloadURL(id)

                GallerySaver().saveImageFromURL(ctx, downloadURL, name)
            },
            shape = CircleShape,
            modifier = Modifier.scale(1.5f).offset(0.dp, 30.dp)
        ) {
            Icon(Icons.Filled.Download, contentDescription = "")
        }
    }

    @Composable
    private fun getMainContent() {
        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 0.dp),
            // Without this property, the left-right page scrolling would be insanely laggy!
            beyondViewportPageCount = 2
        ) { page ->
            // The photo's specific metadata.
            val currentPhotoObject = GlobalSchema.targetAlbumContent!!.getJSONObject(page)
            val name = currentPhotoObject.getString("name")
            val date = currentPhotoObject.getString("date")
            val id = currentPhotoObject.getString("id")

            // The image URL.
            val imageURL = StringFormatter().getGoogleDriveThumbnail(id, 600)

            // Set the screen's title.
            currentScreenTopBarTitle.value = name

            ZoomableBox {
                AsyncImage(
                    model = imageURL,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        ),
                    contentDescription = "",
                    error = painterResource(R.drawable.thumbnail_loading),
                    contentScale = ContentScale.Fit
                )
            }

        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val topBarTitle = currentScreenTopBarTitle.value

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            /* The navigation top bar. */
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        topBarTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                        GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_GALERI_YEAR
                        scope.launch {
                            GlobalSchema.fragmentGalleryListScrollState!!.scrollToItem(horizontalPagerState.currentPage)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = { },
                scrollBehavior = scrollBehavior
            )
        }
    }

}