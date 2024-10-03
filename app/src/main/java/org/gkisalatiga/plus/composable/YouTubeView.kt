/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.composable

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.gkisalatiga.plus.global.GlobalSchema

class YouTubeView {
    companion object {
        /**
         * This function handles the action when the "fullscreen" button is pressed.
         */
        fun handleFullscreenStateChange(ctx: Context) {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[YouTubeView.handleFullscreenStateChange] Fullscreen button is clicked.")
            GlobalSchema.ytCurrentSecond.floatValue = GlobalSchema.ytTracker.currentSecond
            GlobalSchema.ytIsFullscreen.value = !GlobalSchema.ytIsFullscreen.value

            // Change the screen's orientation.
            // SOURCE: https://www.geeksforgeeks.org/android-jetpack-compose-change-the-screen-orientation-programmatically-using-a-button/
            val targetOrientation = if (GlobalSchema.ytIsFullscreen.value) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            (ctx as Activity).requestedOrientation = targetOrientation

            // Hides the phone's top and bottom bars.
            GlobalSchema.phoneBarsVisibility.value = !GlobalSchema.ytIsFullscreen.value
        }
    }

    @Composable
    @Stable
    fun YouTubeViewComposable() {
        // Display the video.
        AndroidView(factory = { GlobalSchema.ytView!! })
    }  // --- end of fun YouTubeViewComposable.

    @Composable
    fun initYouTubeView() {
        val ctx = LocalContext.current
        val lifecycle = LocalLifecycleOwner.current.lifecycle

        // Enable full screen button.
        // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#full-screen
        val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
            .controls(0)
            .fullscreen(0)
            .modestBranding(1)
            .build()

        // Embedding the YouTube video into the composable.
        // SOURCE: https://dev.to/mozeago/jetpack-compose-loadinghow-to-load-a-youtube-video-or-youtube-livestream-channel-to-your-android-application-4ffc
        val youtubeVideoID = GlobalSchema.ytViewerParameters["yt-id"]

        // Initialize the YouTubePlayer view.
        GlobalSchema.ytView = YouTubePlayerView(ctx)

        // Ensures that we don't play the YouTube video player in background
        // so that we can pass the Google Play Store screening.
        // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#lifecycleobserver
        GlobalSchema.ytView!!.enableBackgroundPlayback(false)

        // We need to initialize manually in order to pass IFramePlayerOptions to the player
        GlobalSchema.ytView!!.enableAutomaticInitialization = false

        // This destroys the video player upon exiting the activity.
        // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#lifecycleobserver
        lifecycle.addObserver(GlobalSchema.ytView!!)

        // Setup the player view.
        GlobalSchema.ytView!!.initialize(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    GlobalSchema.ytPlayer = youTubePlayer
                    super.onReady(youTubePlayer)

                    // Using a custom UI.
                    // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#defaultplayeruicontroller
                    val ytCustomController: DefaultPlayerUiController = DefaultPlayerUiController(
                        GlobalSchema.ytView!!, youTubePlayer)
                    ytCustomController.showYouTubeButton(false)
                    ytCustomController.setFullscreenButtonClickListener {
                        YouTubeView.handleFullscreenStateChange(ctx)
                    }

                    GlobalSchema.ytView!!.setCustomPlayerUi(ytCustomController.rootView)

                    // Loads and plays the video.
                    youTubePlayer.loadVideo(youtubeVideoID!!, GlobalSchema.ytCurrentSecond.floatValue)
                    youTubePlayer.addListener(GlobalSchema.ytTracker)
                }
            }, iFramePlayerOptions
        )
    }

}