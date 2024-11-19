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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONArray
import org.json.JSONObject

class ScreenGaleri : ComponentActivity() {

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
    @SuppressLint("ComposableNaming")
    private fun getMainContent() {
        val ctx = LocalContext.current

        // The gallery node.
        val galleryNode = GalleryCompanion.jsonRoot!!

        // Enlist the list galleries, the top parent (root) album.
        val galleryAlbumRoots = mutableListOf(JSONObject())
        for (i in 0 until galleryNode.length() ) galleryAlbumRoots.add(i, galleryNode.getJSONObject(i))
        galleryAlbumRoots.sortBy { if (it.toString() != "{}") it.getString("title") else "" }

        // The column's saved scroll state.
        val scrollState = ScreenGaleriCompanion.rememberedScrollState!!
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize().padding(20.dp)
        ) {
            /* Display the church's building image. */
            val imgSource = R.drawable.banner_gallery
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

            /* Draw the form selection elements. */
            galleryAlbumRoots.forEach {
                // Ensures we only select non-empty JSON objects.
                if (it.toString() != "{}") {
                    // Determining the text title.
                    val title = it.getString("title")

                    // Displaying the individual card.
                    Card(
                        onClick = {
                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "Opening gallery folder year: $title", Toast.LENGTH_SHORT).show()

                            // Navigate to the WebView viewer.
                            ScreenGaleriCompanion.targetGalleryTitle = title
                            ScreenGaleriCompanion.targetGalleryAlbumData = it.getJSONArray("album-data")
                            AppNavigation.navigate(NavigationRoutes.SCREEN_GALERI_YEAR)
                        },
                        modifier = Modifier.padding(bottom = 10.dp).height(65.dp)
                    ) {
                        Row ( modifier = Modifier.padding(5.dp).fillMaxSize().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically ) {
                            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Normal, modifier = Modifier.padding(start = 5.dp).weight(5f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                            // The "arrow forward" icon.
                            Icon(Icons.AutoMirrored.Default.ArrowForward, "", modifier = Modifier.padding(vertical = 5.dp).padding(end = 5.dp).padding(start = 10.dp).fillMaxHeight())
                        }
                    }  // --- end of card.
                }
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
                    stringResource(R.string.screengallery_title),
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
class ScreenGaleriCompanion : Application() {
    companion object {
        internal var targetGalleryTitle: String = ""
        internal var targetGalleryAlbumData: JSONArray = JSONArray()

        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}