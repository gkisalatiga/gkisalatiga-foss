/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display meta-application information about GKI Salatiga Plus.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ForwardToInbox
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.services.NotificationService
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream


class ScreenStaticContentList : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        val ctx = LocalContext.current

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
        }

    }

    @Composable
    private fun getMainContent() {
        val ctx = LocalContext.current

        // The selected static folder's folder content.
        val folderContent = GlobalSchema.targetStaticFolder!!

        // Enlist the list of contents under this folder.
        val contentList: MutableList<JSONObject> = mutableListOf()
        for (i in 0 until folderContent.getJSONArray("content").length()) {
            contentList.add(folderContent.getJSONArray("content")[i] as JSONObject)
        }

        // The column's saved scroll state.
        val scrollState = rememberScrollState()
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize()
        ) {
            // Display the main content.
            Column (Modifier.fillMaxSize().padding(20.dp)) {

                /* Draw the form selection elements. */
                contentList.forEach {
                    // Determining the text title, among other things.
                    val title = it.getString("title")
                    val subtitle = it.getString("subtitle")
                    var featuredImageURL = it.getString("featured-image")

                    // For some reason, coil cannot render non-HTTPS images.
                    if (featuredImageURL.startsWith("http://")) featuredImageURL = featuredImageURL.replaceFirst("http://", "https://")

                    // Displaying the individual card.
                    Card(
                        onClick = {
                            if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $title!", Toast.LENGTH_SHORT).show()

                            // Set this screen as the anchor point for "back"
                            GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_STATIC_CONTENT_LIST
                            GlobalSchema.popBackDoubleScreen.value = NavigationRoutes().SCREEN_MAIN

                            // Display the church profile content folder list.
                            GlobalSchema.targetHTMLContent = it.getString("html")
                            GlobalSchema.internalWebViewTitle = it.getString("title")
                            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_INTERNAL_HTML
                        },

                        modifier = Modifier.padding(bottom = 10.dp).aspectRatio(2.4f).fillMaxWidth()
                    ) {

                        // Displaying the text-overlaid image.
                        Box {
                            /* The background featured image. */
                            // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/customize
                            // ---
                            val contrast = 1.1f  // --- 0f..10f (1 should be default)
                            val brightness = 0.0f  // --- -255f..255f (0 should be default)
                            AsyncImage(
                                model = featuredImageURL,
                                contentDescription = "Profile page content: $title",
                                error = painterResource(R.drawable.thumbnail_loading_no_text),
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Crop,
                                colorFilter = ColorFilter.colorMatrix(
                                    ColorMatrix(
                                    floatArrayOf(
                                        contrast, 0f, 0f, 0f, brightness,
                                        0f, contrast, 0f, 0f, brightness,
                                        0f, 0f, contrast, 0f, brightness,
                                        0f, 0f, 0f, 1f, 0f
                                    )
                                )
                                )
                            )

                            /* Add shadow-y overlay background so that the white text becomes more visible. */
                            // SOURCE: https://developer.android.com/develop/ui/compose/graphics/draw/brush
                            // SOURCE: https://stackoverflow.com/a/60479489
                            Box (
                                modifier = Modifier
                                    // Color pattern: 0xAARRGGBB (where "AA" is the alpha value).
                                    .background(Color(0x40fda308))
                                    .matchParentSize()
                            )

                            /* The card description label. */
                            Column (horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = title,
                                    fontSize = 22.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 5.dp),
                                    style = TextStyle(
                                        shadow = Shadow(Color.Black, Offset(3.0f, 3.0f), 8.0f)
                                    )
                                )

                                if (!subtitle.isBlank() && subtitle != "-") {
                                    Text(
                                        text = subtitle,
                                        fontSize = 16.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        style = TextStyle(
                                            shadow = Shadow(Color.Black, Offset(3.0f, 3.0f), 8.0f)
                                        )
                                    )
                                }

                                Spacer(Modifier.padding(bottom = 20.dp))
                            }  // --- end of text column.
                        }  // --- end of box.

                    }  // --- end of card.
                }  // --- end of forEach.

            }  // --- end of column (2).
        }  // --- end of column (1).
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
                    GlobalSchema.targetStaticFolder!!.getString("title"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
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