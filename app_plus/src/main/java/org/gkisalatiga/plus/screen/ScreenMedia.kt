/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the YKB daily devotional.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.MainYouTubePlaylistObject
import org.gkisalatiga.plus.data.MainYouTubeVideoContentObject
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONArray
import org.json.JSONObject

class ScreenMedia (private val current : ActivityData) : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {

        Scaffold (
            topBar = { getTopBar() }
                ) {
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }
    
    @Composable
    private fun getMainContent() {

        // The "all video playlists" JSON array.
        // val allVideoPlaylistArray = MainCompanion.jsonRoot!!.getJSONArray("yt")
        val allVideoPlaylistArray = MainCompanion.api!!.yt

        // Get the non-pinned video playlists.
        val ordinaryPlaylistDictionary: MutableList<MainYouTubePlaylistObject> = mutableListOf()
        for (i in 0 until allVideoPlaylistArray.size) {
            if (allVideoPlaylistArray[i].pinned == 0) {
                ordinaryPlaylistDictionary.add(allVideoPlaylistArray[i])
            }
        }

        // Enabling vertical scrolling, and setting the layout to center both vertically and horizontally.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        // SOURCE: https://stackoverflow.com/a/72769561
        val scrollState = ScreenMediaCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            // Assumes both "pinnedPlaylistTitle" and "pinnedPlaylistContent" have the same list size.
            ordinaryPlaylistDictionary.forEach { obj ->
                // Displaying the relevant YouTube-based church services.
                getMediaUI(obj.title, obj.content)
            }
        }

    }

    /**
     * Obtains the composable UI of the "list of non-pinned service playlist".
     * The data displayed is based on the arguments supplied.
     * @param nodeName the name of the JSON metadata node that represents the desired service section.
     * @param sectionTitle the title string that will be displayed on top of the section.
     */
    @Composable
    private fun getMediaUI(sectionTitle: String, sectionContent: MutableList<MainYouTubeVideoContentObject>) {

        // Testing and debugging.
        Logger.logTest({}, "Size of the JSONObject's parsed list is: ${sectionContent.size}")

        /* Displaying the section title. */
        Row (modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(sectionTitle, modifier = Modifier.fillMaxWidth().weight(4f), fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis)
            Button(onClick = {
                // Display the list of videos in this playlist.
                ScreenVideoListCompanion.putArguments(sectionContent, sectionTitle)
                // Navigate to the video playlist viewer.
                AppNavigation.navigate(NavigationRoutes.SCREEN_VIDEO_LIST)
            }, modifier = Modifier.fillMaxWidth().weight(1f).padding(0.dp).wrapContentSize(Alignment.Center, true)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Some desc", modifier = Modifier.fillMaxSize().aspectRatio(1.0f).padding(0.dp))
            }
        }

        /* Display the video banner image (first video in the list). */
        if (sectionContent.size >= 1) {
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 10.dp).aspectRatio(1.77778f),
                onClick = {
                    // Display the list of videos in this playlist.
                    ScreenVideoListCompanion.putArguments(sectionContent, sectionTitle)
                    // Navigate to the playlist view.
                    AppNavigation.navigate(NavigationRoutes.SCREEN_VIDEO_LIST)
                }
            ) {
                AsyncImage(
                    sectionContent[0].thumbnail,
                    contentDescription = "",
                    error = painterResource(R.drawable.thumbnail_error_stretched),
                    placeholder = painterResource(R.drawable.thumbnail_placeholder),
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        /* Displaying redundant spacer for visual neatness. */
        Spacer(modifier = Modifier.height(20.dp))

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screenmedia_title),
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

class ScreenMediaCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}