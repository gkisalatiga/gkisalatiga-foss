/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's internally saved preferences.
 * SOURCE: https://developer.android.com/training/data-storage/shared-preferences
 */

package org.gkisalatiga.fdroid.lib

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import org.gkisalatiga.fdroid.global.GlobalSchema

// TODO: Remove these obsolete code blocks.
/*
import org.gkisalatiga.fdroid.global.GlobalSchema
import org.gkisalatiga.fdroid.global.GlobalSchema.Companion.PREF_KEY_CAROUSEL_BANNER_UPDATE_FREQUENCY
import org.gkisalatiga.fdroid.global.GlobalSchema.Companion.PREF_KEY_LAST_CAROUSEL_BANNER_UPDATE
import org.gkisalatiga.fdroid.global.GlobalSchema.Companion.PREF_KEY_LAST_STATIC_DATA_UPDATE
import org.gkisalatiga.fdroid.global.GlobalSchema.Companion.PREF_KEY_LAUNCH_COUNTS
import org.gkisalatiga.fdroid.global.GlobalSchema.Companion.PREF_KEY_STATIC_DATA_UPDATE_FREQUENCY*/
/*class AppPreferences(private val ctx: Context) {

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
    fun readAllPreferences() {

        // Creates the shared preferences object.
        val prefObj = getPrefObj()

        // Assign each preference in the map individually.
        for (l in GlobalSchema.preferencesKeyValuePairs.keys) {
            // The default value.
            val def = GlobalSchema.preferencesKeyValuePairs[l]

            // Reading the shared preferences.
            if (def!!::class == Long::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getLong(l, def as Long)
            } else if (def::class == Int::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getInt(l, def as Int)
            } else if (def::class == String::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getString(l, def as String)!!
            } else if (def::class == Boolean::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getBoolean(l, def as Boolean)
            } else if (def::class == Float::class) {
                GlobalSchema.preferencesKeyValuePairs[l] = prefObj.getFloat(l, def as Float)
            }

        }
    }

    /**
     * Writing a given saved preference according to the passed key.
     * @param prefKey the preference key to refer to.
     * @param prefValue the value to be saved.
     */
    fun writePreference(prefKey: String, prefValue: Any) {
        // Creates the shared preferences object.
        val prefObj = getPrefObj()

        // Save the value also to the global schema.
        GlobalSchema.preferencesKeyValuePairs[prefKey] = prefValue

        // Write the preference values.
        with (prefObj.edit()) {

            if (prefValue::class == Long::class) {
                putLong(prefKey, prefValue as Long)
            } else if (prefValue::class == Int::class) {
                putInt(prefKey, prefValue as Int)
            } else if (prefValue::class == String::class) {
                putString(prefKey, prefValue as String)
            } else if (prefValue::class == Boolean::class) {
                putBoolean(prefKey, prefValue as Boolean)
            } else if (prefValue::class == Float::class) {
                putFloat(prefKey, prefValue as Float)
            }

            apply()
        }
    }

}*/

class AppPreferences(private val ctx: Context) {
    private val prefObj = ctx.getSharedPreferences(AppPreferencesCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)

    /**
     * The default values of preference keys.]
     */
    private val DEFAULT_PREFERENCE_KEY_VALUES: Map<PreferenceKeys, Any> = mapOf(
        PreferenceKeys.PREF_KEY_STATIC_DATA_UPDATE_FREQUENCY to 604800000.toLong(),  // --- 604800000 means "once every 7 days" in millisecond
        PreferenceKeys.PREF_KEY_CAROUSEL_BANNER_UPDATE_FREQUENCY to 86400000.toLong(),  // --- 86400000 means "once every 1 day" in millisecond
        PreferenceKeys.PREF_KEY_LAST_STATIC_DATA_UPDATE to Long.MIN_VALUE,
        PreferenceKeys.PREF_KEY_LAST_CAROUSEL_BANNER_UPDATE to Long.MIN_VALUE,
        PreferenceKeys.PREF_KEY_LAUNCH_COUNTS to -1.toInt()
    )

    private val abc = mapOf("" to listOf(Long::class, Long.MAX_VALUE))

    /**
     * Returns the preference object.
     * It must be set to private so that only this class may be allowed to alter
     * the app's internal preferences.
     */
    @Deprecated("Substituted by the constructor's `prefObj` variable, automatically assigned upon class instantiation.")
    private fun getPrefObj(): SharedPreferences {
        return ctx.getSharedPreferences(AppPreferencesCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)
    }

    /**
     * Initializes the default preference values.
     * Useful during first launch where preferences are unset.
     */
    fun initDefaultPreferences() {
        // Assign each preference in the map individually.
        for (prefKey in DEFAULT_PREFERENCE_KEY_VALUES.keys) {
            // The default value, as declared in the default preference key value.
            val defaultVal = DEFAULT_PREFERENCE_KEY_VALUES[prefKey]!!

            // Debug the default preference's type.
            Logger.logTest({}, "Type of defaultVal ${defaultVal::class.qualifiedName}, valued = $defaultVal")

            // Initializing the default values to the preference store.
            with (prefObj.edit()) {
                if (defaultVal::class == Long::class) putLong(prefKey.name, defaultVal as Long)
                else if (defaultVal::class == Int::class) putInt(prefKey.name, defaultVal as Int)
                else if (defaultVal::class == String::class) putString(prefKey.name, defaultVal as String)
                else if (defaultVal::class == Boolean::class) putBoolean(prefKey.name, defaultVal as Boolean)
                else if (defaultVal::class == Float::class) putFloat(prefKey.name, defaultVal as Float)

                // Writing the default preference values.
                apply()
            }
        }
    }

    /**
     * Get the saved value of a given preference key, on an individual basis.
     * @param prefKey the preference key to refer to.
     * @return The value stored by the preference key, if the key exists. Otherwise returns null.
     */
    fun getPreferenceValue(prefKey: PreferenceKeys): Any? {
        // The returned value.
        var retVal: Any? = null

        // Find the key's default value, as declared in the default preference key value.
        val defaultVal = DEFAULT_PREFERENCE_KEY_VALUES[prefKey]!!

        // Initializing the default values to the preference store.
        with (prefObj) {
            if (defaultVal::class == Long::class) retVal = getLong(prefKey.name, defaultVal as Long)
            else if (defaultVal::class == Int::class) retVal = getInt(prefKey.name, defaultVal as Int)
            else if (defaultVal::class == String::class) retVal = getString(prefKey.name, defaultVal as String)
            else if (defaultVal::class == Boolean::class) retVal = getBoolean(prefKey.name, defaultVal as Boolean)
            else if (defaultVal::class == Float::class) retVal = getFloat(prefKey.name, defaultVal as Float)
        }

        // Debug the default preference's type.
        Logger.logTest({}, "Type of defaultVal ${defaultVal::class.qualifiedName}, valued = $defaultVal")

        // Hand over the preference value the caller asks for.
        return retVal
    }

    /**
     * Writing a given saved preference according to the passed key.
     * @param prefKey the preference key to refer to.
     * @param prefValue the value to be saved. Must be either: float, int, long, string, or boolean.
     */
    fun setPreferenceValue(prefKey: PreferenceKeys, prefValue: Any) {
        // Debug the preference key-to-write value.
        Logger.logTest({}, "Writing the preference value: $prefValue under the key ${prefKey.name} with class type: ${prefValue::class.qualifiedName}")

        with (prefObj.edit()) {
            // Detect preference value type.
            if (prefValue::class == Long::class) {
                putLong(prefKey.name, prefValue as Long)
            } else if (prefValue::class == Int::class) {
                putInt(prefKey.name, prefValue as Int)
            } else if (prefValue::class == String::class) {
                putString(prefKey.name, prefValue as String)
            } else if (prefValue::class == Boolean::class) {
                putBoolean(prefKey.name, prefValue as Boolean)
            } else if (prefValue::class == Float::class) {
                putFloat(prefKey.name, prefValue as Float)
            }

            // Write the preference values.
            apply()
        }
    }

}

/**
 * This companion class stores every static information related to this class file.
 * It is globally readable, exposing any values assigned to public variables stored in it.
 */
class AppPreferencesCompanion : Application () {
    companion object {
        const val NAME_SHARED_PREFERENCES: String = "org.gkisalatiga.GKISPLUS_SETTINGS"
    }
}

/**
 * This class is the keying class for all preferences in this app.
 * Each preference bears its own key, represented by the following enum object.
 */
enum class PreferenceKeys {
    PREF_KEY_STATIC_DATA_UPDATE_FREQUENCY,
    PREF_KEY_CAROUSEL_BANNER_UPDATE_FREQUENCY,
    PREF_KEY_LAST_STATIC_DATA_UPDATE,
    PREF_KEY_LAST_CAROUSEL_BANNER_UPDATE,
    PREF_KEY_LAUNCH_COUNTS,
}
