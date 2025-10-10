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

package org.gkisalatiga.plus.services

import org.gkisalatiga.plus.global.GlobalCompanion

/**
 * Enabling debug toggles and other developer options.
 */
class EnableDevMode {
    companion object {
        fun activateDebugToggles() {
            GlobalCompanion.DEBUG_ENABLE_TOAST = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_ANALYTICS = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_BOOT = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_CONN_TEST = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DOWNLOADER = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DEEP_LINK = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DUMP = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_FCM = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_INIT = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PDF = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PERSISTENT_LOGGER = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PREFERENCES = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_RAPID_TEST = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_TEST = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_UPDATER = true
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_WORKER = true
            GlobalCompanion.DEBUG_SHOW_DATA_UPDATER_NOTIFICATION = true
            GlobalCompanion.DEBUG_SHOW_INFO_PDF_LOCAL_PATH_INFO = true
        }

        fun disableDebugToggles() {
            GlobalCompanion.DEBUG_ENABLE_TOAST = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_ANALYTICS = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_BOOT = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_CONN_TEST = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DOWNLOADER = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DEEP_LINK = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DUMP = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_FCM = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_INIT = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PDF = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PERSISTENT_LOGGER = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PREFERENCES = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_RAPID_TEST = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_TEST = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_UPDATER = false
            GlobalCompanion.DEBUG_ENABLE_LOG_CAT_WORKER = false
            GlobalCompanion.DEBUG_SHOW_DATA_UPDATER_NOTIFICATION = false
            GlobalCompanion.DEBUG_SHOW_INFO_PDF_LOCAL_PATH_INFO = false
        }
    }
}