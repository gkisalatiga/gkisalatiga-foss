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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import org.gkisalatiga.plus.composable.YouTubeViewCompanion
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter

class FragmentSeasonalPlaylist (private val current : ActivityData) : ComponentActivity() {

    @Composable
    fun getComposable() {

        // Enumerate the list of seasonal YouTube playlist.
        val keyword = ModulesCompanion.api!!.seasonal.staticMenu.playlist.ytPlaylist.let { if (it.isNullOrBlank()) "" else it }
        val filteredPlaylist = MainCompanion.api!!.yt.filter { it.playlistId == keyword }

        // Display the list of seasonal videos.
        if (filteredPlaylist.size == 1) {
            filteredPlaylist[0].let { playlist ->
                Logger.logTest({}, "The seasonal YouTube playlist is: ${playlist.playlistId}, ${playlist.title}.")
                Column {
                    // Display the main title.
                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        Text(playlist.title, modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
                    }

                    Spacer(Modifier.height(20.dp))

                    /* Draw the news selection elements. */
                    playlist.content.forEach {

                        // Preparing the arguments.
                        val title = it.title
                        val date = StringFormatter.convertDateFromJSON(it.date)
                        val url = it.link
                        val desc = it.desc
                        val thumbnail = it.thumbnail

                        // Displaying the individual card.
                        Card(
                            onClick = {
                                if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                                // Trying to switch to the YouTube viewer and open the stream.
                                Logger.log({}, "Opening YouTube stream: $url.")
                                YouTubeViewCompanion.seekToZero()

                                // Put arguments to the YouTube video viewer.
                                YouTubeViewCompanion.putArguments(
                                    date = date,
                                    desc = desc,
                                    thumbnail = thumbnail,
                                    title = title,
                                    yt_id = StringFormatter.getYouTubeIDFromUrl(url),
                                    yt_link = url
                                )

                                // Navigate to the screen.
                                AppNavigation.navigate(NavigationRoutes.SCREEN_LIVE)
                            },
                            modifier = Modifier.padding(bottom = 10.dp).height(65.dp)
                        ) {
                            Row ( modifier = Modifier.padding(0.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically ) {
                                // Displaying the thumbnail image.
                                AsyncImage(
                                    model = thumbnail,
                                    contentDescription = title,
                                    error = painterResource(R.drawable.thumbnail_error_stretched),
                                    placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                    modifier = Modifier.aspectRatio(1.77778f).fillMaxHeight(),
                                    contentScale = ContentScale.Crop,
                                )
                                Text(title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp),
                                    minLines = 1,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

    }

}
