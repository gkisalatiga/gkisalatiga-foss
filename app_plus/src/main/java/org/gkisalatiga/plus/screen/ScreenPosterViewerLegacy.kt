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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.lib.AppNavigation

/**
 * This is the legacy viewer for poster-class carousels.
 * It can only display single-images without scrolling.
 * It does not support multiple image viewing, such as that implemented in [ScreenGaleriView].
 * It is no longer used to display main screen's carousels.
 *
 * Use [ScreenPosterViewer] instead.
 */
@Deprecated("Deprecated since v0.6.5. Please use ScreenPosterViewer() instead.")
class ScreenPosterViewerLegacy(private val current : ActivityData) : ComponentActivity() {

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
        val targetPosterSource = ScreenPosterViewerLegacyCompanion.posterViewerImageSource

        // Displaying the poster image from a remote source.
        val zoomState = rememberZoomState()
        AsyncImage(
            model = targetPosterSource,
            modifier = Modifier.zoomable(
                zoomState,
                onDoubleTap = { position -> zoomState.toggleScale(ScreenPosterViewerLegacyCompanion.PAGE_ZOOM_TARGET_SCALE, position) },
                scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
            ).fillMaxSize().background(current.colors.mainZoomableBoxBackgroundColor),
            error = painterResource(R.drawable.thumbnail_error_stretched),
            placeholder = painterResource(R.drawable.thumbnail_placeholder),
            contentDescription = "Poster display view.",
            contentScale = ContentScale.Fit
        )

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    ScreenPosterViewerLegacyCompanion.posterViewerTitle,
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

@Deprecated("Deprecated since v0.6.5. Please use ScreenPosterViewerCompanion() instead.")
class ScreenPosterViewerLegacyCompanion : Application() {
    companion object {
        /* Determines the maximum zoom scale of the image. */
        const val PAGE_ZOOM_TARGET_SCALE = 3.5f

        /* The poster image title and URL. */
        var posterViewerTitle: String = String()
        var posterViewerCaption: String = String()
        var posterViewerImageSource: String = String()
    }
}