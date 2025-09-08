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

import android.content.ContentResolver.MimeTypeInfo
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenPosterViewerLegacyCompanion
import org.gkisalatiga.plus.services.InternalFileManager

class FragmentSeasonalAgenda (private val current : ActivityData) : ComponentActivity() {

    // The root seasonal node.
    // private val seasonalNode = ModulesCompanion.jsonRoot!!.getJSONObject("seasonal")
    private val seasonalNode = ModulesCompanion.api!!.seasonal

    @Composable
    fun getComposable() {
        Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            // Display the main title.
            // val mainTitle = seasonalNode.getJSONObject("static-menu").getJSONObject("agenda").getString("title")
            val mainTitle = seasonalNode.staticMenu.agenda.title
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(mainTitle, modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(20.dp))

            /* Preparing the strings. */
            val posterViewerUrl = seasonalNode.staticMenu.agenda.url
            val posterViewerTitle = seasonalNode.staticMenu.agenda.title
            val posterViewerCaption = seasonalNode.staticMenu.agenda.title
            val posterSavedFilename = posterViewerTitle.replace(" ", "_") + "." + MimeTypeMap.getFileExtensionFromUrl(posterViewerUrl)

            /* Display the image. */
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = {
                    ScreenPosterViewerLegacyCompanion.posterViewerImageSource = if (posterViewerUrl.isNullOrBlank()) "" else posterViewerUrl
                    ScreenPosterViewerLegacyCompanion.posterViewerTitle = posterViewerTitle
                    ScreenPosterViewerLegacyCompanion.posterViewerCaption = posterViewerCaption
                    AppNavigation.navigate(NavigationRoutes.SCREEN_POSTER_VIEWER_LEGACY)
                }
            ) {
                Box {
                    /* The base background for the transparent PNG. */
                    Box (Modifier.background(Color(0xffffffff)).matchParentSize()) {}

                    /* The image. */
                    AsyncImage(
                        posterViewerUrl,
                        contentDescription = "The seasonal agenda image",
                        error = painterResource(R.drawable.thumbnail_error),
                        placeholder = painterResource(R.drawable.thumbnail_placeholder),
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            // Display the helper text.
            Text(stringResource(R.string.fragment_seasonal_agenda_image_caption_zoom), fontStyle = FontStyle.Italic, fontSize = 11.sp)

            // Display the share button.
            Button(onClick = {
                InternalFileManager(current.ctx).downloadAndShare(
                    url = if (posterViewerUrl.isNullOrBlank()) "" else posterViewerUrl,
                    savedFilename = posterSavedFilename,
                    shareSheetText = current.ctx.getString(R.string.fragment_seasonal_agenda_share_sheet_text).replace("%%%TITLE%%%", posterViewerTitle),
                    shareSheetTitle = posterViewerTitle,
                    clipboardLabel = posterViewerTitle
                )
            }) {
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Share, "Share icon", tint = Color.White, modifier = Modifier.padding(end = 10.dp))
                    Text(stringResource(R.string.fragment_seasonal_agenda_image_share_btn_text), color = Color.White)
                }
            }
        }
    }

}
