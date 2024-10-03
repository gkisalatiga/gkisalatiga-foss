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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.YouTubeView
import org.gkisalatiga.plus.global.GlobalSchema


class ScreenVideoLive : ComponentActivity() {

    // The trigger to open an URL in an external browser.
    private val doTriggerBrowserOpen = mutableStateOf(false)

    // Controls, from an outside composable, whether to display the link confirmation dialog.
    private val showLinkConfirmationDialog = mutableStateOf(false)

    // The calculated top bar padding.
    private var calculatedTopPadding: Dp = 0.dp

    // The coroutine scope.
    private lateinit var scope: CoroutineScope

    // The snackbar host state.
    private val snackbarHostState = GlobalSchema.snackbarHostState

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        val ctx = LocalContext.current
        scope = rememberCoroutineScope()

        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ScreenVideoLive.getComposable] Are we full screen?: ${GlobalSchema.ytIsFullscreen.value}. Duration: ${GlobalSchema.ytCurrentSecond.floatValue}")

        // Ensures that we only initialize the ytView once.
        GlobalSchema.ytComposable!!.initYouTubeView()

        // Opens a specific composable element based on the fullscreen state.
        if (GlobalSchema.ytIsFullscreen.value) {
            getFullscreenPlayer()

            // Exits the fullscreen mode.
            BackHandler {
                YouTubeView.handleFullscreenStateChange(ctx)
            }
        } else {
            getNormalPlayer()

            // Ensures that we always land where we started.
            BackHandler {
                GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                GlobalSchema.ytView!!.release()
            }
        }

        // Check whether we are connected to the internet.
        // Then notify user about this.
        val snackbarMessageString = stringResource(R.string.not_connected_to_internet)
        LaunchedEffect(GlobalSchema.isConnectedToInternet.value) {
            if (!GlobalSchema.isConnectedToInternet.value) scope.launch {
                snackbarHostState.showSnackbar(
                    message = snackbarMessageString,
                    duration = SnackbarDuration.Short
                )
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
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) {
            calculatedTopPadding = it.calculateTopPadding()

            // Display the necessary content.
            Box ( modifier= Modifier
                .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                .background(color = colorResource(R.color.brown_900))
                .fillMaxHeight()) {
                Column {

                    GlobalSchema.ytComposable!!.YouTubeViewComposable()

                    Column (Modifier.verticalScroll(rememberScrollState())) {
                        //jeff 10.25
                        Spacer(Modifier.height(15.dp))
                        Text(GlobalSchema.ytViewerParameters["title"]!!,Modifier.absolutePadding(left = 20.dp, right = 20.dp), fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color.White)
                        Text("Diunggah pada " + GlobalSchema.ytViewerParameters["date"]!!, Modifier.absolutePadding(left = 20.dp, right = 20.dp), color = Color(0xfffcfcfc), fontSize = 16.sp)
                        Spacer(Modifier.height(15.dp))

                        // the video desc
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .absolutePadding(left = 10.dp, right = 10.dp)
                                .padding(bottom = 20.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .background(color = colorResource(R.color.grey_1))
                                .fillMaxSize()
                                .wrapContentSize()
                        ) {
                            Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Top) {
                                Text(
                                    GlobalSchema.ytViewerParameters["desc"]!!,
                                    Modifier
                                        .absolutePadding(left = 10.dp, right = 10.dp)
                                        .fillMaxWidth(),
                                    fontSize = 14.sp,
                                    lineHeight = 1.em,
                                    color = Color.White,
                                    textAlign = TextAlign.Left
                                )
                            }
                            // Add other composables inside the Box if needed.
                        }
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
        Column (
            modifier = Modifier.background(Color.Black).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface (Modifier.aspectRatio(1.77778f)) { GlobalSchema.ytComposable!!.YouTubeViewComposable() }
        }
    }

}