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
import android.app.Activity
import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.services.NotificationService
import org.gkisalatiga.plus.services.WorkScheduler
import kotlin.math.abs


class ScreenDev : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        val ctx = LocalContext.current

        // Obtain the app's essential information.
        // SOURCE: https://stackoverflow.com/a/6593822
        val pInfo: PackageInfo = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
        val vName = pInfo.versionName
        val vCode = pInfo.versionCode

        // Get app name.
        // SOURCE: https://stackoverflow.com/a/15114434
        val applicationInfo: ApplicationInfo = ctx.applicationInfo
        val stringId = applicationInfo.labelRes
        val appName = if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else ctx.getString(stringId)

        // Init the texts.
        val screenTitle = stringResource(R.string.screen_dev_title)
        val screenDescription = stringResource(R.string.screen_dev_desc)

        Scaffold (
            topBar = { getTopBar() }
                ) {

            val scrollState = ScreenDevCompanion.rememberedScrollState!!
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
        BackHandler { AppNavigation.popBack() }

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
                    NotificationService.showDebugDataUpdateNotification(ctx)
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

            /* Trigger the changing of screen's orientation. */
            val triggerOrientationChange =
                stringResource(R.string.screen_dev_trigger_orientation_change)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    (ctx as Activity).requestedOrientation = abs(ctx.requestedOrientation - 1)
                    GlobalCompanion.isPortraitMode.value = ctx.requestedOrientation == 1
                }
            ) {
                Row (modifier = Modifier.padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlipCameraAndroid, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(triggerOrientationChange, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            val appLocalText = stringResource(R.string.screen_dev_local_storage_title)
            Spacer(Modifier.height(10.dp))
            Text(appLocalText, modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Displaying all LocalStorage values. */
            LocalStorage(ctx).getAll().entries.sortedBy { it.key }.forEach {
                Text(it.key, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp, modifier = Modifier.padding(horizontal = 20.dp))
                Text("${it.value}", fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp, modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp))
            }

            val appPrefText = stringResource(R.string.screen_dev_app_preferences_title)
            Spacer(Modifier.height(10.dp))
            Text(appPrefText, modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Displaying all AppPreferences values. */
            AppPreferences(ctx).getAll().entries.sortedBy { it.key }.forEach {
                Text(it.key, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp, modifier = Modifier.padding(horizontal = 20.dp))
                Text("${it.value}", fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp, modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp))
            }

            val appDebugFlagsText = stringResource(R.string.screen_dev_debug_flags_title)
            Spacer(Modifier.height(10.dp))
            Text(appDebugFlagsText, modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            // The list of all debug flags.
            val debugFlags : Map<String, Boolean> = mapOf(
                "DEBUG_ENABLE_EASTER_EGG" to GlobalCompanion.DEBUG_ENABLE_EASTER_EGG,
                "DEBUG_ENABLE_TOAST" to GlobalCompanion.DEBUG_ENABLE_TOAST,
                "DEBUG_ENABLE_LOG_CAT" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT,
                "DEBUG_ENABLE_LOG_CAT_BOOT" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_BOOT,
                "DEBUG_ENABLE_LOG_CAT_CONN_TEST" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_CONN_TEST,
                "DEBUG_ENABLE_LOG_CAT_DOWNLOADER" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DOWNLOADER,
                "DEBUG_ENABLE_LOG_CAT_DEEP_LINK" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DEEP_LINK,
                "DEBUG_ENABLE_LOG_CAT_DUMP" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DUMP,
                "DEBUG_ENABLE_LOG_CAT_INIT" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_INIT,
                "DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE,
                "DEBUG_ENABLE_LOG_CAT_PDF" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PDF,
                "DEBUG_ENABLE_LOG_CAT_PREFERENCES" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PREFERENCES,
                "DEBUG_ENABLE_LOG_CAT_RAPID_TEST" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_RAPID_TEST,
                "DEBUG_ENABLE_LOG_CAT_TEST" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_TEST,
                "DEBUG_ENABLE_LOG_CAT_UPDATER" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_UPDATER,
                "DEBUG_ENABLE_LOG_CAT_WORKER" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_WORKER,
                "DEBUG_SHOW_INFO_PDF_LOCAL_PATH_INFO" to GlobalCompanion.DEBUG_SHOW_INFO_PDF_LOCAL_PATH_INFO,
                "DEBUG_DISABLE_SPLASH_SCREEN" to GlobalCompanion.DEBUG_DISABLE_SPLASH_SCREEN,
            )

            /* Displaying all of the app's debug flag values. */
            debugFlags.entries.sortedBy { it.key }.forEach {
                Text(it.key, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp, modifier = Modifier.padding(horizontal = 20.dp))
                Text("${it.value}", fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp, modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 10.dp))
            }

        }  // --- end of column: section app info.
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screendev_title),
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

class ScreenDevCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}