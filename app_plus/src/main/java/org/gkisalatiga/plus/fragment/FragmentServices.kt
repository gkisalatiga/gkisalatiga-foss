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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppColors
import org.gkisalatiga.plus.lib.Logger
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONArray
import org.json.JSONObject

class FragmentServices : ComponentActivity() {

    @Composable
    fun getComposable() {

        // The "all video playlists" JSON array.
        val allVideoPlaylistArray = GlobalSchema.globalJSONObject!!.getJSONArray("yt")

        // Get the pinned video playlists.
        val pinnedPlaylistDictionary: MutableList<JSONObject> = mutableListOf()
        for (i in 0 until allVideoPlaylistArray.length()) {
            if (allVideoPlaylistArray.getJSONObject(i).getInt("pinned") == 1) {
                pinnedPlaylistDictionary.add(allVideoPlaylistArray[i] as JSONObject)
            }
        }

        // Enabling vertical scrolling, and setting the layout to center both vertically and horizontally.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        // SOURCE: https://stackoverflow.com/a/72769561
        val scrollState = GlobalSchema.fragmentServicesScrollState!!
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            // Assumes both "pinnedPlaylistTitle" and "pinnedPlaylistContent" have the same list size.
            pinnedPlaylistDictionary.forEach { obj ->
                // Displaying the relevant YouTube-based church services.
                getServicesUI((obj as JSONObject).getString("title"), obj.getJSONArray("content"))
            }

            // Opens the non-pinned video playlist.
            // TODO: String extraction and visual improvement of the button.
            Spacer(Modifier.fillMaxWidth().padding(top = 10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(AppColors.FRAGMENT_SERVICES_SHOWMORE_BACKGROUND),
                contentColor = Color(AppColors.FRAGMENT_SERVICES_SHOWMORE_CONTENT),
                onClick = {
                    GlobalSchema.popBackScreen.value = NavigationRoutes.SCREEN_MAIN
                    GlobalSchema.pushScreen.value = NavigationRoutes.SCREEN_MEDIA
                }
            ) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                    Text(stringResource(R.string.submenu_services_showmore), modifier = Modifier.weight(7.0f), fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(Modifier.weight(2.0f))
                    Icon(Icons.AutoMirrored.Default.ArrowRight, "", modifier = Modifier.weight(1.0f))
                }
            }

            /* Displaying redundant spacer for visual neatness. */
            Spacer(modifier = Modifier.height(20.dp))

        }

    }

    /**
     * Obtains the composable UI of the "list of services".
     * The data displayed is based on the arguments supplied.
     * @param nodeName the name of the JSON metadata node that represents the desired service section.
     * @param sectionTitle the title string that will be displayed on top of the section.
     */
    @Composable
    private fun getServicesUI(sectionTitle: String, sectionContent: JSONArray) {

        // The current playlist's video list.
        val playlistContentList: MutableList<JSONObject> = mutableListOf()
        for (i in 0 until sectionContent.length()) {
            playlistContentList.add(sectionContent[i] as JSONObject)
        }
        // playlistContentList.removeAt(0)

        // Only show the "N" most recent videos.
        val recentVideoList: MutableList<JSONObject> = mutableListOf()
        val videoCount = 3
        if (playlistContentList.size > videoCount) {
            recentVideoList.addAll(playlistContentList.subList(0, videoCount))
        } else {
            recentVideoList.addAll(playlistContentList)
        }

        /* Displaying the section title. */
        Row (modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(sectionTitle, modifier = Modifier.fillMaxWidth().weight(4f), fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis)
            Button(onClick = {
                // Display the list of videos in this playlist.
                GlobalSchema.videoListContentArray = playlistContentList
                GlobalSchema.videoListTitle = sectionTitle
                GlobalSchema.pushScreen.value = NavigationRoutes.SCREEN_VIDEO_LIST
                GlobalSchema.ytVideoListDispatcher = NavigationRoutes.SCREEN_MAIN
            }, modifier = Modifier.fillMaxWidth().weight(1f).padding(0.dp).wrapContentSize(Alignment.Center, true)) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Some desc", modifier = Modifier.fillMaxSize().aspectRatio(1.0f).padding(0.dp))
            }
        }

        /* Displaying the individual item of this section. */
        val ctx = LocalContext.current
        LazyRow {
            // Left-right padding.
            item { Spacer(modifier = Modifier.width(5.dp)) }

            // The actual content. This displays the cards.
            items(recentVideoList.size) {

                // Preparing the arguments.
                val title = recentVideoList[it].getString("title")
                val url = recentVideoList[it].getString("link")
                val desc = recentVideoList[it].getString("desc")

                // Format the date.
                val date = StringFormatter.convertDateFromJSON(recentVideoList[it].getString("date"))

                // Retrieving the video thumbnail.
                val thumbnail = recentVideoList[it].getString("thumbnail")

                Card (
                    onClick = {
                        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "The card $title is clicked", Toast.LENGTH_SHORT).show()

                        // Trying to switch to the YouTube viewer and open the stream.
                        Logger.log({}, "Opening the YouTube stream: $url.")
                        GlobalSchema.ytViewerParameters["yt-link"] = url
                        GlobalSchema.ytViewerParameters["yt-id"] = StringFormatter.getYouTubeIDFromUrl(url)
                        GlobalSchema.ytViewerParameters["title"] = title
                        GlobalSchema.ytViewerParameters["date"] = date
                        GlobalSchema.ytViewerParameters["desc"] = desc
                        GlobalSchema.ytCurrentSecond.floatValue = 0.0f
                        GlobalSchema.popBackScreen.value = NavigationRoutes.SCREEN_MAIN
                        GlobalSchema.pushScreen.value = NavigationRoutes.SCREEN_LIVE
                    },
                    //modifier = Modifier.fillMaxHeight().width(320.dp).height(232.dp).padding(horizontal = 5.dp),
                    modifier = Modifier.width(320.dp).aspectRatio(1.33334f).padding(horizontal = 5.dp),
                ) {
                    Column {
                        // Displaying the image.
                        AsyncImage(
                            model = thumbnail,
                            contentDescription = title,
                            error = painterResource(R.drawable.thumbnail_loading_stretched),
                            modifier = Modifier.fillMaxWidth().aspectRatio(1.77778f),
                            contentScale = ContentScale.Crop
                        )
                        // Displaying the content description of the card.
                        Column (modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp), verticalArrangement = Arrangement.Center) {
                            Text(title, fontWeight = FontWeight.SemiBold, minLines = 1, maxLines = 2, overflow = TextOverflow.Visible)
                            // Text("Diunggah $date")
                        }
                    }
                }
            }  // --- end for each.

            // Left-right padding.
            item { Spacer(modifier = Modifier.width(5.dp)) }
        }

        /* Displaying redundant spacer for visual neatness. */
        Spacer(modifier = Modifier.height(20.dp))

    }

}