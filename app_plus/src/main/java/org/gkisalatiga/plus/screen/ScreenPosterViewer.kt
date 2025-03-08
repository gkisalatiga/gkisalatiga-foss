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
import android.app.Application
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.fragment.FragmentHomeCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.StringFormatter

class ScreenPosterViewer (private val current : ActivityData) : ComponentActivity() {

    // The pager state.
    private lateinit var horizontalPagerState: PagerState

    // The coroutine scope.
    private lateinit var scope: CoroutineScope

    // The calulated top padding.
    private var calculatedTopPadding = 0.dp
    private var calculatedBottomPadding = 0.dp

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        horizontalPagerState = FragmentHomeCompanion.rememberedCarouselPagerState!!
        scope = rememberCoroutineScope()

        Box (Modifier.fillMaxSize()) {
            // Let the top and bottom bars be below the scrim.
            Scaffold (
                topBar = { getTopBar() },
                bottomBar = { getBottomBar() },
                // TODO: Implement once ready.
                // floatingActionButton =  { getFloatingActionButton() },
                // floatingActionButtonPosition = FabPosition.Center
            ) {
                calculatedTopPadding = it.calculateTopPadding()
                calculatedBottomPadding = it.calculateBottomPadding()

                // Display the necessary content.
                Box ( Modifier.padding(top = calculatedTopPadding, bottom = calculatedBottomPadding) ) {
                    getMainContent()
                }
            }

            // The download progress circle.
            if (ScreenGaleriViewCompanion.showScreenGaleriViewDownloadProgress.value) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))  // Semi-transparent scrim
                    .clickable(onClick = { /* Disable user input during progression. */ }),
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            // Show some alert dialogs.
            if (ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = false
                    },
                    title = { Text(ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogTitle) },
                    text = { Text(ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogSubtitle) },
                    confirmButton = {
                        Button(onClick = { ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = false }) {
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
            // Do not return back if we are downloading.
            if (!ScreenGaleriViewCompanion.showScreenGaleriViewDownloadProgress.value) {
                AppNavigation.popBack()
                scope.launch {
                    ScreenGaleriListCompanion.rememberedLazyGridState!!.scrollToItem(horizontalPagerState.currentPage)
                }
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
        FloatingActionButton (
            onClick = {
                /*val currentPhotoObject = ScreenGaleriListCompanion.targetAlbumContent!!.getJSONObject(horizontalPagerState.currentPage)
                val name = currentPhotoObject.getString("name")
                val id = currentPhotoObject.getString("id")

                // Obtain the download URL.
                val downloadURL = StringFormatter.getGoogleDriveDownloadURL(id)

                GallerySaver().saveImageFromURL(current.ctx, downloadURL, name)
                */
            },
            shape = CircleShape,
            modifier = Modifier.scale(1.5f).offset(0.dp, 30.dp)
        ) {
            Icon(Icons.Filled.Share, contentDescription = "")
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
            val currentPhotoObject = FragmentHomeCompanion.filteredCarouselPostersList!!.let { it[page % it.size] }
            val title = currentPhotoObject.getString("title")
            val caption = currentPhotoObject.getString("poster-caption")
            val imageURL = currentPhotoObject.getString("poster-image")
            val date = currentPhotoObject.getString("date-created")

            // Displaying the zoomable image view.
            val zoomState = rememberZoomState()
            AsyncImage(
                model = imageURL,
                modifier = Modifier.zoomable(
                    zoomState,
                    onDoubleTap = { position -> zoomState.toggleScale(ScreenPosterViewerCompanion.PAGE_ZOOM_TARGET_SCALE, position) },
                    scrollGesturePropagation = ScrollGesturePropagation.ContentEdge
                ).fillMaxSize().background(current.colors.mainZoomableBoxBackgroundColor),
                error = painterResource(R.drawable.thumbnail_error_stretched),
                placeholder = painterResource(R.drawable.thumbnail_placeholder),
                contentDescription = "Poster carousel display view.",
                contentScale = ContentScale.Fit
            )

        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        // Calculate the top bar title.
        val currentPage = FragmentHomeCompanion.rememberedCarouselPagerState!!.currentPage
        val topBarTitle = FragmentHomeCompanion.filteredCarouselPostersList!!.let { it[currentPage % it.size].getString("title") }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            /* The navigation top bar. */
            CenterAlignedTopAppBar(
                colors = TopAppBarColorScheme.default(),
                title = {
                    Text(
                        topBarTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        AppNavigation.popBack()
                        scope.launch {
                            ScreenGaleriListCompanion.rememberedLazyGridState!!.scrollToItem(horizontalPagerState.currentPage)
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

class ScreenPosterViewerCompanion : Application() {
    companion object {
        /* Determines the maximum zoom scale of the image. */
        const val PAGE_ZOOM_TARGET_SCALE = 3.5f
    }
}
