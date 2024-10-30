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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONArray
import org.json.JSONObject

class ScreenYKB : ComponentActivity() {

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
        val ctx = LocalContext.current
        val scrollState = ScreenYKBCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            /* Display the banner image. */
            val imgSource = R.drawable.banner_ykb
            val imgDescription = "Menu banner"
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.aspectRatio(1.77778f)
            ) {
                Image(
                    painter = painterResource(imgSource),
                    contentDescription = imgDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            /* Retrieve the list of devotionals. */
            val ykbListAsJSONArray = MainCompanion.jsonRoot!!.getJSONArray("ykb")

            /* Enumerate and enlist the individual card. */
            val enumeratedYKBList: MutableList<Map<String, Any>> =  mutableListOf(emptyMap<String, Any>())
            for (i in 0 until ykbListAsJSONArray.length()) {
                val curNode = ykbListAsJSONArray[i] as JSONObject
                enumeratedYKBList.add(mapOf(
                    "title" to curNode.getString("title"),
                    "url" to curNode.getString("url"),
                    "posts" to curNode.getJSONArray("posts")
                ))
            }

            // For some reason, we must pop the 0-th item in cardsList
            // because JSONArray iterates from 1, not 0.
            enumeratedYKBList.removeAt(0)

            /* Draw the devotional selection elements. */
            enumeratedYKBList.forEach {

                // The first post of this devotion's list.
                val postsList = it["posts"] as JSONArray
                val firstPostObject = postsList.get(0) as JSONObject

                // Preparing the arguments.
                val title = it["title"] as String
                val url = it["url"] as String
                val firstPostThumbnail = firstPostObject.getString("featured-image")
                val firstPostTitle = firstPostObject.getString("title")
                val firstPostDate = StringFormatter.convertDateFromJSON(firstPostObject.getString("date"))
                val firstPostHTML = firstPostObject.getString("html")

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                        // Navigate to the WebView viewer.
                        ScreenInternalHTMLCompanion.internalWebViewTitle = title
                        ScreenInternalHTMLCompanion.targetHTMLContent = firstPostHTML
                        AppNavigation.navigate(NavigationRoutes.SCREEN_INTERNAL_HTML)
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Column ( modifier = Modifier.fillMaxWidth().padding(10.dp).padding(top = 5.dp), verticalArrangement = Arrangement.Center ) {
                        Row {
                            // The first post thumbnail.
                            Surface (shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1.0f).fillMaxHeight().padding(start = 5.dp)) {
                                AsyncImage(
                                    firstPostThumbnail,
                                    contentDescription = "YKB: $title",
                                    error = painterResource(R.drawable.thumbnail_loading_stretched),
                                    modifier = Modifier.aspectRatio(1f).width(12.5.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.weight(5.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Center) {
                                // The post title.
                                Text(firstPostTitle, fontSize = 21.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                // The post date.
                                Text(firstPostDate, fontSize = 18.sp, fontWeight = FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        // The devotion title + archive button.
                        TextButton(
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(Colors.YKB_ARCHIVE_BUTTON_COLOR)
                            ),
                            onClick = {
                                // Set the content list.
                                ScreenYKBListCompanion.targetYKBArchiveList = postsList
                                ScreenYKBListCompanion.screenYKBListTitle = title

                                // Set the navigation.
                                AppNavigation.navigate(NavigationRoutes.SCREEN_YKB_LIST)
                            }
                        ) {
                            Row (verticalAlignment = Alignment.CenterVertically) {
                                Text(title.uppercase())
                                Icon(Icons.AutoMirrored.Default.ArrowRight, "", modifier = Modifier.padding(start = 5.dp))
                            }
                        }
                    }
                }

            }

        }  // --- end of column.
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
                    stringResource(R.string.screenykb_title),
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

class ScreenYKBCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}