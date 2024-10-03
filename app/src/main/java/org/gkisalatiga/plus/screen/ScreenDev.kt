/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * The "easter egg" developer menu.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ForwardToInbox
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.jeziellago.compose.markdowntext.MarkdownText
import okhttp3.internal.toHexString
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.services.NotificationService
import org.gkisalatiga.plus.services.WorkScheduler
import java.io.File
import java.io.InputStream


class ScreenDev : ComponentActivity() {

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

        // Init the texts.
        val screenTitle = stringResource(R.string.screen_dev_title)
        val screenDescription = stringResource(R.string.screen_dev_desc)

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
                    Surface (shape = CircleShape, modifier = Modifier.size(100.dp)) {
                        Box {
                            Box(Modifier.background(Color(0xff1482fa), shape = CircleShape).fillMaxSize())
                            Image(painterResource(R.mipmap.ic_launcher_foreground), "",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Displaying the text contents.
                Text(screenTitle, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Text("$appName $vName $vCode", fontSize = 18.sp)
                Spacer(Modifier.height(20.dp))
                Text(screenDescription, modifier = Modifier.padding(horizontal = 20.dp), textAlign = TextAlign.Center)
                Spacer(Modifier.height(20.dp))

                // Display the main "about" contents.
                getMainContent()

                // Trailing space for visual improvement.
                Spacer(Modifier.height(20.dp))
            }  // --- end of Column.
        }  // --- end of Scaffold.

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_ABOUT
        }

    }

    @Composable
    private fun getMainContent() {
        getQuickActions()
        Spacer(Modifier.height(20.dp))
        // insert more menu group here.
    }

    @Composable
    private fun getQuickActions() {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current

        /* The quick actions menu. */
        Column (Modifier.fillMaxWidth()) {
            val appInfoText = stringResource(R.string.screen_dev_quick_action_title)
            Spacer(Modifier.height(10.dp))
            Text(appInfoText, modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Trigger all notifications. */
            val notifTriggerText = stringResource(R.string.screen_dev_trigger_all_notifs)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    /* DEBUG: Testing notification trigger. */
                    NotificationService.showDebugNotification(ctx)
                    NotificationService.showSarenNotification(ctx)
                    NotificationService.showYKBHarianNotification(ctx)
                }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(notifTriggerText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* Trigger a WorKManager that launches notification once every 20th second each minute. */
            val triggerMinutelyWorkManagerText =
                stringResource(R.string.screen_dev_trigger_minutely_work_manager)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    WorkScheduler.scheduleMinutelyDebugReminder(ctx)
                }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Alarm, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(triggerMinutelyWorkManagerText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

        }  // --- end of column: section app info.
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
                    stringResource(R.string.screendev_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    GlobalSchema.pushScreen.value = NavigationRoutes().SCREEN_ABOUT
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

}