/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import java.io.File

class FragmentGalleryList : ComponentActivity() {

    @Composable
    public fun getComposable() {
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