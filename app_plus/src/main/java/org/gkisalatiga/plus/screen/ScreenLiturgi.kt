/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the service liturgy.
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.MainPTR
import org.gkisalatiga.plus.composable.MainPTRCompanion
import org.gkisalatiga.plus.composable.OfflineSnackbarHost
import org.gkisalatiga.plus.composable.OfflineSnackbarHostCompanion
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONObject

class ScreenLiturgi : ComponentActivity() {

    // The snackbar host state.
    private val snackbarHostState = OfflineSnackbarHostCompanion.snackbarHostState

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()

        // The pull-to-refresh indicator states.
        val isRefreshing = remember { MainPTRCompanion.isPTRRefreshing }
        val pullToRefreshState = MainPTRCompanion.mainPTRState

        Scaffold (
            topBar = { getTopBar() },
            snackbarHost = { OfflineSnackbarHost() },
            modifier = Modifier.pullToRefresh(isRefreshing.value, pullToRefreshState!!, onRefresh = {
                MainPTRCompanion.launchOnRefresh(ctx)
            })) {
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }

            // Check whether we are connected to the internet.
            // Then notify user about this.
            val snackbarMessageString = stringResource(R.string.not_connected_to_internet)
            LaunchedEffect(GlobalCompanion.isConnectedToInternet.value) {
                if (!GlobalCompanion.isConnectedToInternet.value) scope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarMessageString,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            // Add pull-to-refresh mechanism for updating the content data.
            MainPTR(it.calculateTopPadding())

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
        val scrollState = ScreenLiturgiCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            /* Display the banner image. */
            val imgSource = R.drawable.banner_liturgi
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

            /* Retrieve the list of liturgies. */
            val formListAsJSONArray = MainCompanion.jsonRoot!!.getJSONObject("pdf").getJSONArray("liturgi")

            /* Enumerate and enlist the individual card. */
            val enumeratedContentList: MutableList<Map<String, String>> =  mutableListOf(emptyMap<String, String>())
            for (i in 0 until formListAsJSONArray.length()) {
                val curNode = formListAsJSONArray[i] as JSONObject
                enumeratedContentList.add(mapOf(
                    "title" to curNode.getString("title"),
                    "date" to curNode.getString("date"),
                    "link" to curNode.getString("link"),
                    "thumbnail" to curNode.getString("thumbnail"),
                    "post-page" to curNode.getString("post-page"),
                    "size" to curNode.getString("size")
                ))
            }

            // For some reason, we must pop the 0-th item in cardsList
            // because JSONArray iterates from 1, not 0.
            enumeratedContentList.removeAt(0)

            /* Draw the news selection elements. */
            enumeratedContentList.forEach {

                // Preparing the arguments.
                val title = it["title"]!!
                val urlId = StringFormatter.getGoogleDriveId( it["link"]!! )

                // Let's obtain the download URL.
                val url = StringFormatter.getGoogleDriveDownloadURL(urlId)

                // Additional information of the PDF not present in the remote JSON main data.
                val author = "Gereja Kristen Indonesia Salatiga"
                val publisher = "Gereja Kristen Indonesia Salatiga"
                val publisherLoc = "Kota Salatiga, Jawa Tengah 50742"
                val year = it["date"]!!.split('-')[0]
                val thumbnail = it["thumbnail"]!!
                val source = it["post-page"]!!
                val size = it["size"]!!

                // Whether this PDF has been downloaded.
                val isDownloaded = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                        // Navigate to the PDF viewer.
                        ScreenPDFViewerCompanion.putArguments(title, author, publisher, publisherLoc, year, thumbnail, url, source, size)
                        AppNavigation.navigate(NavigationRoutes.SCREEN_PDF_VIEWER)
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Column ( modifier = Modifier.fillMaxWidth().padding(8.dp), verticalArrangement = Arrangement.Center ) {
                        Row {
                            // The first post thumbnail.
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.0f).fillMaxWidth()
                            ) {
                                AsyncImage(
                                    thumbnail,
                                    contentDescription = "Liturgy Book: $title",
                                    error = painterResource(R.drawable.menu_cover_liturgi),
                                    placeholder = painterResource(R.drawable.thumbnail_placeholder_vert_notext),
                                    modifier = Modifier.width(14.dp),
                                    contentScale = ContentScale.FillWidth
                                )
                            }
                            Column(
                                modifier = Modifier.weight(5.0f).padding(start = 10.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                // The publication title.
                                Text(
                                    title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // The file size.
                                Row {
                                    Icon(Icons.Default.Download, "File download size icon", modifier = Modifier.scale(0.8f).padding(end = 5.dp))
                                    Text(
                                        size,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                // The downloaded PDF badge.
                                val isDownloadedTitle = stringResource(R.string.pdf_already_downloaded_localized)
                                val badgeColor = Color(Colors.MAIN_PDF_DOWNLOADED_BADGE_COLOR)
                                if (isDownloaded) {
                                    Row {
                                        Icon(Icons.Default.CheckCircle, "File downloaded icon", modifier = Modifier.scale(0.8f).padding(end = 5.dp), tint = badgeColor)
                                        Text(
                                            isDownloadedTitle,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = badgeColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }  // --- end of Card.
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
                    stringResource(R.string.screenliturgi_title),
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

class ScreenLiturgiCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}