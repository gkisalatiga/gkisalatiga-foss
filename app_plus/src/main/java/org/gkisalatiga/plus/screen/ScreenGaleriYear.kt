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
import android.app.Application
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.GalleryAlbumObject
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONArray
import org.json.JSONObject

class ScreenGaleriYear (private val current : ActivityData) : ComponentActivity() {

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
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
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    @Suppress("RemoveCurlyBracesFromTemplate")
    @SuppressLint("ComposableNaming")
    private fun getMainContent() {

        // The agenda node.
        val galleryNode = GalleryCompanion.api!!

        // Enlist the list of albums in the currently selected year.
        val galleryYearList = ScreenGaleriCompanion.targetGalleryAlbumData

        // DEBUG. Always comment out.
        Logger.logTest({}, "Current object (1): ${galleryYearList}")

        // The column's saved scroll state.
        val scrollState = ScreenGaleriYearCompanion.rememberedScrollState!!
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize()
        ) {
            // Display the main content.
            Column (Modifier.fillMaxSize().padding(20.dp)) {

                Logger.logTest({}, "Current object (3): ${galleryYearList}")

                /* Draw the form selection elements. */
                galleryYearList.forEach {
                    Logger.logTest({}, "Current object (4): ${it}")

                    // Determining the text title.
                    val title = it.title.toString()

                    // Determining the featured image ID.
                    val featuredImageID = it.photos[0].id

                    // Displaying the individual card.
                    Card(
                        onClick = {
                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "Opening gallery album year: $title", Toast.LENGTH_SHORT).show()

                            // Navigate to the WebView viewer.
                            ScreenGaleriListCompanion.putArguments(
                                albumTitle = title,
                                albumStory = it.story.toString(),
                                featuredImageId = featuredImageID,
                                albumContent = it.photos
                            )
                            AppNavigation.navigate(NavigationRoutes.SCREEN_GALERI_LIST)
                        },
                        modifier = Modifier.padding(bottom = 10.dp).height(65.dp)
                    ) {
                        Row ( modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically ) {
                            AsyncImage(
                                model = StringFormatter.getGoogleDriveThumbnail(featuredImageID, 160),
                                contentDescription = title,
                                error = painterResource(R.drawable.thumbnail_error),
                                placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                modifier = Modifier.fillMaxSize().weight(2f),
                                contentScale = ContentScale.Crop
                            )
                            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Normal, modifier = Modifier.padding(horizontal = 10.dp).weight(7.5f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }  // --- end of card.
                }  // --- end of forEach.

            }  // --- end of column (2).
        }  // --- end of column (1).

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @Suppress("SpellCheckingInspection", "RedundantSuppression")
    @SuppressLint("ComposableNaming")
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val topBarTitle = ScreenGaleriCompanion.targetGalleryTitle
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    topBarTitle,
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

@Suppress("SpellCheckingInspection")
class ScreenGaleriYearCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}