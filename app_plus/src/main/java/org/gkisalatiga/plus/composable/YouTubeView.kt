/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.composable

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.Logger

class YouTubeView {
    companion object {
        /**
         * This function handles the action when the "fullscreen" button is pressed.
         */
        fun handleFullscreenStateChange(ctx: Context) {
            Logger.log({}, "Fullscreen button is clicked.")
            YouTubeViewCompanion.currentSecond.floatValue = YouTubeViewCompanion.tracker.currentSecond
            YouTubeViewCompanion.isFullscreen.value = !YouTubeViewCompanion.isFullscreen.value

            // Change the screen's orientation.
            // SOURCE: https://www.geeksforgeeks.org/android-jetpack-compose-change-the-screen-orientation-programmatically-using-a-button/
            val targetOrientation = if (YouTubeViewCompanion.isFullscreen.value) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            (ctx as Activity).requestedOrientation = targetOrientation

            // Hides the phone's top and bottom bars.
            GlobalCompanion.isPhoneBarsVisible.value = !YouTubeViewCompanion.isFullscreen.value
        }
    }

    @Composable
    @Stable
    fun YouTubeViewComposable() {
        // Display the video.
        AndroidView(factory = { YouTubeViewCompanion.view!! })
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
        val youtubeVideoID = YouTubeViewCompanion.videoId

        // Initialize the YouTubePlayer view.
        YouTubeViewCompanion.view = YouTubePlayerView(ctx)

        // Ensures that we don't play the YouTube video player in background
        // so that we can pass the Google Play Store screening.
        // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#lifecycleobserver
        YouTubeViewCompanion.view!!.enableBackgroundPlayback(false)

        // We need to initialize manually in order to pass IFramePlayerOptions to the player
        YouTubeViewCompanion.view!!.enableAutomaticInitialization = false

        // This destroys the video player upon exiting the activity.
        // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#lifecycleobserver
        lifecycle.addObserver(YouTubeViewCompanion.view!!)

        // Setup the player view.
        YouTubeViewCompanion.view!!.initialize(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    YouTubeViewCompanion.player = youTubePlayer
                    super.onReady(youTubePlayer)

                    // Using a custom UI.
                    // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#defaultplayeruicontroller
                    val ytCustomController: DefaultPlayerUiController = DefaultPlayerUiController(
                        YouTubeViewCompanion.view!!, youTubePlayer)
                    ytCustomController.showYouTubeButton(false)
                    ytCustomController.setFullscreenButtonClickListener {
                        handleFullscreenStateChange(ctx)
                    }

                    YouTubeViewCompanion.view!!.setCustomPlayerUi(ytCustomController.rootView)

                    // Loads and plays the video.
                    youTubePlayer.loadVideo(youtubeVideoID!!, YouTubeViewCompanion.currentSecond.floatValue)
                    youTubePlayer.addListener(YouTubeViewCompanion.tracker)
                }
            }, iFramePlayerOptions
        )
    }

}

class YouTubeViewCompanion : Application() {
    companion object {
        internal var videoUploadDate: String = ""
        internal var videoDesc: String = ""
        internal var videoId: String = ""
        internal var videoThumbnail: String = ""
        internal var videoTitle: String = ""
        internal var videoUrl: String = ""

        /* The composable YouTube element. */
        var composable: YouTubeView? = null

        /* The YouTube player object. */
        var player: YouTubePlayer? = null

        /* The global YouTube tracker. */
        val tracker: YouTubePlayerTracker = YouTubePlayerTracker()

        /* The global YouTubeViewer element. */
        var view: YouTubePlayerView? = null

        /* The YouTube video player states. */
        val isFullscreen = mutableStateOf(false)
        val currentSecond = mutableFloatStateOf(0.0f)

        /**
         * This function neatly and thoroughly passes the respective arguments to the screen's handler.
         * @param date the date of the video to be shown
         * @param desc the description of the video to be shown
         * @param thumbnail the thumbnail of the video to be shown
         * @param title the title of the video to be shown
         * @param yt_id the video ID of the video to be shown
         * @param yt_link the URL of the video to be shown
         */
        fun putArguments(date: String, desc: String, thumbnail: String, title: String, yt_id: String, yt_link: String) {
            videoUploadDate = date
            videoDesc = desc
            videoId = yt_id
            videoThumbnail = thumbnail
            videoTitle = title
            videoUrl = yt_link
        }

        /**
         * Sets the "currentSecond" value of the YouTube video viewer to "0.0" (the beginning).
         */
        fun seekToZero() {
            currentSecond.floatValue = 0.0f
        }
    }
}