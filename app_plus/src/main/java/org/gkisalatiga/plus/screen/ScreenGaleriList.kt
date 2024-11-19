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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONArray

class ScreenGaleriList : ComponentActivity() {

    // The pager state.
    private lateinit var horizontalPagerState: PagerState

    // The currently selected tab index.
    private val selectedTabIndex = mutableIntStateOf(0)

    // The coroutine scope.
    private lateinit var scope: CoroutineScope

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        horizontalPagerState = rememberPagerState ( pageCount = {2}, initialPage = 0 )
        scope = rememberCoroutineScope()

        Scaffold (
            topBar = { this.getTopBar() }
                ) {
            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }

        // Integrate the horizontal pager with the top tab.
        LaunchedEffect(horizontalPagerState.currentPage) {
            selectedTabIndex.intValue = horizontalPagerState.currentPage
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    private fun getMainContent() {

        // Titles, stories, and such.
        val title = ScreenGaleriListCompanion.displayedAlbumTitle
        val story = ScreenGaleriListCompanion.displayedAlbumStory
        val featuredImageID = ScreenGaleriListCompanion.displayedFeaturedImageID

        /* Extract JSONArray to regular list. As always, JSONArray starts at 1. */
        val extractedAlbumContent = mutableListOf(mapOf<String, String>())
        for (i in 0 until ScreenGaleriListCompanion.targetAlbumContent!!.length()) {
            val curItem = ScreenGaleriListCompanion.targetAlbumContent!!.getJSONObject(i)
            extractedAlbumContent.add(
                mapOf<String, String> (
                    "date" to curItem.getString("date"),
                    "id" to curItem.getString("id"),
                    "name" to curItem.getString("name")
                )
            )
        }
        extractedAlbumContent.removeAt(0)

        /* Displaying the thumbnails. */
        Box (Modifier.fillMaxSize()) {
            val verticalScrollState = ScreenGaleriListCompanion.rememberedLazyGridState!!
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp).padding(bottom = 10.dp),
                state = verticalScrollState,
            ) {

                item (span = { GridItemSpan(3) }) {
                    Column (Modifier.padding(horizontal = 10.dp).padding(top = 20.dp)) {
                        // Displaying featured banner.
                        Surface (
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(bottom = 20.dp),
                        ) {
                            AsyncImage(
                                model = StringFormatter.getGoogleDriveThumbnail(featuredImageID, 240),
                                contentDescription = title,
                                error = painterResource(R.drawable.thumbnail_error),
                                placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                modifier = Modifier.aspectRatio(1.77778f).fillMaxSize().background(Color.Gray),
                                contentScale = ContentScale.Crop,
                            )
                        }

                        // Displaying the meta-information.
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(bottom = 10.dp))
                        Text(story, fontWeight = FontWeight.Normal, fontSize = 14.sp)

                        /* Add a visually dividing divider :D */
                        HorizontalDivider(Modifier.padding(top = 20.dp).padding(bottom = 10.dp))
                    }
                }

                extractedAlbumContent.forEachIndexed { index, map ->
                    val photoThumbnail = StringFormatter.getGoogleDriveThumbnail(map["id"]!!)
                    item {
                        Surface(
                            onClick = {
                                ScreenGaleriViewCompanion.galleryViewerStartPage = index
                                AppNavigation.navigate(NavigationRoutes.SCREEN_GALERI_VIEW)
                            },
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RectangleShape)
                                .padding(10.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            /* Displaying the thumbnail. */
                            AsyncImage(
                                model = photoThumbnail,
                                contentDescription = "",
                                error = painterResource(R.drawable.thumbnail_error),
                                placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }  // --- end of surface.
                }  // --- end of forEachIndexed iteration.

            }  // --- end of lazy grid.
        }  // --- end of box.

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val topBarTitle = ScreenGaleriListCompanion.displayedAlbumTitle

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
                    IconButton(onClick = { AppNavigation.popBack() }) {
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

class ScreenGaleriListCompanion : Application() {
    companion object {
        internal var displayedAlbumTitle: String = ""
        internal var displayedAlbumStory: String = ""
        internal var displayedFeaturedImageID: String = ""
        internal var targetAlbumContent: JSONArray? = null

        /* The screen's remembered lazy grid state. */
        var rememberedLazyGridState: LazyGridState? = null

        /**
         * This function neatly and thoroughly passes the respective arguments to the screen's handler.
         * @param albumTitle the target album's title.
         * @param albumStory the target album's story.
         * @param featuredImageId the featured image that will visually describe the album.
         * @param albumContent the target album contents (photographs) that will be enlisted.
         */
        fun putArguments(albumTitle: String, albumStory: String, featuredImageId: String, albumContent: JSONArray?) {
            displayedAlbumTitle = albumTitle
            displayedAlbumStory = albumStory
            displayedFeaturedImageID = featuredImageId
            targetAlbumContent = albumContent
        }
    }
}