/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Handles app logging and manipulates the output of the log cat and debug toasts.
 */

package org.gkisalatiga.plus.lib

import android.util.Log
import org.gkisalatiga.plus.global.GlobalCompanion

/**
 * Perform the back-end logging to the logcat.
 * @author Samarthya Lykamanuella (github.com/groaking)
 */
class Logger {
    companion object {
        /* The base tag that will be used in all sorts of logging of this app.
         * It is set to private because other functions should not bother with debug loggings.
         */
        private const val BASE_LOGGING_TAG = "Groaker"

        /**
         * Actually do the back-end logging.
         * @param tag The tag of the log.
         * @param msgString The message to be logged.
         * @param type The type of the logging.
         */
        private fun doLog (tag: String, msgString: String, type: LoggerType = LoggerType.DEBUG) {
            when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
        }

        /**
         * General terminal logging.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun log (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT) doLog(tag, msgString, type)
        }

        /**
         * Logging for "on boot signal received"-related operations.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logBoot (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-Boot"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_BOOT) doLog(tag, msgString, type)
        }

        /**
         * Logging for internet connection-testing operations.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logConnTest (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-CT"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_CONN_TEST) doLog(tag, msgString, type)
        }

        /**
         * Logging for the app's downloader process.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logDownloader (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-DL"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DOWNLOADER) doLog(tag, msgString, type)
        }

        /**
         * Dumping strings that are needed for debugging purposes.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logDump (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-Dump"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: DUMPING LOG MESSAGE:\n$msg\n::: END OF DUMP"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_DUMP) doLog(tag, msgString, type)
        }

        /**
         * Logging of operations during application start/initialization.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logInit (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-Init"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_INIT) doLog(tag, msgString, type)
        }

        /**
         * Logging of operations during local storage accessions.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logLocalStorage (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-LS"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_LOCAL_STORAGE) doLog(tag, msgString, type)
        }

        /**
         * Log messages sent for the internal PDF viewer of GKI Salatiga app.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logPDF (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-PDF"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PDF) doLog(tag, msgString, type)
        }

        /**
         * Log messages sent for the purpose of debugging app preferences.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logPrefs (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-Preferences"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_PREFERENCES) doLog(tag, msgString, type)
        }

        /**
         * Short logging messages sent rapidly across successive time to test out time-critical operations.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logRapidTest (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-RT"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_RAPID_TEST) doLog(tag, msgString, type)
        }

        /**
         * Log messages for testing a new feature or novel operation.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logTest (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-Test"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_TEST) doLog(tag, msgString, type)
        }

        /**
         * Log messages triggered during data refresh and updates.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logUpdate (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-Update"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_UPDATER) doLog(tag, msgString, type)
        }

        /**
         * Log messages sent during WorkManager operations.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun logWorker (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG-Worker"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalCompanion.DEBUG_ENABLE_LOG_CAT_WORKER) doLog(tag, msgString, type)
        }
    }
}

/**
 * Whether to send "verbose", "debug", "error", "info", or "warning" message type to the logcat output.
 * This enum class specifies which logging method (and thus output color) that the logger should use.
 * @author Samarthya Lykamanuella (github.com/groaking)
 */
enum class LoggerType {
    DEBUG,
    ERROR,
    INFO,
    VERBOSE,
    WARNING,
}
