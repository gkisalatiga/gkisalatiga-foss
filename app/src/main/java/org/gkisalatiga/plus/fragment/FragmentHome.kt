/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONObject
import java.io.File
import kotlin.math.ceil
import kotlin.math.round

class FragmentHome : ComponentActivity() {

    // The following defines the visible menu buttons shown in the main menu,
    // as well as their corresponding navigation targets.
    private val btnRoutes = listOf(
        NavigationRoutes().SCREEN_WARTA,
        NavigationRoutes().SCREEN_LITURGI,
        NavigationRoutes().SCREEN_AGENDA,
        NavigationRoutes().SCREEN_PERSEMBAHAN,
        NavigationRoutes().SCREEN_YKB,
        NavigationRoutes().SCREEN_FORMS,
        NavigationRoutes().SCREEN_GALERI,
        NavigationRoutes().SCREEN_MEDIA,
    )

    // The following defines the label of each visible menu button.
    private lateinit var btnLabels: List<String>

    // The following defines each visible menu button's icon description.
    private lateinit var btnDescriptions: List<String>

    // The following defines the icons used for the visible menu buttons.
    // Find the icons here: https://fonts.google.com/icons
    private val btnIcons = listOf(
        Icons.Default.CheckBoxOutlineBlank,  // --- we don't need icon for this entry.
        Icons.Default.CheckBoxOutlineBlank,  // --- we don't need icon for this entry.
        Icons.Default.Today,
        Icons.Default.QrCodeScanner,
        Icons.AutoMirrored.Default.MenuBook,
        Icons.Default.QuestionAnswer,
        Icons.Default.Wallpaper,
        Icons.Default.Subscriptions,
    )

    @Composable
    public fun getComposable() {
        val ctx = LocalContext.current

        /* Initialized the "lateinit" variables. */
        btnLabels = listOf(
            ctx.resources.getString(R.string.btn_mainmenu_wj),
            ctx.resources.getString(R.string.btn_mainmenu_liturgi),
            ctx.resources.getString(R.string.btn_mainmenu_agenda),
            ctx.resources.getString(R.string.btn_mainmenu_offertory),
            ctx.resources.getString(R.string.btn_mainmenu_ykb),
            ctx.resources.getString(R.string.btn_mainmenu_form),
            ctx.resources.getString(R.string.btn_mainmenu_gallery),
            ctx.resources.getString(R.string.btn_mainmenu_media),
        )
        btnDescriptions = listOf(
            ctx.resources.getString(R.string.btn_desc_mainmenu_wj),
            ctx.resources.getString(R.string.btn_desc_mainmenu_liturgi),
            ctx.resources.getString(R.string.btn_desc_mainmenu_agenda),
            ctx.resources.getString(R.string.btn_desc_mainmenu_offertory),
            ctx.resources.getString(R.string.btn_desc_mainmenu_ykb),
            ctx.resources.getString(R.string.btn_desc_mainmenu_form),
            ctx.resources.getString(R.string.btn_desc_mainmenu_gallery),
            ctx.resources.getString(R.string.btn_desc_mainmenu_media),
        )

        // Prepare the poster dialog.
        getPosterDialog()

        // The following defines each individual featured cover image of the menu.
        // (Only the top two menus are considered.)
        val btnFeaturedCover = listOf(
            R.drawable.menu_cover_wj,
            R.drawable.menu_cover_liturgi
        )

        // Get the number of carousel banners.
        val actualPageCount = GlobalSchema.carouselJSONKey.size

        // Retrieving the global state.
        val carouselPagerState = GlobalSchema.fragmentHomeCarouselPagerState!!

        /* Set-up the launched effect for auto-scrolling the horizontal carousel/pager. */
        // SOURCE: https://stackoverflow.com/a/67615616
        LaunchedEffect(carouselPagerState.settledPage) {
            launch {
                delay(2500)
                with(carouselPagerState) {
                    animateScrollToPage(
                        page = currentPage + 1,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }  // --- end of launched effect.

        /* --------------------------------------------------------------- */

        // Setting the layout to center both vertically and horizontally
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = GlobalSchema.fragmentHomeScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {

            /* Show the "infinite" horizontal carousel for CTA. */
            // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
            // SOURCE: https://stackoverflow.com/a/75469260
            // ---
            // Create the box boundary.
            Box (modifier = Modifier.fillMaxWidth().aspectRatio(1.77778f)) {

                /* Create the horizontal pager "carousel" */
                HorizontalPager(
                    state = carouselPagerState,
                    beyondViewportPageCount = 5,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Navigate to the current iteration's JSON node.
                    val currentNode = GlobalSchema.carouselJSONObject[it % actualPageCount]

                    /* Display the carousel banner image. */
                    Surface (
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.padding(LocalContext.current.resources.getDimension(R.dimen.banner_inner_padding).dp).fillMaxWidth().aspectRatio(1.77778f),
                        onClick = {
                            if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You are clicking carousel banner no. ${it % actualPageCount}!", Toast.LENGTH_SHORT).show()

                            // Get the type of the current carousel banner.
                            val currentType = currentNode.getString("type")

                            /* Switch to a different screen or run a certain action based on the carousel banner type. */
                            when (currentType) {
                                "article" -> {
                                    // Preparing the WebView arguments.
                                    val url = currentNode.getString("article-url")
                                    val title = currentNode.getString("title")

                                    // Navigate to the WebView viewer.
                                    GlobalSchema.webViewTargetURL = url
                                    GlobalSchema.webViewTitle = title
                                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_WEBVIEW

                                    // Set this screen as the anchor point for "back"
                                    GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                                }
                                "poster" -> {
                                    showPosterDialog.value = true
                                    posterDialogTitle.value = currentNode.getString("title")
                                    posterDialogCaption.value = currentNode.getString("poster-caption")
                                    posterDialogImageSource.value = currentNode.getString("poster-image")
                                }
                                "yt" -> {
                                    // Preparing the YouTube player arguments.
                                    val url = currentNode.getString("yt-link")
                                    val title = currentNode.getString("yt-title")
                                    val date = currentNode.getString("yt-date")
                                    val desc = currentNode.getString("yt-desc")

                                    // Trying to switch to the YouTube viewer and open the stream.
                                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Opening the YouTube stream: $url.")
                                    GlobalSchema.ytViewerParameters["yt-link"] = url
                                    GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter().getYouTubeIDFromUrl(url)
                                    GlobalSchema.ytViewerParameters["title"] = title!!
                                    GlobalSchema.ytViewerParameters["date"] = StringFormatter().convertDateFromJSON(date)
                                    GlobalSchema.ytViewerParameters["desc"] = desc!!
                                    GlobalSchema.ytCurrentSecond.floatValue = 0.0f
                                    GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_LIVE
                                }
                            }
                        }
                    ) {
                        AsyncImage(
                            model = currentNode.getString("banner"),
                            contentDescription = "Carousel Image ${it % actualPageCount}",
                            error = painterResource(R.drawable.thumbnail_loading_no_text),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Create the pager indicator.
                // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
                Row(
                    modifier = Modifier.height(45.dp).fillMaxWidth().align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(actualPageCount) { iteration ->
                        val color = if (carouselPagerState.currentPage % actualPageCount == iteration) Color.White else Color.White.copy(alpha = 0.5f)

                        // The individual dot for indicating carousel page.
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(10.dp)
                        )
                    }
                }

            }

            Spacer(Modifier.fillMaxWidth().height(10.dp))

            /* Displaying the top two menus. */
            Row {
                btnRoutes.subList(0, 2).forEachIndexed { index, str ->
                    // The individual card item.
                    Card (modifier = Modifier.padding(10.dp).fillMaxWidth().weight(1f), onClick = {
                        // This will be triggered when the main menu button is clicked.
                        if (btnRoutes[index] != NavigationRoutes().SCREEN_BLANK) {
                            GlobalSchema.pushScreen.value = btnRoutes[index]
                        }
                    }) {
                        Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp), modifier = Modifier.fillMaxWidth().aspectRatio(0.75f)) {
                                Image(painter = painterResource(btnFeaturedCover[index]), "", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                            Text(btnLabels[index], textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.fillMaxWidth().height(10.dp))

            // The modifier that applies to both the actual buttons and the spacers.
            // val buttonSpacerModifier = Modifier.weight(1f).padding(5.dp).height(125.dp)
            val buttonSpacerModifier = Modifier.weight(1f).padding(5.dp).aspectRatio(0.88888f)

            // The menu array after "popping" the first two elements.
            val subArray = btnRoutes.subList(2, btnRoutes.size)

            /* Displaying the main menu action buttons other than the first two. */
            // Assumes btnRoutes, btnLabels, and btnIcons all have the same size.
            val columns = 3
            val rows = ceil((subArray.size / columns).toDouble()).toInt()

            var index = 0
            for (j in 0 .. rows) {

                // Ensures that we don't draw an empty row when there is no data.
                if (index == subArray.size) break

                Row {
                    while (index < subArray.size) {
                        val offsetIndex = index + 2

                        // Displaying the menu button.
                        Button (
                            onClick = {
                                // This will be triggered when the main menu button is clicked.
                                if (btnRoutes[offsetIndex] != NavigationRoutes().SCREEN_BLANK) {
                                    GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                                    GlobalSchema.pushScreen.value = btnRoutes[offsetIndex]
                                }
                            },
                            modifier = buttonSpacerModifier,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp)
                        ) {
                            // The main menu element wrapper.
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()) {
                                // The main menu action button icon.
                                Icon(
                                    btnIcons[offsetIndex],
                                    contentDescription = btnDescriptions[offsetIndex],
                                    tint = Color.White,
                                    modifier = Modifier.fillMaxHeight(0.50f).aspectRatio(1.0f)
                                )
                                // Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                // Spacer(Modifier.fillMaxHeight(0.10f))
                                // The text.
                                Text(
                                    btnLabels[offsetIndex],
                                    textAlign = TextAlign.Center,
                                    minLines = 1,
                                    maxLines = 2,
                                    overflow = TextOverflow.Visible,
                                    softWrap = true,
                                    modifier = Modifier
                                        // .fillMaxHeight(0.50f)
                                        .padding(3.dp)
                                )
                            }
                        }

                        // Ensures that we have the right amount of columns.
                        index += 1
                        if (index % columns == 0) break
                    }

                    // Add spacer for non-even button rows. (Visual improvement.)
                    // Only applies to the last row.
                    if (j == rows) {
                        repeat(columns - (subArray.size % columns)) {
                            Spacer(buttonSpacerModifier)
                        }
                    }

                }
            }  // --- end of for loop.
        }  // --- end of scrollable column.

    }  // --- end of getComposable().

    // The state of the current poster dialog.
    private val showPosterDialog = GlobalSchema.fragmentHomePosterDialogState
    private val posterDialogTitle = GlobalSchema.posterDialogTitle
    private val posterDialogCaption = GlobalSchema.posterDialogCaption
    private val posterDialogImageSource = GlobalSchema.posterDialogImageSource

    /**
     * This function displays the poster dialog.
     * SOURCE: https://www.composables.com/tutorials/dialogs
     * SOURCE: https://developer.android.com/develop/ui/compose/components/dialog
     */
    @Composable
    @SuppressLint("ComposableNaming")
    private fun getPosterDialog() {
        val ctx = LocalContext.current
        val verticalScrollState = rememberScrollState()
        if (showPosterDialog.value) {
            AlertDialog(
                onDismissRequest = { showPosterDialog.value = false },
                title = { Text(posterDialogTitle.value, fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                text = {
                    Column(
                        modifier = Modifier.height(300.dp).verticalScroll(verticalScrollState)
                    ) {
                        Text("Ketuk pada gambar untuk memperbesar poster.", fontStyle = FontStyle.Italic)
                        Spacer(Modifier.height(15.dp))
                        Surface (modifier = Modifier.fillMaxWidth().height(150.dp), color = Color.Transparent, onClick = {
                            showPosterDialog.value = false
                            GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_MAIN
                            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_POSTER_VIEWER
                        }, shape = RoundedCornerShape(20.dp)) {
                            AsyncImage(
                                model = posterDialogImageSource.value,
                                contentDescription = "Carousel Poster Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.height(15.dp))
                        Text(posterDialogCaption.value)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPosterDialog.value = false }) {
                        Text(stringResource(R.string.poster_dialog_close_text).uppercase())
                    }
                },
                confirmButton = { }
            )
        }
    }

}