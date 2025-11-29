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
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.services.InternalFileManager


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

            // Init. the dialog.
            FileDeleteDialog().let { fdg ->
                fdg.draw(
                    onConfirmRequest = {
                        InternalFileManager(current.ctx).deleteBible(fdg.getFileUrlToDelete())
                        AppNavigation.recomposeUi()
                    },
                    onDismissRequest = null
                )
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

            Text("[LOREM IPSUM] Fitur ini masih dalam pengembangan. Disklaimer>>> Beberapa versi yang tersedia (AYT, BBE, dan WEB) merupakan Alkitab bersumber terbuka yang dapat diunduh tanpa memerlukan izin khusus. Sementara itu, Alkitab versi TB dan TB2 dalam bentuk elektronik masih dalam proses pengurusan administrasi.")

            // The list of bible versions.
            ModulesCompanion.api!!.bible.forEach {

                // Preparing the arguments.
                val abbr = it.abbr
                val title = it.name
                val author = it.author
                val authorUrl = it.authorUrl
                val lang = it.lang
                val license = it.license
                val licenseUrl = it.licenseUrl
                val source = it.source
                val size = it.sourceSize
                val desc = it.description
                val url = it.sourceJson

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "You just clicked: $title that points to $url!", Toast.LENGTH_SHORT).show()

                        // Navigate to the Bible viewer.
                        ScreenBibleViewerCompanion.putArguments(abbr, title, author, authorUrl, lang, license, licenseUrl, source, size, desc, url)
                        AppNavigation.navigate(NavigationRoutes.SCREEN_BIBLE_VIEWER)
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
                                    title + "($abbr)",
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
                                val isDownloaded = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_BIBLE_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
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