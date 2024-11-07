/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load a custom poster image.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable
import org.gkisalatiga.plus.lib.AppNavigation


class ScreenPosterViewer : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        Scaffold ( topBar = { this.getTopBar() } ) {

            // Display the necessary content.
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
    @SuppressLint("SetJavaScriptEnabled", "ComposableNaming")
    private fun getMainContent() {

        // Declare the local image path that will be displayed.
        val targetPosterSource = ScreenPosterViewerCompanion.posterViewerImageSource

        // Displaying the poster image from a remote source.
        val zoomState = rememberZoomState()
        AsyncImage(
            model = targetPosterSource,
            modifier = Modifier.zoomable(
                zoomState,
                onDoubleTap = { position -> zoomState.toggleScale(ScreenPosterViewerCompanion.PAGE_ZOOM_TARGET_SCALE, position) },
                scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
            ).fillMaxSize().background(Color.White),
            contentDescription = "Poster display view.",
            contentScale = ContentScale.Fit
        )

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    ScreenPosterViewerCompanion.posterViewerTitle,
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

class ScreenPosterViewerCompanion : Application() {
    companion object {
        /* Determines the maximum zoom scale of the image. */
        const val PAGE_ZOOM_TARGET_SCALE = 3.5f

        /* The poster image title and URL. */
        var posterViewerTitle: String = String()
        var posterViewerCaption: String = String()
        var posterViewerImageSource: String = String()
    }
}