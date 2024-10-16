/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.fdroid.fragment

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.fdroid.R
import org.gkisalatiga.fdroid.global.GlobalSchema
// import coil.compose.AsyncImage
import org.gkisalatiga.fdroid.lib.StringFormatter

class FragmentGalleryStory : ComponentActivity() {

    @Composable
    fun getComposable() {
        val verticalScrollState = rememberScrollState()
        Column (
            modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(verticalScrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {
            // Titles, stories, and such.
            val title = GlobalSchema.displayedAlbumTitle
            val story = GlobalSchema.displayedAlbumStory
            val featuredImageID = GlobalSchema.displayedFeaturedImageID

            // Displaying featured banner.
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                AsyncImage(
                    model = StringFormatter().getGoogleDriveThumbnail(featuredImageID, 240),
                    contentDescription = title,
                    error = painterResource(R.drawable.thumbnail_loading),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Displaying the meta-information.
            Text(title, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(bottom = 10.dp))
            Text(story, fontWeight = FontWeight.Normal, fontSize = 14.sp)
        }
    }

}