/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.fdroid.fragment

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.gkisalatiga.fdroid.R
import org.gkisalatiga.fdroid.global.GlobalSchema
// import coil.compose.AsyncImage
import org.gkisalatiga.fdroid.lib.NavigationRoutes
import org.gkisalatiga.fdroid.lib.StringFormatter

class FragmentGalleryList : ComponentActivity() {

    @Composable
    fun getComposable() {
        /* Extract JSONArray to regular list. As always, JSONArray starts at 1. */
        val extractedAlbumContent = mutableListOf(mapOf<String, String>())
        for (i in 0 until GlobalSchema.targetAlbumContent!!.length()) {
            val curItem = GlobalSchema.targetAlbumContent!!.getJSONObject(i)
            extractedAlbumContent.add(
                mapOf<String, String> (
                    "date" to curItem.getString("date"),
                    "id" to curItem.getString("id"),
                    "name" to curItem.getString("name")
                )
            )
        }
        extractedAlbumContent.removeAt(0)

        /* Displaying the thumbnails. */
        Box (Modifier.fillMaxSize().padding(10.dp)) {
            val verticalScrollState = GlobalSchema.fragmentGalleryListScrollState!!
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                state = verticalScrollState,
            ) {

                extractedAlbumContent.forEachIndexed { index, map ->
                    val photoThumbnail = StringFormatter().getGoogleDriveThumbnail(map["id"]!!)
                    item {
                        Surface(
                            onClick = {
                                GlobalSchema.galleryViewerStartPage = index
                                GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_GALERI_VIEW
                                GlobalSchema.popBackScreen.value = NavigationRoutes().SCREEN_GALERI_LIST
                            },
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RectangleShape)
                                .padding(10.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            /* Displaying the thumbnail. */
                            AsyncImage(
                                model = photoThumbnail,
                                contentDescription = "",
                                error = painterResource(R.drawable.thumbnail_loading),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }  // --- end of surface.
                }  // --- end of forEachIndexed iteration.

            }  // --- end of lazy grid.
        }  // --- end of box.
    }  // --- end of getComposable().

}