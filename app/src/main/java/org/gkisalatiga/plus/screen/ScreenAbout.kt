/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display meta-application information about GKI Salatiga Plus.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ForwardToInbox
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import dev.jeziellago.compose.markdowntext.MarkdownText
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes
import java.io.InputStream


class ScreenAbout : ComponentActivity() {

    // For displaying the license dialogs.
    private val showLicenseDialog = mutableStateOf(false)

    // For displaying generic markdown dialog.
    private val showMarkdownDialog = mutableStateOf(false)
    private var dialogMarkdownTitle = ""
    private var dialogMarkdownContent = ""
    private var dialogMarkdownIcon = Icons.Default.QuestionMark

    // The description of the application.
    private var appMainDescription = mutableStateOf("")

    // Determines the time and number-of-clicks for opening the EasterEgg.
    private var easterEggFirstClick = 0.toLong()
    private var easterEggCurrentClicks = 0
    private val easterEggMinClicks = 10
    private val easterEggTimeout = 2000.toLong()

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        val ctx = LocalContext.current

        // Obtain the app's essential information.
        // SOURCE: https://stackoverflow.com/a/6593822
        val pInfo: PackageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0)
        val vName = pInfo.versionName
        val vCode = pInfo.versionCode

        // Get app name.
        // SOURCE: https://stackoverflow.com/a/15114434
        val applicationInfo: ApplicationInfo = ctx.getApplicationInfo()
        val stringId = applicationInfo.labelRes
        val appName = if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else ctx.getString(stringId)

        // Init the app's main desc.
        if (appMainDescription.value.isBlank()) appMainDescription.value = stringResource(R.string.app_description)

        Scaffold (
            topBar = { getTopBar() }
                ) {

            val scrollState = GlobalSchema.screenAboutScrollState!!
            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
                    .verticalScroll(scrollState)) {
                /* Show app logo, name, and version. */
                val welcomeDevText = stringResource(R.string.screen_dev_welcome_developer)
                Box (Modifier.padding(vertical = 15.dp).padding(top = 10.dp)) {
                    Surface (
                        shape = CircleShape,
                        modifier = Modifier.size(100.dp),
                        onClick = {
                            /* You know what this is. */
                            if (GlobalSchema.DEBUG_ENABLE_EASTER_EGG) {
                                // Ensures that the user (developer) has to click N-times before opening the dev menu.
                                if (easterEggFirstClick + easterEggTimeout > System.currentTimeMillis()) {
                                    /* Opens the easter egg. */
                                    if (easterEggCurrentClicks >= easterEggMinClicks) {
                                        easterEggCurrentClicks = 0
                                        GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_DEV
                                        Toast.makeText(ctx, welcomeDevText, Toast.LENGTH_SHORT).show()
                                    } else {
                                        easterEggCurrentClicks += 1
                                    }
                                } else {
                                    easterEggCurrentClicks = 0
                                }

                                // Detecting the first time this button was clicked.
                                if (easterEggCurrentClicks == 0) {
                                    Toast.makeText(ctx, "\uD83D\uDC23", Toast.LENGTH_SHORT).show()
                                    easterEggFirstClick = System.currentTimeMillis()
                                }
                            }
                        }
                    ) {
                        Box {
                            Box(Modifier.background(Color(0xff482505), shape = CircleShape).fillMaxSize())
                            Image(painterResource(R.mipmap.ic_launcher_foreground), "",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Displaying the text contents.
                Text(appName, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text("Versi $vName", fontSize = 18.sp)
                Spacer(Modifier.height(20.dp))
                Text(appMainDescription.value, modifier = Modifier.padding(horizontal = 20.dp), textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))

                // Display the main "about" contents.
                getMainContent()

                // Trailing space for visual improvement.
                Spacer(Modifier.height(20.dp))

                // Displaying the about dialogs.
                getDialogLicense()
                getDialogMarkdown()
            }

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
        }

    }

    @Composable
    private fun getMainContent() {
        getAppInfo()
        Spacer(Modifier.height(20.dp))
        getAuthorInfo()
    }

    @Composable
    private fun getAppInfo() {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current

        /* Section: App Info */
        Column (Modifier.fillMaxWidth()) {
            val appInfoText = stringResource(R.string.screen_about_tentang_aplikasi)
            Spacer(Modifier.height(10.dp))
            Text(appInfoText, modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* App License. */
            val licenseText = stringResource(R.string.screen_about_lisensi)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = { showLicenseDialog.value = true }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Badge, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(licenseText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* App privacy policy. */
            val privacyPolicyText = stringResource(R.string.screen_about_kebijakan_privasi)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    // Prepare the text from raw resource.
                    val input: InputStream = ctx.resources.openRawResource(R.raw.app_privacy_policy)
                    val inputAsString: String = input.bufferedReader().use { it.readText() }

                    // Displaying the text.
                    dialogMarkdownContent = inputAsString
                    dialogMarkdownIcon = Icons.Default.Security
                    dialogMarkdownTitle = privacyPolicyText
                    showMarkdownDialog.value = true
                }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(privacyPolicyText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* App attribution and third party libraries. */
            val attributionText = stringResource(R.string.screen_about_atribusi_pihak_ketiga)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_ATTRIBUTION
                }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Default.LibraryBooks, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(attributionText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* App source code. */
            val sourceCodeText = stringResource(R.string.screen_about_kode_sumber)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = { uriHandler.openUri(GlobalSchema.aboutSourceCodeURL) }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(sourceCodeText, modifier = Modifier, textAlign = TextAlign.Center)
                    Icon(Icons.Default.OpenInBrowser,"", modifier = Modifier.padding(start = 5.dp).height(14.dp))
                }
            }

            /* App source code. */
            val changelogText = stringResource(R.string.screen_about_log_perubahan)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = { uriHandler.openUri(GlobalSchema.aboutChangelogURL) }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(changelogText, modifier = Modifier, textAlign = TextAlign.Center)
                    Icon(Icons.Default.OpenInBrowser,"", modifier = Modifier.padding(start = 5.dp).height(14.dp))
                }
            }

        }  // --- end of column: section app info.
    }

    @Composable
    private fun getAuthorInfo() {
        val ctx = LocalContext.current

        /* Section: Author Info */
        Column (Modifier.fillMaxWidth()) {
            val appInfoText = stringResource(R.string.screen_about_tentang_pengembang)
            Spacer(Modifier.height(10.dp))
            Text(appInfoText, modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Contributor lists. */
            val contributorText = stringResource(R.string.screen_about_kontributor)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    // Prepare the text from raw resource.
                    val input: InputStream = ctx.resources.openRawResource(R.raw.app_contribution)
                    val inputAsString: String = input.bufferedReader().use { it.readText() }

                    // Displaying the text.
                    dialogMarkdownContent = inputAsString
                    dialogMarkdownIcon = Icons.Default.Diversity1
                    dialogMarkdownTitle = contributorText
                    showMarkdownDialog.value = true
                }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Diversity1, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(contributorText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* Author contact. */
            val contactText = stringResource(R.string.screen_about_kontak)
            val emailChooserTitle = stringResource(R.string.email_chooser_title)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    // SOURCE: https://www.geeksforgeeks.org/how-to-send-an-email-from-your-android-app/
                    // SOURCE: https://www.tutorialspoint.com/android/android_sending_email.htm
                    // SOURCE: https://stackoverflow.com/a/59365539
                    val emailIntent = Intent(Intent.ACTION_SENDTO)
                    emailIntent.setData(Uri.parse("mailto:${GlobalSchema.aboutContactMail}"))
                    ctx.startActivity(Intent.createChooser(emailIntent, emailChooserTitle))
                }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Default.ForwardToInbox, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(contactText, modifier = Modifier, textAlign = TextAlign.Center)
                    Icon(Icons.Default.OpenInBrowser,"", modifier = Modifier.padding(start = 5.dp).height(14.dp))
                }
            }

        }  // --- end of column: section author info.
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
                    stringResource(R.string.screenabout_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_MAIN
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
    private fun getDialogLicense() {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val scrollState = rememberScrollState()

        if (showLicenseDialog.value) {
            AlertDialog(
                icon = {
                    Icon(Icons.Default.Badge, "")
                },
                title = {
                    Text(stringResource(R.string.screen_about_lisensi))
                },
                text = {
                    Box(Modifier.height(250.dp).background(Color.Transparent)) {
                        // Prepare the license text from raw resource.
                        val input: InputStream = ctx.resources.openRawResource(R.raw.app_license)
                        val inputAsString: String = input.bufferedReader().use { it.readText() }

                        // Display the license text.
                        Column(Modifier.verticalScroll(scrollState).fillMaxSize()) {
                            Text(inputAsString, modifier = Modifier.fillMaxSize())
                        }
                    }
                },
                onDismissRequest = {
                    showLicenseDialog.value = false
                },
                confirmButton = {
                    TextButton(onClick = { uriHandler.openUri(GlobalSchema.aboutLicenseFullTextURL) }) {
                        Text(stringResource(R.string.screen_about_license_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLicenseDialog.value = false }) {
                        Text(stringResource(R.string.screen_about_license_dismiss))
                    }
                }
            )

        }
    }

    @Composable
    private fun getDialogMarkdown() {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val scrollState = rememberScrollState()

        if (showMarkdownDialog.value) {
            AlertDialog(
                icon = {
                    Icon(dialogMarkdownIcon, "")
                },
                title = {
                    Text(dialogMarkdownTitle)
                },
                text = {
                    Box(Modifier.height(250.dp).background(Color.Transparent)) {

                        // Display the markdown text.
                        Column(Modifier.verticalScroll(scrollState).fillMaxSize()) {
                            MarkdownText(
                                modifier = Modifier.padding(2.dp),
                                markdown = dialogMarkdownContent.trimIndent(),
                                style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Justify)
                            )
                        }

                    }
                },
                onDismissRequest = {
                    showMarkdownDialog.value = false
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showMarkdownDialog.value = false }) {
                        Text(stringResource(R.string.screen_about_license_dismiss))
                    }
                }
            )

        }
    }

}