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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.AndroidView
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.NavigationRoutes
import java.io.ByteArrayOutputStream

class ScreenInternalHTML() : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        Scaffold (
            topBar = { this.getTopBar() }
                ) {

            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
            GlobalSchema.popBackScreen.value = GlobalSchema.popBackDoubleScreen.value
        }

    }

    @Composable
    @SuppressLint("SetJavaScriptEnabled", "ComposableNaming")
    private fun getMainContent() {

        // Declare the HTML code to display in the viewer.
        var HTMLBody = GlobalSchema.targetHTMLContent

        // Allow for displaying mixed-content images.
         HTMLBody = HTMLBody.replace("http://", "https://")

        // Create the encoded HTML body.
        // SOURCE: https://developer.android.com/develop/ui/views/layout/webapps/load-local-content#loadDataWithBaseUrl
        val encodedHTMLBody = Base64.encodeToString(HTMLBody.toByteArray(), Base64.NO_PADDING)

        /* Displaying the web view.
         * SOURCE: https://medium.com/@kevinnzou/using-webview-in-jetpack-compose-bbf5991cfd14 */
        // Adding a WebView inside AndroidView with layout as full screen
        AndroidView(factory = {
            WebView(it).apply {
                val wv = this
                wv.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Disable text selection and haptic feedback caused by long press.
                // This also disables copy-pasting of HTML text.
                // SOURCE: https://stackoverflow.com/a/12793740
                wv.setOnLongClickListener(View.OnLongClickListener { true })
                wv.isLongClickable = false
                wv.isHapticFeedbackEnabled = false

                // Enables JavaScript.
                // SOURCE: https://stackoverflow.com/a/69373543
                wv.settings.javaScriptEnabled = true
            }
        }, update = {
            // Load the local HTML content.
            it.loadData(encodedHTMLBody, "text/html", "base64")
        })

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
                    GlobalSchema.internalWebViewTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                    GlobalSchema.popBackScreen.value = GlobalSchema.popBackDoubleScreen.value
                }) {
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