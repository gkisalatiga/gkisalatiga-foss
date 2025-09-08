/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 *
 * AsyncImage.
 * SOURCE: https://coil-kt.github.io/coil/compose/
 */

package org.gkisalatiga.plus.fragment

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.GalleryAlbumObject
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.screen.ScreenGaleriListCompanion
import org.json.JSONArray
import org.json.JSONObject

class FragmentSeasonalGaleri (private val current : ActivityData) : ComponentActivity() {

    // The root seasonal node.
    // private val seasonalNode = ModulesCompanion.jsonRoot!!.getJSONObject("seasonal")
    private val seasonalNode = ModulesCompanion.api!!.seasonal

    @Composable
    fun getComposable() {

        // Enumerate the list of gallery albums that correspond to the seasonal keyword.
        val seasonalGalleryKeyword = ModulesCompanion.api!!.seasonal.staticMenu.gallery.albumKeyword.let { if (it.isNullOrBlank()) "" else it }
        /*val seasonalGalleryKeyword = ModulesCompanion.jsonRoot!!
            .getJSONObject("seasonal")
            .getJSONObject("static-menu")
            .getJSONObject("gallery")
            .getString("album-keyword")*/
        val filteredGalleryList = mutableListOf<GalleryAlbumObject>()
        for (i in 0 until GalleryCompanion.api!!.size ) {
            GalleryCompanion.api!![i].albumData.let {
                for (j in 0 until it.size) {
                    it[j].let { o -> if (o.title.contains(seasonalGalleryKeyword)) filteredGalleryList.add(o) }
                }
            }
        }

        Column {
            // Display the main title.
            // val mainTitle = seasonalNode.getJSONObject("static-menu").getJSONObject("gallery").getString("title")
            val mainTitle = seasonalNode.staticMenu.gallery.title
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(mainTitle, modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(20.dp))
            Logger.logDump({}, filteredGalleryList.toString())
            filteredGalleryList.forEach {
                // Determining the text title.
                val title = it.title

                // Determining the featured image ID.
                val featuredImageID = it.photos[0].id

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "Opening gallery album year: $title", Toast.LENGTH_SHORT).show()

                        // Navigate to the WebView viewer.
                        ScreenGaleriListCompanion.putArguments(
                            albumTitle = title,
                            albumStory = it.story,
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
                }
            }
        }
    }

}
