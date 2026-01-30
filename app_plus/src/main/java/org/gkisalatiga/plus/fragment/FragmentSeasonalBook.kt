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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
import org.gkisalatiga.plus.composable.FileDeleteDialog
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.ModulesLibraryItemObject
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion
import org.gkisalatiga.plus.services.InternalFileManager

class FragmentSeasonalBook (private val current : ActivityData) : ComponentActivity() {

    // The root seasonal node.
    // private val seasonalNode = ModulesCompanion.jsonRoot!!.getJSONObject("seasonal")
    private val seasonalNode = ModulesCompanion.api!!.seasonal

    @Composable
    fun getComposable() {
        // Init. the delete dialog.
        FileDeleteDialog().let { fdg ->
            fdg.draw(
                onConfirmRequest = {
                    InternalFileManager(current.ctx).deletePdf(fdg.getFileUrlToDelete())
                    AppNavigation.recomposeUi()
                },
                onDismissRequest = null
            )
        }

        // Enumerate the list of e-books that correspond to the seasonal tag.
        val seasonalBookTag = ModulesCompanion.api!!.seasonal.staticMenu.books.selectionTag.let { if (it.isNullOrBlank()) "" else it }
        // val seasonalBookTag = ModulesCompanion.jsonRoot!!
        //     .getJSONObject("seasonal")
        //     .getJSONObject("static-menu")
        //     .getJSONObject("books")
        //     .getString("selection-tag")
        val filteredBookList = mutableListOf<ModulesLibraryItemObject>()
        // ModulesCompanion.jsonRoot!!.getJSONArray("library").let {
        ModulesCompanion.api!!.library.let {
            for (i in 0 until it.size) {
                // it.getJSONObject(i).let { o -> if (o.getJSONArray("tags").join(",").contains(seasonalBookTag)) filteredBookList.add(o) }
                it[i].let { o -> if (o.tags.toString().contains(seasonalBookTag)) filteredBookList.add(o) }
            }
        }

        Column {
            // Display the main title.
            // val mainTitle = seasonalNode.getJSONObject("static-menu").getJSONObject("books").getString("title")
            val mainTitle = seasonalNode.staticMenu.books.title
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(mainTitle, modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(20.dp))

            filteredBookList.filter { it.isShown == 1 }.forEach {
                // Preparing the arguments.
                val title = it.title
                val author = it.author
                val publisher = it.publisher
                val publisherLoc = it.publisherLoc
                val year = it.year
                val thumbnail = it.thumbnail
                val url = it.downloadUrl
                val source = it.source
                val size = it.size

                // Whether this PDF has been downloaded.
                val isDownloaded = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

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
                                val badgeColor = Colors.MAIN_PDF_DOWNLOADED_BADGE_COLOR
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

                                // The remove file button.
                                if (isDownloaded) {
                                    TextButton(
                                        modifier = Modifier.padding(top = 8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Colors.SCREEN_YKB_ARCHIVE_BUTTON_COLOR
                                        ),
                                        onClick = {
                                            FileDeleteDialog().show(title, url)
                                        }
                                    ) {
                                        Row (verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.DeleteForever, "")
                                            Spacer(Modifier.width(5.dp))
                                            Text(stringResource(R.string.pdf_action_delete_pdf_btn).uppercase())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }  // --- end of Card.
            }
        }
    }

}
