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
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONArray
import org.json.JSONObject

class ScreenYKBList : ComponentActivity() {

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

        // Setting the layout to center both vertically and horizontally,
        // and then make it scrollable vertically.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = ScreenYKBListCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            /* Display the banner image. */
            val imgSource = ScreenYKBListCompanion.targetYKBArchiveBannerUrl
            val imgDescription = "YKB archival list banner"
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.aspectRatio(1.0f)
            ) {
                AsyncImage(
                    model = imgSource,
                    contentDescription = imgDescription,
                    modifier = Modifier.fillMaxWidth(),
                    error = painterResource(R.drawable.thumbnail_error),
                    placeholder = painterResource(R.drawable.thumbnail_placeholder),
                    contentScale = ContentScale.Crop
                )
            }

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            /* Retrieve the list of devotionals. */
            val ykbListAsJSONArray = ScreenYKBListCompanion.targetYKBArchiveList!!

            /* Enumerate and enlist the individual card. */
            val enumeratedYKBList: MutableList<JSONObject> =  mutableListOf()
            for (i in 0 until ykbListAsJSONArray.length()) {
                enumeratedYKBList.add(ykbListAsJSONArray[i] as JSONObject)
            }

            /* Draw the devotional selection elements. */
            enumeratedYKBList.forEach {

                // Preparing the arguments.
                val title = it.getString("title")
                val thumbnail = it.getString("featured-image")
                val date = StringFormatter.convertDateFromJSON(it.getString("date"))
                val html = it.getString("html")

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $title", Toast.LENGTH_SHORT).show()

                        // Navigate to the WebView viewer.
                        ScreenInternalHTMLCompanion.internalWebViewTitle = ScreenYKBListCompanion.screenYKBListTitle
                        ScreenInternalHTMLCompanion.targetHTMLContent = html

                        // Pushing the screen.
                        AppNavigation.navigate(NavigationRoutes.SCREEN_INTERNAL_HTML)
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Column ( modifier = Modifier.fillMaxWidth().padding(10.dp).padding(top = 5.dp), verticalArrangement = Arrangement.Center ) {
                        Row {
                            // The first post thumbnail.
                            Surface (shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1.0f).fillMaxHeight().padding(start = 5.dp)) {
                                AsyncImage(
                                    if (thumbnail == "") ScreenYKBListCompanion.targetYKBArchiveBannerUrl else thumbnail,
                                    contentDescription = "",
                                    error = painterResource(R.drawable.thumbnail_error),
                                    placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                    modifier = Modifier.aspectRatio(1f).width(12.5.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.weight(5.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Center) {
                                // The post title.
                                Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                // The post date.
                                Text(date, fontSize = 18.sp, fontWeight = FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    ScreenYKBListCompanion.screenYKBListTitle,
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

class ScreenYKBListCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* The screen's target name. */
        var screenYKBListTitle: String = String()

        /* The JSONArray which enlists the devotional posts to display in this screen. */
        var targetYKBArchiveList: JSONArray? = null
        var targetYKBArchiveBannerUrl: String = String()
    }
}