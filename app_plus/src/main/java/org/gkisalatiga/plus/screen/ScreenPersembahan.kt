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
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 10.dp)
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

            // Display the QRIS share button.
            Button(onClick = { ScreenPersembahanCompanion.shareQrisImage(current.ctx) }) {
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
            val persembahanJSONArray = MainCompanion.jsonRoot!!.getJSONArray("offertory")

            // Iterate through every offertory option.
            isFirstElement = true
            for (index in 0 until persembahanJSONArray.length()) {
                if (isFirstElement) {
                    HorizontalDivider()
                    isFirstElement = false
                }

                val currentNode = persembahanJSONArray[index] as JSONObject
                val notificationString = stringResource(R.string.offertory_number_copied)
                ListItem(
                    leadingContent = {
                        val bankLogoURL = currentNode.getString("bank-logo-url")
                        val bankName = currentNode.getString("bank-name")
                        AsyncImage(bankLogoURL, bankName, modifier = Modifier.size(60.dp))
                    },
                    overlineContent = { Text( currentNode.getString("bank-name") ) },
                    headlineContent = {
                        val headlineText = "${currentNode.getString("bank-abbr")} ${currentNode.getString("bank-number")}"
                        Text(headlineText, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = { Text("a.n. ${currentNode.getString("account-holder")}") },
                    modifier = Modifier.clickable(onClick = {
                        // Attempt to copy text to clipboard.
                        // SOURCE: https://www.geeksforgeeks.org/clipboard-in-android/
                        val clipData = ClipData.newPlainText("text", currentNode.getString("bank-number").replace(".", ""))
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
            val kodeUnikJSONArray = MainCompanion.jsonRoot!!.getJSONArray("offertory-code")

            // Iterate through every offertory option.
            isFirstElement = true
            for (index in 0 until kodeUnikJSONArray.length()) {
                if (isFirstElement) {
                    HorizontalDivider()
                    isFirstElement = false
                }

                val currentNode = kodeUnikJSONArray[index] as JSONObject
                val notificationString = stringResource(R.string.offertory_code_copied)
                ListItem(
                    leadingContent = {
                        val leadingText = currentNode.getString("unique-code")
                        Text(leadingText, fontWeight = FontWeight.Black, fontSize = 32.sp, textAlign = TextAlign.Center)
                    },
                    headlineContent = {
                        val headlineText = currentNode.getString("title")
                        Text(headlineText, fontWeight = FontWeight.Bold)
                    },
                    supportingContent = { Text(currentNode.getString("desc")) },
                    modifier = Modifier.clickable(onClick = {
                        // Attempt to copy text to clipboard.
                        // SOURCE: https://www.geeksforgeeks.org/clipboard-in-android/
                        val clipData = ClipData.newPlainText("text", currentNode.getString("unique-code"))
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
        val savedFilename = "QRIS GKI Salatiga.png"  // --- allows space and uppercase letters in the file name because it is the most user-friendly file export naming.

        /* Downloading the QRIS image and save/share it outside the app. */
        fun shareQrisImage(ctx: Context) {
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                try {
                    // Downloading the data.
                    val streamIn = URL(OFFERTORY_QRIS_SOURCE).openStream()
                    val decodedData: ByteArray = streamIn.readBytes()

                    // Writing into the file.
                    val outFile = File(absolutePathToQrisFile)
                    FileOutputStream(outFile).let {
                        it.flush()
                        it.write(decodedData)
                        it.close()
                        it
                    }

                    // The URI to the QRIS image provided by FileProvider.
                    val authority = InternalFileManager(ctx).FILE_PROVIDER_AUTHORITY
                    val secureUri = FileProvider.getUriForFile(ctx, authority, outFile)

                    // The strings to be attached to the intent.
                    val qrisShareSheetTitle = ctx.getString(R.string.offertory_qris_sharesheet_title)
                    val qrisShareSheetText = ctx.getString(R.string.offertory_qris_sharesheet_text)

                    // Creating the intent.
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, secureUri)
                        putExtra(Intent.EXTRA_TEXT, qrisShareSheetText)
                        putExtra(Intent.EXTRA_TITLE, qrisShareSheetTitle)  // --- this is not currently successful at setting the chooser title.
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        type = "image/png"

                        // Attach clipboard.
                        // This prevents "Writing exception to parcel" (java.lang.SecurityException) when attempting to load thumbnails.
                        // The error in itself is not harmful; the images can still be shared or saved to the destination file.
                        // SOURCE: https://stackoverflow.com/a/64541741
                        clipData = ClipData(
                            "QRIS Image of GKI Salatiga",
                            listOf("image/png").toTypedArray(),
                            ClipData.Item(secureUri)
                        )
                    }

                    // Sharing the data.
                    ctx.startActivity(Intent.createChooser(shareIntent, null))
                } catch (e: Exception) {
                    Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)
                    e.printStackTrace()
                }
            }
        }
    }
}