/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the Bible.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.FileDeleteDialog
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter


class ScreenBible (private val current : ActivityData) : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        Scaffold (
            topBar = { getTopBar() }
        ) {
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
    private fun getMainContent() {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = ScreenBibleCompanion.rememberedScrollState!!)
                .padding(20.dp)
        ) {
            // BEGIN: Actual content.
            /* Display the banner image. */
            val imgSource = R.drawable.banner_alkitab
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

            // The list of bible versions.
            ModulesCompanion.api!!.bible.forEach {

                // Preparing the arguments.
                val abbr = it.abbr
                val title = it.name
                val author = it.author
                val lang = it.lang
                val license = it.license
                val source = it.source
                val size = it.sourceSize
                val desc = it.description

                // Displaying the individual card.
                Card(
                    onClick = {
                        // if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                        // Navigate to the PDF viewer.
                        // ScreenPDFViewerCompanion.putArguments(title, author, publisher, publisherLoc, year, thumbnail, url, source, size)
                        // AppNavigation.navigate(NavigationRoutes.SCREEN_PDF_VIEWER)
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
                                    "",
                                    contentDescription = "Bible Book Thumbnail: $title",
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
                                // The publication license.
                                val licenseLabel = stringResource(R.string.bible_license_label)
                                Row {
                                    Icon(Icons.Default.Policy, "License icon", modifier = Modifier.scale(0.8f).padding(end = 5.dp))
                                    Text(
                                        "$licenseLabel $license",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                // The language.
                                Row {
                                    Icon(Icons.Default.Language, "Language icon", modifier = Modifier.scale(0.8f).padding(end = 5.dp))
                                    Text(
                                        StringFormatter.convertBibleLangToLocale(lang),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // The downloaded PDF badge.
                                val isDownloadedTitle = stringResource(R.string.pdf_already_downloaded_localized)
                                val badgeColor = Colors.MAIN_PDF_DOWNLOADED_BADGE_COLOR
                                // if (isDownloaded) {
                                if (false) {
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
                                // if (isDownloaded) {
                                if (false) {
                                    TextButton(
                                        modifier = Modifier.padding(top = 8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Colors.SCREEN_YKB_ARCHIVE_BUTTON_COLOR
                                        ),
                                        onClick = {
                                            // FileDeleteDialog().show(title, url)
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

            // END: Actual content.
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screenbible_title),
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

class ScreenBibleCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}