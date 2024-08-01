/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load the live video URL passed by the navigation parameter.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema


class ScreenVideoLive : ComponentActivity() {

    // The trigger to open an URL in an external browser.
    private val doTriggerBrowserOpen = mutableStateOf(false)

    // Controls, from an outside composable, whether to display the link confirmation dialog.
    private val showLinkConfirmationDialog = mutableStateOf(false)

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        val ctx = LocalContext.current

        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ScreenVideoLive.getComposable] Are we full screen?: ${GlobalSchema.ytIsFullscreen.value}. Duration: ${GlobalSchema.ytCurrentSecond.floatValue}")

        // Opens a specific composable element based on the fullscreen state.
        if (GlobalSchema.ytIsFullscreen.value) {
            getFullscreenPlayer()

            // Exits the fullscreen mode.
            BackHandler {
                handleFullscreenStateChange(ctx)
            }
        } else {
            getNormalPlayer()

            // Ensures that we always land where we started.
            BackHandler {
                GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                GlobalSchema.ytView!!.release()
            }
        }

        // Disabling background YouTube playback.
        LaunchedEffect(GlobalSchema.isRunningInBackground.value) {
            if (GlobalSchema.isRunningInBackground.value) {
                try {
                    GlobalSchema.ytPlayer!!.pause()
                } catch (e: Exception) {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ScreenVideoLive.getNormalPlayer] Error detected when trying to pause the video: $e")
                }
            }
        }

        // Display the external link open confirm dialog.
        getLinkConfirmationDialog()

        // Handles opening URLs in external browser.
        key(doTriggerBrowserOpen.value) {
            if (doTriggerBrowserOpen.value) {
                // Opens in an external browser.
                // SOURCE: https://stackoverflow.com/a/69103918
                LocalUriHandler.current.openUri(GlobalSchema.ytViewerParameters["yt-link"]!!)
                doTriggerBrowserOpen.value = false
            }
        }
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
                    stringResource(R.string.screenlive_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                    GlobalSchema.ytView!!.release()
                }) { Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "") }
            },
            actions = {
                IconButton(onClick = {
                    showLinkConfirmationDialog.value = true
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.OpenInNew,
                        contentDescription = ""
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    /**
     * This function displays the confirmation dialog that asks the user
     * whether the user wants to proceed opening a certain link.
     * SOURCE: https://www.composables.com/tutorials/dialogs
     * SOURCE: https://developer.android.com/develop/ui/compose/components/dialog
     */
    @Composable
    private fun getLinkConfirmationDialog() {
        val ctx = LocalContext.current
        val notificationText = stringResource(R.string.yt_visit_link_link_copied)
        if (showLinkConfirmationDialog.value) {
            AlertDialog(
                onDismissRequest = { showLinkConfirmationDialog.value = false },
                title = { Text(stringResource(R.string.yt_visit_link_confirmation_title), fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                text = {
                    Column {
                        Text(stringResource(R.string.yt_visit_link_confirmation_subtitle) )
                        Surface (modifier = Modifier.fillMaxWidth(), color = Color.Transparent, onClick = {
                            // Attempt to copy text to clipboard.
                            // SOURCE: https://www.geeksforgeeks.org/clipboard-in-android/
                            val clipData = ClipData.newPlainText("text", GlobalSchema.ytViewerParameters["yt-link"])
                            GlobalSchema.clipManager!!.setPrimaryClip(clipData)

                            Toast.makeText(ctx, notificationText, Toast.LENGTH_SHORT).show()
                        }) {
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = GlobalSchema.ytViewerParameters["yt-link"]!!,
                                onValueChange = { /* NOTHING */ },
                                label = { Text("-") },
                                enabled = false,
                            )
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLinkConfirmationDialog.value = false }) {
                        Text(stringResource(R.string.yt_visit_link_cancel_btn).uppercase())
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showLinkConfirmationDialog.value = false
                        doTriggerBrowserOpen.value = true
                    }) {
                        Text(stringResource(R.string.yt_visit_link_proceed_btn).uppercase())
                    }
                }
            )
        }
    }

    /**
     * The normal YouTube player, which shows the video description.
     */
    @Composable
    private fun getNormalPlayer() {
        val ctx = LocalContext.current
        Scaffold (
            topBar = { if (!GlobalSchema.ytIsFullscreen.value) this.getTopBar() },
            /*floatingActionButton = {
                FloatingActionButton(onClick = {
                    isFullscreen.value = true
                    currentSecond.value = GlobalSchema.ytTracker.currentSecond
                }) {
                    Icon(Icons.Default.Fullscreen, "")
                }
            }*/
        ) {

            // Display the necessary content.
            Box ( modifier= Modifier
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                .background(color = colorResource(R.color.brown_900))
                .fillMaxHeight()) {
                Column {

                    getVideo()

                    //jeff 10.25
                    Spacer(Modifier.height(8.dp))
                    Text(GlobalSchema.ytViewerParameters["title"]!!,Modifier.absolutePadding(left = 20.dp, right = 20.dp), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color.White)
                    Text(GlobalSchema.ytViewerParameters["date"]!!, Modifier.absolutePadding(left = 20.dp, right = 20.dp), color = Color(0xfffcfcfc))
                    Spacer(Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .absolutePadding(left = 10.dp, right = 10.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .height(300.dp)
                            .background(color = colorResource(R.color.grey_1))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(GlobalSchema.ytViewerParameters["desc"]!!, Modifier.absolutePadding(left = 10.dp, right = 10.dp), fontSize = 14.sp, lineHeight = 1.em, color = Color.White)
                        }
                        // Add other composables inside the Box if needed
                    }
                }  // --- end of scrollable column.
            }  // --- end of box.
        }  // --- end of scaffold.
    }

    /**
     * The fullscreen player.
     */
    @Composable
    private fun getFullscreenPlayer() {
        /* The fullscreen canvas, rotated to landscape configuration. */
        Box (Modifier.fillMaxHeight().wrapContentSize().padding(45.dp)) {
            getVideo()
        }
    }

    /**
     * The YouTube video player.
     */
    @Composable
    private fun getVideo() {
        val ctx = LocalContext.current

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
        var view: YouTubePlayerView? = null
        AndroidView(factory = {
            GlobalSchema.ytView = YouTubePlayerView(it)

            // This destroys the video player upon exiting the activity.
            // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#lifecycleobserver
            lifecycle.addObserver(GlobalSchema.ytView!!)

            // Ensures that we don't play the YouTube video player in background
            // so that we can pass the Google Play Store screening.
            // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#lifecycleobserver
            GlobalSchema.ytView!!.enableBackgroundPlayback(false)

            // We need to initialize manually in order to pass IFramePlayerOptions to the player
            GlobalSchema.ytView!!.enableAutomaticInitialization = false

            // TODO: Remove this block. It makes the video player freeze.
            // Add the fullscreen listener.
            /*GlobalSchema.ytView!!.addFullscreenListener(object : FullscreenListener {
                override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ScreenVideoLive.getComposable] Entering full screen mode at time: ${GlobalSchema.ytTracker.currentSecond} ...")
                    GlobalSchema.ytIsFullscreen.value = true
                    GlobalSchema.ytCurrentSecond.floatValue = GlobalSchema.ytTracker.currentSecond
                }

                override fun onExitFullscreen() {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ScreenVideoLive.getComposable] Leaving full screen mode at time: ${GlobalSchema.ytTracker.currentSecond} ...")
                    GlobalSchema.ytIsFullscreen.value = false
                    GlobalSchema.ytCurrentSecond.floatValue = GlobalSchema.ytTracker.currentSecond
                }
            })*/

            // Initialize the YouTube player.
            // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player/blob/master/core-sample-app/src/main/java/com/pierfrancescosoffritti/androidyoutubeplayer/core/sampleapp/examples/fullscreenExample/FullscreenExampleActivity.kt
            GlobalSchema.ytView!!.initialize(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(youtubePlayer: YouTubePlayer) {
                        GlobalSchema.ytPlayer = youtubePlayer
                        super.onReady(youtubePlayer)

                        // Using a custom UI.
                        // SOURCE: https://github.com/PierfrancescoSoffritti/android-youtube-player?tab=readme-ov-file#defaultplayeruicontroller
                        val ytCustomController: DefaultPlayerUiController = DefaultPlayerUiController(GlobalSchema.ytView!!, youtubePlayer)
                        ytCustomController.showYouTubeButton(false)
                        ytCustomController.setFullscreenButtonClickListener {
                            handleFullscreenStateChange(ctx)
                        }

                        GlobalSchema.ytView!!.setCustomPlayerUi(ytCustomController.rootView)

                        // Loads and plays the video.
                        youtubePlayer.loadVideo(youtubeVideoID!!, GlobalSchema.ytCurrentSecond.floatValue)
                        youtubePlayer.addListener(GlobalSchema.ytTracker)

                        // Detect full screen change state.

                    }
                }, iFramePlayerOptions
            )

            // Display the video.
            GlobalSchema.ytView!!
        })
    }

    /**
     * This function handles the action when the "fullscreen" button is pressed.
     */
    private fun handleFullscreenStateChange(ctx: Context) {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ScreenVideoLive.handleFullscreenStateChange] Fullscreen button is clicked.")
        GlobalSchema.ytCurrentSecond.floatValue = GlobalSchema.ytTracker.currentSecond
        GlobalSchema.ytIsFullscreen.value = !GlobalSchema.ytIsFullscreen.value

        // Pause the video (temporarily).
        GlobalSchema.ytPlayer!!.pause()

        // Change the screen's orientation.
        // SOURCE: https://www.geeksforgeeks.org/android-jetpack-compose-change-the-screen-orientation-programmatically-using-a-button/
        val targetOrientation = if (GlobalSchema.ytIsFullscreen.value) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (ctx as Activity).requestedOrientation = targetOrientation

        // Hides the phone's top and bottom bars.
        GlobalSchema.phoneBarsVisibility.value = !GlobalSchema.ytIsFullscreen.value
    }

}