/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load a custom HTML body.
 * Only those HTML contents stored in the JSON schema's "data/static" node can be displayed.
 */

package org.gkisalatiga.plus.screen

import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
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
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.services.ClipManager
import org.gkisalatiga.plus.services.InternalFileManager
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.Executors


class ScreenPersembahan (private val current : ActivityData) : ComponentActivity() {
    private var isFirstElement = false
    private val showOffertoryCodeTextDialog = mutableStateOf(false)

    @Composable
    fun getComposable() {
        Scaffold (
            topBar = { this.getTopBar() }
        ) {

            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }

            // Display the offertory code help text.
            getOffertoryCodeText()
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    private fun getMainContent() {

        // The column's saved scroll state.
        val scrollState = ScreenPersembahanCompanion.rememberedScrollState!!
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize().padding(20.dp)
        ) {
            /* QRIS title. */
            Text(
                stringResource(R.string.section_title_qris),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 10.dp)
            )

            /* Display the banner image. */
            val posterViewerTitle = stringResource(R.string.offertory_qris_image_viewer_title)
            val posterViewerCaption = stringResource(R.string.offertory_qris_image_viewer_caption)
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 10.dp),
                onClick = {
                    ScreenPosterViewerLegacyCompanion.posterViewerImageSource = ScreenPersembahanCompanion.OFFERTORY_QRIS_SOURCE
                    ScreenPosterViewerLegacyCompanion.posterViewerTitle = posterViewerTitle
                    ScreenPosterViewerLegacyCompanion.posterViewerCaption = posterViewerCaption
                    AppNavigation.navigate(NavigationRoutes.SCREEN_POSTER_VIEWER_LEGACY)
                }
            ) {
                Box {
                    /* The base background for the transparent PNG. */
                    Box (Modifier.background(Color(0xffffffff)).matchParentSize()) {}

                    /* The transparent QR code. */
                    AsyncImage(
                        ScreenPersembahanCompanion.OFFERTORY_QRIS_SOURCE,
                        contentDescription = "The QRIS code image to GKI Salatiga offertory account",
                        error = painterResource(R.drawable.thumbnail_error),
                        placeholder = painterResource(R.drawable.thumbnail_placeholder),
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

            // Display the helper text.
            Text(stringResource(R.string.offertory_qris_image_caption_zoom), fontStyle = FontStyle.Italic, fontSize = 11.sp)

            // Display the QRIS share button.
            Button(onClick = {
                InternalFileManager(current.ctx).downloadAndShare(
                    url = ScreenPersembahanCompanion.OFFERTORY_QRIS_SOURCE,
                    savedFilename = ScreenPersembahanCompanion.savedFilename,
                    shareSheetText = current.ctx.getString(R.string.offertory_qris_sharesheet_text),
                    shareSheetTitle = current.ctx.getString(R.string.offertory_qris_sharesheet_title),
                    clipboardLabel = "QRIS Image of GKI Salatiga"
                )
            }) {
                Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.Share, "Share icon", tint = Color.White, modifier = Modifier.padding(end = 10.dp))
                    Text(stringResource(R.string.offertory_qris_share_btn_text), color = Color.White)
                }
            }

            /* Other payment method title. */
            Text(
                stringResource(R.string.section_title_other_payment_method),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .padding(top = 25.dp)
            )

            // The JSON node for bank account.
            // val persembahanJSONArray = MainCompanion.jsonRoot!!.getJSONArray("offertory")
            val persembahanJSONArray = MainCompanion.api!!.offertory

            // Iterate through every offertory option.
            isFirstElement = true
            for (index in 0 until persembahanJSONArray.size) {
                if (isFirstElement) {
                    HorizontalDivider()
                    isFirstElement = false
                }

                val currentNode = persembahanJSONArray[index]
                val notificationString = stringResource(R.string.offertory_number_copied)
                ListItem(
                    leadingContent = {
                        val bankLogoURL = currentNode.bankLogoUrl
                        val bankName = currentNode.bankName
                        AsyncImage(bankLogoURL, bankName, modifier = Modifier.size(60.dp))
                    },
                    overlineContent = { Text( currentNode.bankName ) },
                    headlineContent = {
                        val headlineText = "${currentNode.bankAbbr} ${currentNode.bankNumber}"
                        Text(headlineText, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = { Text("a.n. ${currentNode.accountHolder}") },
                    modifier = Modifier.clickable(onClick = {
                        // Attempt to copy text to clipboard.
                        // SOURCE: https://www.geeksforgeeks.org/clipboard-in-android/
                        val clipData = ClipData.newPlainText("text", currentNode.bankNumber.replace(".", ""))
                        ClipManager.clipManager!!.setPrimaryClip(clipData)

                        Toast.makeText(current.ctx, notificationString, Toast.LENGTH_SHORT).show()
                    })
                )
                HorizontalDivider()
            }

            /* Offertory code title. */
            Row (modifier = Modifier.padding(vertical = 10.dp).padding(top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.section_title_offertory_code),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton( onClick = { showOffertoryCodeTextDialog.value = true } ) { Icon(Icons.AutoMirrored.Default.Help, "") }
            }

            // The JSON node for offertory code.
            // val kodeUnikJSONArray = MainCompanion.jsonRoot!!.getJSONArray("offertory-code")
            val kodeUnikJSONArray = MainCompanion.api!!.offertoryCode

            // Iterate through every offertory option.
            isFirstElement = true
            for (index in 0 until kodeUnikJSONArray.size) {
                if (isFirstElement) {
                    HorizontalDivider()
                    isFirstElement = false
                }

                val currentNode = kodeUnikJSONArray[index]
                val notificationString = stringResource(R.string.offertory_code_copied)
                ListItem(
                    leadingContent = {
                        val leadingText = currentNode.uniqueCode
                        Text(leadingText, fontWeight = FontWeight.Black, fontSize = 32.sp, textAlign = TextAlign.Center)
                    },
                    headlineContent = {
                        val headlineText = currentNode.title
                        Text(headlineText, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = { Text(currentNode.desc) },
                    modifier = Modifier.clickable(onClick = {
                        // Attempt to copy text to clipboard.
                        // SOURCE: https://www.geeksforgeeks.org/clipboard-in-android/
                        val clipData = ClipData.newPlainText("text", currentNode.uniqueCode)
                        ClipManager.clipManager!!.setPrimaryClip(clipData)

                        Toast.makeText(current.ctx, notificationString, Toast.LENGTH_SHORT).show()
                    })
                )
                HorizontalDivider()
            }

        }  // --- end of scrollable column.
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screenoffertory_title),
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

    @Composable
    private fun getOffertoryCodeText() {

        if (showOffertoryCodeTextDialog.value) {
            AlertDialog(
                icon = {
                    Icon(Icons.AutoMirrored.Default.Help, "")
                },
                title = {
                    Text(stringResource(R.string.section_title_offertory_code))
                },
                text = {
                    Text(stringResource(R.string.offertory_code_desc))
                },
                onDismissRequest = {
                    showOffertoryCodeTextDialog.value = false
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showOffertoryCodeTextDialog.value = false }) {
                        Text(stringResource(R.string.screen_persembahan_ok))
                    }
                }
            )

        }
    }

}

class ScreenPersembahanCompanion : Application() {
    companion object {
        /* The QRIS image for offertories. */
        const val OFFERTORY_QRIS_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data-json/main/v2/res/qris_gkis.png"

        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* The QRIS location of the image in the phone's storage. */
        var absolutePathToQrisFile: String = String()
        @Suppress("ConstPropertyName")
        const val savedFilename = "QRIS_GKI_Salatiga.png"  // --- allows space and uppercase letters in the file name because it is the most user-friendly file export naming.
    }
}