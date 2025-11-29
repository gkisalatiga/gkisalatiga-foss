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
import android.content.ClipData
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.Gallery
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.Modules
import org.gkisalatiga.plus.db.Static
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.PersistentLogger
import org.gkisalatiga.plus.services.ClipManager
import org.gkisalatiga.plus.services.EnableDevMode
import org.gkisalatiga.plus.services.NotificationService
import org.gkisalatiga.plus.services.WorkScheduler
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.abs


class ScreenDev (private val current : ActivityData) : ComponentActivity() {

    private val ctx = current.ctx

    // The main JSON root -> backend node.
    // private val mainRoot = MainCompanion.jsonRoot!!
    private val mainRoot = MainCompanion.api!!
    private val mainRootBackend = mainRoot.backend

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        // It is already done for in ActivityLauncer.
        // LaunchedEffect(Unit) { Beacon(current).logScreenOpen(NavigationRoutes.SCREEN_DEV) }

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
        val appPackageName = current.ctx.packageName

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
                Text(appPackageName, fontSize = 18.sp)
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
        // Disable spacings between Surfaces that have onClick component.
        // SOURCE: https://stackoverflow.com/a/75392447
        CompositionLocalProvider(
            // Deprecated starting on Version 1.3.0-alpha04
            // https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-alpha04
            // LocalMinimumInteractiveComponentEnforcement provides false
            LocalMinimumInteractiveComponentSize provides Dp.Unspecified
        ) {
            getQuickActions()
            Spacer(Modifier.height(20.dp))
            // insert more menu group here.
        }
    }

    @Composable
    @SuppressLint("SimpleDateFormat")
    private fun getQuickActions() {
        val persistentLoggerToastText = stringResource(R.string.screen_dev_text_toast_value_copy)

        /* The quick actions menu. */
        Column (
            Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
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
                    NotificationService.showDebugDataUpdateNotification(ctx)
                    NotificationService.showSarenNotification(ctx)
                    NotificationService.showYKBHarianNotification(ctx)
                }
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(notifTriggerText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* Trigger a WorKManager that launches notification once every 20th second each minute. */
            val triggerMinutelyWorkManagerText = stringResource(R.string.screen_dev_trigger_minutely_work_manager)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    WorkScheduler.scheduleMinutelyDebugReminder(ctx)
                }
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Alarm, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(triggerMinutelyWorkManagerText, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* Trigger the changing of screen's orientation. */
            val triggerOrientationChange = stringResource(R.string.screen_dev_trigger_orientation_change)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    (ctx as Activity).requestedOrientation = abs(ctx.requestedOrientation - 1)
                    GlobalCompanion.isPortraitMode.value = ctx.requestedOrientation == 1
                }
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FlipCameraAndroid, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(triggerOrientationChange, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            /* Locks the developer menu. */
            val lockDeveloperMenu = stringResource(R.string.screen_dev_lock_dev_menu_change)
            Surface(
                modifier = Modifier.fillMaxWidth().padding(0.dp).height(50.dp),
                onClick = {
                    LocalStorage(ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_DEVELOPER_MENU_UNLOCKED, false, LocalStorageDataTypes.BOOLEAN)
                    PersistentLogger(ctx).write({}, "The developer menu was locked!")
                    EnableDevMode.disableDebugToggles()
                    AppNavigation.popBack()
                }
            ) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, "", modifier = Modifier.fillMaxHeight().padding(horizontal = 20.dp))
                    Text(lockDeveloperMenu, modifier = Modifier, textAlign = TextAlign.Center)
                }
            }

            val appLocalText = stringResource(R.string.screen_dev_local_storage_title)
            Spacer(Modifier.height(10.dp))
            Text(appLocalText, fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Displaying all LocalStorage values. */
            LocalStorage(ctx).getAll().entries.sortedBy { it.key }.forEach {
                val text1 = it.key
                val text2 = "${it.value}"
                Surface (
                    modifier = Modifier.fillMaxSize().padding(2.dp),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        // Copy the persistent logger value.
                        val clipData = ClipData.newPlainText("text", "$text1 $text2")
                        ClipManager.clipManager!!.setPrimaryClip(clipData)
                        Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Column (Modifier.fillMaxWidth().padding(2.dp)) {
                        Text(text1, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                        Text(text2, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                    }
                }
            }

            val appPrefText = stringResource(R.string.screen_dev_app_preferences_title)
            Spacer(Modifier.height(10.dp))
            Text(appPrefText, fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Displaying all AppPreferences values. */
            AppPreferences(ctx).getAll().entries.sortedBy { it.key }.forEach {
                val text1 = it.key
                val text2 = "${it.value}"
                Surface (
                    modifier = Modifier.fillMaxSize().padding(2.dp),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        // Copy the persistent logger value.
                        val clipData = ClipData.newPlainText("text", "$text1 $text2")
                        ClipManager.clipManager!!.setPrimaryClip(clipData)
                        Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Column (Modifier.fillMaxWidth().padding(2.dp)) {
                        Text(text1, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                        Text(text2, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                    }
                }
            }

            val appDebugFlagsText = stringResource(R.string.screen_dev_debug_flags_title)
            Spacer(Modifier.height(10.dp))
            Text(appDebugFlagsText, fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            // The list of all debug flags.
            val debugFlags : Map<String, Boolean> = mapOf(
                "DEBUG_ENABLE_TOAST" to GlobalCompanion.DEBUG_ENABLE_TOAST,
                "DEBUG_ENABLE_LOG_CAT" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT,
                "DEBUG_ENABLE_LOG_CAT_ANALYTICS" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_ANALYTICS,
                "DEBUG_ENABLE_LOG_CAT_BIBLE" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_BIBLE,
                "DEBUG_ENABLE_LOG_CAT_BOOT" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_BOOT,
                "DEBUG_ENABLE_LOG_CAT_CONN_TEST" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_CONN_TEST,
                "DEBUG_ENABLE_LOG_CAT_DOWNLOADER" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DOWNLOADER,
                "DEBUG_ENABLE_LOG_CAT_DEEP_LINK" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DEEP_LINK,
                "DEBUG_ENABLE_LOG_CAT_DUMP" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DUMP,
                "DEBUG_ENABLE_LOG_CAT_FCM" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_FCM,
                "DEBUG_ENABLE_LOG_CAT_INIT" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_INIT,
                "DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE,
                "DEBUG_ENABLE_LOG_CAT_PDF" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PDF,
                "DEBUG_ENABLE_LOG_CAT_PERSISTENT_LOGGER" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PERSISTENT_LOGGER,
                "DEBUG_ENABLE_LOG_CAT_PREFERENCES" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PREFERENCES,
                "DEBUG_ENABLE_LOG_CAT_RAPID_TEST" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_RAPID_TEST,
                "DEBUG_ENABLE_LOG_CAT_TEST" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_TEST,
                "DEBUG_ENABLE_LOG_CAT_UPDATER" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_UPDATER,
                "DEBUG_ENABLE_LOG_CAT_WORKER" to GlobalCompanion.DEBUG_ENABLE_LOG_CAT_WORKER,
                "DEBUG_SHOW_INFO_PDF_LOCAL_PATH_INFO" to GlobalCompanion.DEBUG_SHOW_INFO_DOC_LOCAL_PATH_INFO,
            )

            /* Displaying all of the app's debug flag values. */
            debugFlags.entries.sortedBy { it.key }.forEach {
                val text1 = it.key
                val text2 = "${it.value}"
                Surface (
                    modifier = Modifier.fillMaxSize().padding(2.dp),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        // Copy the persistent logger value.
                        val clipData = ClipData.newPlainText("text", "$text1 $text2")
                        ClipManager.clipManager!!.setPrimaryClip(clipData)
                        Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Column (Modifier.fillMaxWidth().padding(2.dp)) {
                        Text(text1, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                        Text(text2, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                    }
                }
            }

            val appGlobalCompBackendText = stringResource(R.string.screen_dev_global_comp_backend_title)
            Spacer(Modifier.height(10.dp))
            Text(appGlobalCompBackendText, fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Displaying the read-out of the JSON backend flags, strings, etc. */
            mapOf(
                "isAppDebuggable" to GlobalCompanion.isAppDebuggable,
                "isAppUpdateAvailable" to GlobalCompanion.isAppUpdateAvailable,
                "isDarkModeUi" to GlobalCompanion.isDarkModeUi,
                "isConnectedToInternet" to GlobalCompanion.isConnectedToInternet,
                "isNotificationGranted" to GlobalCompanion.isNotificationGranted,
                "isPhoneBarsVisible" to GlobalCompanion.isPhoneBarsVisible,
                "isPortraitMode" to GlobalCompanion.isPortraitMode,
                "isRunningInBackground" to GlobalCompanion.isRunningInBackground,
                "lastAppUpdateVersionName" to GlobalCompanion.lastAppUpdateVersionName,
            ).let {
                it.keys.forEach { key ->
                    val text1 = "GlobalCompanion.$key"
                    val text2 = it[key]!!.value.toString()
                    Surface (
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            // Copy the persistent logger value.
                            val clipData = ClipData.newPlainText("text", "$text1 $text2")
                            ClipManager.clipManager!!.setPrimaryClip(clipData)
                            Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Column (Modifier.fillMaxWidth().padding(2.dp)) {
                            Text(text1, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                            Text(text2, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                        }
                    }
                }
            }

            val appJsonBackendText = stringResource(R.string.screen_dev_main_json_backend_title)
            Spacer(Modifier.height(10.dp))
            Text(appJsonBackendText, fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Displaying the read-out of the JSON backend flags, strings, etc. */
            mapOf(
                "isFeatureSeasonalShown" to mainRootBackend.flags.isFeatureSeasonalShown,
                "isFeatureLapakShown" to mainRootBackend.flags.isFeatureLapakShown,
                "isFeatureLibraryShown" to mainRootBackend.flags.isFeatureLibraryShown,
                "isFeatureBibleShown" to mainRootBackend.flags.isFeatureBibleShown,
                "isFeatureFormulirShown" to mainRootBackend.flags.isFeatureFormulirShown,
                "isFeatureYKBShown" to mainRootBackend.flags.isFeatureYKBShown,
                "isFeaturePersembahanShown" to mainRootBackend.flags.isFeaturePersembahanShown,
                "isFeatureAgendaShown" to mainRootBackend.flags.isFeatureAgendaShown,
                "isEasterEggDevmodeEnabled" to mainRootBackend.flags.isEasterEggDevmodeEnabled,
                "isFeatureGaleriShown" to mainRootBackend.flags.isFeatureGaleriShown,
            ).let {
                it.keys.forEach { key ->
                    val text1 = "flags ::: $key"
                    val text2 = it[key].toString()
                    Surface (
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            // Copy the persistent logger value.
                            val clipData = ClipData.newPlainText("text", "$text1 $text2")
                            ClipManager.clipManager!!.setPrimaryClip(clipData)
                            Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Column (Modifier.fillMaxWidth().padding(2.dp)) {
                            Text(text1, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                            Text(text2, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                        }
                    }
                }
            }

            val metaGallery = Gallery(current.ctx).getGalleryMetadata()
            val metaMain = Main(current.ctx).getMainMetadata()
            val metaModules = Modules(current.ctx).getModulesMetadata()
            val metaStatic = Static(current.ctx).getStaticMetadata()
            mapOf(
                "gallery-last-actor" to metaGallery.lastActor,
                "gallery-last-update" to metaGallery.lastUpdate,
                "gallery-update-count" to metaGallery.updateCount,
                "gallery-version" to metaGallery.schemaVersion,
                "main-last-actor" to metaMain.lastActor,
                "main-last-update" to metaMain.lastUpdate,
                "main-update-count" to metaMain.updateCount,
                "main-version" to metaMain.schemaVersion,
                "modules-last-actor" to metaModules.lastActor,
                "modules-last-update" to metaModules.lastUpdate,
                "modules-update-count" to metaModules.updateCount,
                "modules-version" to metaModules.schemaVersion,
                "static-last-actor" to metaStatic.lastActor,
                "static-last-update" to metaStatic.lastUpdate,
                "static-update-count" to metaStatic.updateCount,
                "static-version" to metaStatic.schemaVersion,
            ).let {
                it.keys.forEach { key ->
                    val text1 = "meta ::: $key"
                    val text2 = it[key].toString()
                    Surface (
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            // Copy the persistent logger value.
                            val clipData = ClipData.newPlainText("text", "$text1 $text2")
                            ClipManager.clipManager!!.setPrimaryClip(clipData)
                            Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Column (Modifier.fillMaxWidth().padding(2.dp)) {
                            Text(text1, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                            Text(text2, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                        }
                    }
                }
            }

            mapOf(
                "aboutContactMail" to mainRootBackend.strings.aboutContactMail,
                "greetingsBottom" to mainRootBackend.strings.greetingsBottom,
                "greetingsTop" to mainRootBackend.strings.greetingsTop,
                "aboutSourceCodeUrl" to mainRootBackend.strings.aboutSourceCodeUrl,
                "aboutGooglePlayListingUrl" to mainRootBackend.strings.aboutGooglePlayListingUrl,
                "aboutChangelogUrl" to mainRootBackend.strings.aboutChangelogUrl,
                "address" to mainRootBackend.strings.address,
            ).let {
                it.keys.forEach { key ->
                    val text1 = "strings ::: $key"
                    val text2 = "${it[key]}"
                    Surface (
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        shape = RoundedCornerShape(5.dp),
                        onClick = {
                            // Copy the persistent logger value.
                            val clipData = ClipData.newPlainText("text", "$text1 $text2")
                            ClipManager.clipManager!!.setPrimaryClip(clipData)
                            Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Column (Modifier.fillMaxWidth().padding(2.dp)) {
                            Text(text1, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                            Text(text2, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                        }
                    }
                }
            }

            val appPersistText = stringResource(R.string.screen_dev_persistent_logger_title)
            Spacer(Modifier.height(10.dp))
            Text(appPersistText, fontWeight = FontWeight.Bold, fontSize = 20.sp, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(10.dp))

            /* Displaying all PersistentLogger entries. */
            PersistentLogger(ctx).getAll().entries.sortedBy { it.key.toLong() }.forEach {
                val dateString = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date(it.key.toLong()))
                Surface (
                    modifier = Modifier.fillMaxSize().padding(2.dp),
                    shape = RoundedCornerShape(5.dp),
                    onClick = {
                        // Copy the persistent logger value.
                        val clipData = ClipData.newPlainText("text", "$dateString ${it.value}")
                        ClipManager.clipManager!!.setPrimaryClip(clipData)
                        Toast.makeText(current.ctx, persistentLoggerToastText, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Column (Modifier.fillMaxWidth().padding(2.dp)) {
                        Text(dateString, fontWeight = FontWeight.Bold, textAlign = TextAlign.Start, fontSize = 10.sp, lineHeight = 11.sp)
                        Text("${it.value}", fontWeight = FontWeight.Normal, textAlign = TextAlign.Start, fontSize = 8.sp, lineHeight = 9.sp)
                    }
                }
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