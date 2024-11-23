/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the e-books.
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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Update
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONObject


class ScreenLibrary : ComponentActivity() {

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        Scaffold (topBar = { getTopBar() }) {

            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
                    .verticalScroll(ScreenLibraryCompanion.rememberedScrollState!!)
            ) {
                // Display the main "attribution" contents.
                getMainContent()
            }

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    @SuppressLint("ComposableNaming")
    private fun getMainContent() {
        val ctx = LocalContext.current
        val libraryAsJSONArray = ModulesCompanion.jsonRoot!!.getJSONArray("library")

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize().padding(20.dp)
        ) {

            /* Display the banner image. */
            val imgSource = R.drawable.banner_perpus
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

            /* Enumerate and enlist the individual card. */
            val enumeratedLibraryList: MutableList<Map<String, Any>> =  mutableListOf(emptyMap<String, Any>())
            for (i in 0 until libraryAsJSONArray.length()) {
                val curNode = libraryAsJSONArray[i] as JSONObject

                // Obtaining the maps of the JSONObject.
                val temporaryMutableMap = mutableMapOf<String, Any>()
                for (key in curNode.keys()) temporaryMutableMap[key] = curNode.getString(key)

                // Assigning the temporary map into the enumerated list.
                enumeratedLibraryList.add(temporaryMutableMap)
            }

            // For some reason, we must pop the 0-th item in cardsList
            // because JSONArray iterates from 1, not 0.
            enumeratedLibraryList.removeAt(0)

            /* Composing each entry. */
            enumeratedLibraryList.forEach {

                // Preparing the arguments.
                val title = it["title"] as String
                val author = it["author"] as String
                val publisher = it["publisher"] as String
                val publisherLoc = it["publisher-loc"] as String
                val year = it["year"] as String
                val thumbnail = it["thumbnail"] as String
                val url = it["download-url"] as String
                val source = it["source"] as String
                val size = it["size"] as String

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
                                    contentDescription = "Library Book: $title",
                                    error = painterResource(R.drawable.thumbnail_error_vert_notext),
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
                                // The publication author.
                                Row {
                                    Icon(Icons.Default.Group, "Publication author icon", modifier = Modifier.scale(0.8f).padding(end = 5.dp))
                                    Text(
                                        author,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Italic,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                // The publication date.
                                val publishedDateLabel = stringResource(R.string.library_published_year_label)
                                Row {
                                    Icon(Icons.Default.Update, "Publication year icon", modifier = Modifier.scale(0.8f).padding(end = 5.dp))
                                    Text(
                                        "$publishedDateLabel $year",
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
                                } else {
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
                                }
                            }
                        }
                    }
                }  // --- end of Card.

            }  // --- end of forEach.

        }  // --- end of column.
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("ComposableNaming")
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screenlibrary_title),
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

class ScreenLibraryCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}