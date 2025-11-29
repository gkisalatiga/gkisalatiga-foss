/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 *
 * Implements a global declaration of variables, which can be accessed across classes.
 * SOURCE: https://tutorial.eyehunts.com/android/declare-android-global-variable-kotlin-example/
 * SOURCE: https://stackoverflow.com/a/52844621
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.global

import android.app.Application
import androidx.compose.runtime.mutableStateOf

class GlobalCompanion : Application() {

    // Initializing the data schema of the app that will be shared across composables
    // and that will course the navigation of screens.
    companion object {

        /* ------------------------------------------------------------------------------------ */
        /* Debugging toggles that must be activated by enabling the developer mode. */

        // Whether to display the debugger's toast.
        var DEBUG_ENABLE_TOAST = false

        // Whether to display the debugger's logcat logging.
        var DEBUG_ENABLE_LOG_CAT = false
        var DEBUG_ENABLE_LOG_CAT_ANALYTICS = false
        var DEBUG_ENABLE_LOG_CAT_BIBLE = false
        var DEBUG_ENABLE_LOG_CAT_BOOT = false
        var DEBUG_ENABLE_LOG_CAT_CONN_TEST = false
        var DEBUG_ENABLE_LOG_CAT_DOWNLOADER = false
        var DEBUG_ENABLE_LOG_CAT_DEEP_LINK = false
        var DEBUG_ENABLE_LOG_CAT_DUMP = false
        var DEBUG_ENABLE_LOG_CAT_FCM = false
        var DEBUG_ENABLE_LOG_CAT_INIT = false
        var DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE = false
        var DEBUG_ENABLE_LOG_CAT_PDF = false
        var DEBUG_ENABLE_LOG_CAT_PERSISTENT_LOGGER = false
        var DEBUG_ENABLE_LOG_CAT_PREFERENCES = false
        var DEBUG_ENABLE_LOG_CAT_RAPID_TEST = false
        var DEBUG_ENABLE_LOG_CAT_TEST = false
        var DEBUG_ENABLE_LOG_CAT_UPDATER = false
        var DEBUG_ENABLE_LOG_CAT_WORKER = false

        // Whether to display extraneous information in various screens.
        var DEBUG_SHOW_INFO_DOC_LOCAL_PATH_INFO = false

        // Whether to display notification when updating the JSON data in the background.
        var DEBUG_SHOW_DATA_UPDATER_NOTIFICATION = false

        /* ------------------------------------------------------------------------------------ */
        /* Global states of the app. */

        // The status of internet connection.
        val isConnectedToInternet = mutableStateOf(false)

        // Whether we are in the dark mode.
        val isDarkModeUi = mutableStateOf(false)

        // Current app's bars (both status bar and navigation bar) state of visibility.
        val isPhoneBarsVisible = mutableStateOf(true)

        // Current app's screen orientation.
        val isPortraitMode = mutableStateOf(true)

        // Whether the app is running in background.
        val isRunningInBackground = mutableStateOf(false)

        // Whether notification permission has already been granted.
        val isNotificationGranted = mutableStateOf(false)

        // Whether new app update is found.
        val isAppUpdateAvailable = mutableStateOf(false)

        // Whether the app is debuggable.
        val isAppDebuggable = mutableStateOf(false)

        // The latest version update as specified in the feeds.
        val lastAppUpdateVersionName = mutableStateOf("")

    }
}