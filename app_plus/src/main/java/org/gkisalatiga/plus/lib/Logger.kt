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
import org.gkisalatiga.plus.global.GlobalSchema

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
         * General terminal logging.
         * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
         * @param msg The message to be logged to the terminal.
         * @param type The logging message type to be displayed (whether "verbose", "debug", "info", "error", or "warning").
         */
        fun log (func: () -> Unit, msg: String, type: LoggerType = LoggerType.DEBUG) {
            val tag = "$BASE_LOGGING_TAG"
            val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_BOOT) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_CONN_TEST) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_DUMP) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_INIT) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_RAPID_TEST) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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

            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_WORKER) when (type) {
                LoggerType.DEBUG -> Log.d(tag, msgString)
                LoggerType.ERROR -> Log.e(tag, msgString)
                LoggerType.INFO -> Log.i(tag, msgString)
                LoggerType.VERBOSE -> Log.v(tag, msgString)
                LoggerType.WARNING -> Log.w(tag, msgString)
            }
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
