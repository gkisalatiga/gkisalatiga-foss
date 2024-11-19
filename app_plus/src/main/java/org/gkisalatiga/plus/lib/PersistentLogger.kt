/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Persistently store log messages and debug information in the app's internal storage.
 */

package org.gkisalatiga.plus.lib

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class PersistentLogger(private val ctx: Context) {
    private val persistLogObj = ctx.getSharedPreferences(PersistentLoggerCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)

    /**
     * This function cleans up the log entries longer than the specified expiration day.
     * TODO: Not yet called upon.
     */
    fun cleanOldEntries() {
        val prefKeepDaysOfPersistentLoggerEntries = AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_PERSISTENT_LOG_ENTRIES) as Long
        getAll().entries.forEach {
            it.key.toLong().let { l ->
                if (l + prefKeepDaysOfPersistentLoggerEntries <= System.currentTimeMillis())
                    persistLogObj.edit().remove(it.key).apply()
            }
        }
    }

    /**
     * Returns the map of all key-value pair of this SharedPrefereces.
     */
    fun getAll() : Map<String, Any?> {
        return persistLogObj.all
    }

    /**
     * Writing a given log message to the persistent log file.
     * @param func Must be set to "{}" in order to correctly back-trace the caller method's enclosing class and method names.
     * @param msg The message to be logged to the terminal.
     */
    fun write(func: () -> Unit, msg: String) {
        val keyNow = System.currentTimeMillis().toString()
        val msgString = "[${func.javaClass.enclosingClass?.name}.${func.javaClass.enclosingMethod?.name}] ::: $msg"
        Logger.logPersistentLogger({}, msg, LoggerType.INFO)

        // Write the persistent log message entry.
        persistLogObj.edit().putString(keyNow, msgString).apply()
    }

}

/**
 * This companion class stores every static information related to this class file.
 * It is globally readable, exposing any values assigned to public variables stored in it.
 */
class PersistentLoggerCompanion : Application () {
    companion object {
        const val NAME_SHARED_PREFERENCES: String = "org.gkisalatiga.GKISPLUS_PERSISTENT_LOGGER"
    }
}
