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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.fragment.FragmentGalleryList
import org.gkisalatiga.plus.fragment.FragmentGalleryStory
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
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
        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .wrapContentHeight()
                .padding(top = 0.dp),
            // Without this property, the left-right page scrolling would be insanely laggy!
            beyondViewportPageCount = 2
        ) { page ->
            when (page) {
                0 -> FragmentGalleryList().getComposable()
                1 -> FragmentGalleryStory().getComposable()
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val topBarTitle = ScreenGaleriListCompanion.displayedAlbumTitle
        val tabs = listOf("Album", "Kisah")  // TODO: Extract string to strings.xml
        val icons = listOf(
            Icons.Outlined.PhotoCamera,
            Icons.AutoMirrored.Outlined.Article
        )
        val iconsSelected = listOf(
            Icons.Filled.PhotoCamera,
            Icons.AutoMirrored.Filled.Article
        )

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

            /* The tab row underneath the top bar. */
            TabRow(
                selectedTabIndex = selectedTabIndex.intValue
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    val tabFontWeight = if (selectedTabIndex.intValue == index) FontWeight.Bold else FontWeight.Normal
                    val tabIcon = if (selectedTabIndex.intValue == index) iconsSelected[index] else icons[index]

                    Tab(
                        modifier = Modifier.height(75.dp),
                        selected = selectedTabIndex.intValue == index,
                        icon = { Icon(tabIcon, "") },
                        text = { Text(tabTitle, fontWeight = tabFontWeight, fontSize = 18.sp) },
                        onClick = {
                            selectedTabIndex.intValue = index
                            scope.launch {
                                horizontalPagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    }

}

class ScreenGaleriListCompanion : Application() {
    companion object {
        internal var displayedAlbumTitle: String = ""
        internal var displayedAlbumStory: String = ""
        internal var displayedFeaturedImageID: String = ""
        internal var targetAlbumContent: JSONArray? = null

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