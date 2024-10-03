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

import android.content.ClipData
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.json.JSONObject

class ScreenPersembahan : ComponentActivity() {
    private var isFirstElement = false
    private val showOffertoryCodeTextDialog = mutableStateOf(false)

    @Composable
    public fun getComposable() {
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
        BackHandler {
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
        }

    }

    @Composable
    private fun getMainContent() {
        val ctx = LocalContext.current

        // The column's saved scroll state.
        val scrollState = GlobalSchema.screenPersembahanScrollState!!
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
                        GlobalSchema.offertoryQRISImageSource,
                        contentDescription = "The QRIS code image to GKI Salatiga offertory account",
                        error = painterResource(R.drawable.thumbnail_loading),
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
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
            val persembahanJSONArray = GlobalSchema.globalJSONObject!!.getJSONArray("offertory")

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
                        GlobalSchema.clipManager!!.setPrimaryClip(clipData)

                        Toast.makeText(ctx, notificationString, Toast.LENGTH_SHORT).show()
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
                Surface(
                    shape = CircleShape,
                    onClick = {
                        showOffertoryCodeTextDialog.value = true
                    }
                ) {
                    Icon(Icons.AutoMirrored.Default.Help, "")
                }
            }

            // The JSON node for offertory code.
            val kodeUnikJSONArray = GlobalSchema.globalJSONObject!!.getJSONArray("offertory-code")

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
                        GlobalSchema.clipManager!!.setPrimaryClip(clipData)

                        Toast.makeText(ctx, notificationString, Toast.LENGTH_SHORT).show()
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
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    stringResource(R.string.screenoffertory_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                }) {
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
        val ctx = LocalContext.current

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