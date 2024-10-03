/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's internally saved preferences.
 * SOURCE: https://developer.android.com/training/data-storage/shared-preferences
 */

package org.gkisalatiga.plus.lib

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import org.gkisalatiga.plus.global.GlobalSchema

class AppPreferences(private val ctx: Context) {

    /**
     * Returns the preference object.
     */
    private fun getPrefObj(): SharedPreferences {
        return ctx.getSharedPreferences(GlobalSchema.NAME_SHARED_PREFERENCES, MODE_PRIVATE)
    }

    /**
     * Read the saved preferences and store them (temporarily) in the GlobalSchema.
     * Assumes it is ran at the very beginning of the app, before the GlobalSchema is modifier
     * by any other function.
     */
    public fun readAllPreferences() {

        // Creates the shared preferences object.
        val prefObj = getPrefObj()

        // Assign each preference in the map individually.
        for (l in GlobalSchema.preferencesKeyValuePairs.keys) {
            // The default value.
            val def = GlobalSchema.preferencesKeyValuePairs[l]

            // Reading the shared preferences.
            if (def!!::class == Long::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getLong(l, def as Long)
            } else if (def!!::class == Int::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getInt(l, def as Int)
            } else if (def!!::class == String::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getString(l, def as String)!!
            } else if (def!!::class == Boolean::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getBoolean(l, def as Boolean)
            } else if (def!!::class == Float::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getFloat(l, def as Float)
            }

        }
    }

    /**
     * Writing a given saved preference according to the passed key.
     * @param prefKey the preference key to refer to.
     * @param prefValue the value to be saved.
     */
    public fun writePreference(prefKey: String, prefValue: Any) {
        // Creates the shared preferences object.
        val prefObj = getPrefObj()

        // Save the value also to the global schema.
        GlobalSchema.preferencesKeyValuePairs[prefKey] = prefValue

        // Write the preference values.
        with (prefObj.edit()) {

            if (prefValue!!::class == Long::class) {
                putLong(prefKey, prefValue as Long)
            } else if (prefValue!!::class == Int::class) {
                putInt(prefKey, prefValue as Int)
            } else if (prefValue!!::class == String::class) {
                putString(prefKey, prefValue as String)
            } else if (prefValue!!::class == Boolean::class) {
                putBoolean(prefKey, prefValue as Boolean)
            } else if (prefValue!!::class == Float::class) {
                putFloat(prefKey, prefValue as Float)
            }

            apply()
        }
    }

}