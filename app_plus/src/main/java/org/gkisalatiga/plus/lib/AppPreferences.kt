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

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class AppPreferences(private val ctx: Context) {
    private val prefObj = ctx.getSharedPreferences(AppPreferencesCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)

    /**
     * The actual values of a given preference item value.
     * The mapped region must be of string, int, float, boolean, or long types.
     */
    private val ACTUAL_PREFERENCE_ITEM_VALUES: Map<PreferenceSettingItem, Any> = mapOf(
        PreferenceSettingItem.PREF_VAL_PDF_QUALITY_BEST to 4.toInt(),
        PreferenceSettingItem.PREF_VAL_PDF_QUALITY_HIGH to 3.toInt(),
        PreferenceSettingItem.PREF_VAL_PDF_QUALITY_LOW to 1.toInt(),
        PreferenceSettingItem.PREF_VAL_PDF_QUALITY_MEDIUM to 2.toInt(),
        PreferenceSettingItem.PREF_VAL_PDF_REMOVE_ALWAYS to 86400000L.toLong(),  // --- 1 day.
        PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_7 to 604800000L.toLong(),  // --- 7 days.
        PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_14 to 1209600000L.toLong(),  // --- 14 days.
        PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_30 to 2592000000L.toLong(),  // --- 30 days.
        PreferenceSettingItem.PREF_VAL_PDF_REMOVE_NEVER to 31536000000L.toLong(),  // --- 365 days, not actually "never"!
        PreferenceSettingItem.PREF_VAL_YOUTUBE_UI_NEW to true,
        PreferenceSettingItem.PREF_VAL_YOUTUBE_UI_OLD to false,
    )

    /**
     * The default values of preference keys.
     */
    private val DEFAULT_PREFERENCE_KEY_VALUES: Map<PreferenceKeys, Any> = mapOf(
        PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_PERSISTENT_LOG_ENTRIES to 2592000000L.toLong(),  // --- 30 days.
        PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES to ACTUAL_PREFERENCE_ITEM_VALUES[PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_14]!! as Long,
        PreferenceKeys.PREF_KEY_OFFLINE_CHECK_FREQUENCY to 10000L.toLong(),  // --- 10 seconds.
        PreferenceKeys.PREF_KEY_PDF_RENDER_QUALITY_FACTOR to 2.toInt(),
        PreferenceKeys.PREF_KEY_YOUTUBE_UI_THEME to true,
    )

    /**
     * Returns the map of all key-value pair of this SharedPrefereces.
     */
    fun getAll() : Map<String, Any?> {
        return prefObj.all
    }

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
            Logger.logPrefs({}, "Type of defaultVal = ${defaultVal::class.qualifiedName}, key = $prefKey, valued = $defaultVal")

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
     * Get the actual value of a given preference settings item.
     * @return the actual value of the associated [PreferenceSettingItem]
     */
    fun getActualItemValue(preferenceSettingValues: PreferenceSettingItem) : Any? {
        return if (ACTUAL_PREFERENCE_ITEM_VALUES.keys.contains(preferenceSettingValues)) {
            ACTUAL_PREFERENCE_ITEM_VALUES[preferenceSettingValues]
        } else null
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
        Logger.logPrefs({}, "getPreferenceValue -> defaultVal type: ${defaultVal::class.qualifiedName}, key: $prefKey, defaultVal: $defaultVal, retVal: $retVal")

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
        Logger.logPrefs({}, "setPreferenceValue -> prefValue: $prefValue, prefKey: ${prefKey.name}, prefKey class: ${prefValue::class.qualifiedName}")

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
    PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES,
    PREF_KEY_KEEP_DAYS_OF_PERSISTENT_LOG_ENTRIES,
    PREF_KEY_OFFLINE_CHECK_FREQUENCY,
    PREF_KEY_PDF_RENDER_QUALITY_FACTOR,
    PREF_KEY_YOUTUBE_UI_THEME,
}

/**
 * This class stores every possible settings value assigned to a given setting key.
 */
enum class PreferenceSettingItem {
    PREF_VAL_PDF_QUALITY_BEST,
    PREF_VAL_PDF_QUALITY_HIGH,
    PREF_VAL_PDF_QUALITY_LOW,
    PREF_VAL_PDF_QUALITY_MEDIUM,
    PREF_VAL_PDF_REMOVE_ALWAYS,
    PREF_VAL_PDF_REMOVE_DAY_7,
    PREF_VAL_PDF_REMOVE_DAY_14,
    PREF_VAL_PDF_REMOVE_DAY_30,
    PREF_VAL_PDF_REMOVE_NEVER,
    PREF_VAL_YOUTUBE_UI_NEW,
    PREF_VAL_YOUTUBE_UI_OLD,
}