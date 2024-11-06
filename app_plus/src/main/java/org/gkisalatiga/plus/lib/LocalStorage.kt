/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's internally stored local storage, persistent across launches.
 * SOURCE: https://developer.android.com/training/data-storage/shared-preferences
 */

package org.gkisalatiga.plus.lib

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class LocalStorage(private val ctx: Context) {
    private val localStorageObj = ctx.getSharedPreferences(LocalStorageCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)

    /* The default custom key value. */
    private val DEFAULT_CUSTOM_KEY_STRING = ""

    /**
     * Returns the composite key if a given custom key is passed for a LocalStorageKeys.
     * @param localKey the base LocalStorageKeys to be considered.
     * @param customKey the custom key which will compound the base localKey.
     */
    fun getCompositeKey(localKey: LocalStorageKeys, customKey: String = DEFAULT_CUSTOM_KEY_STRING): String {
        return localKey.name + LocalStorageCompanion.COMPOSITE_KEY_SEPARATOR + customKey
    }

    /**
     * Returns the LocalStorage object.
     * It must be set to private so that only this class may be allowed to alter
     * the app's internal local storage.
     */
    @Deprecated("Substituted by the constructor's `localStorageObj` variable, automatically assigned upon class instantiation.")
    private fun getLocalStorageObj(): SharedPreferences {
        return ctx.getSharedPreferences(LocalStorageCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)
    }

    /**
     * Get the saved value of a given local storage key, on an individual basis.
     * @param localKey the local storage key to refer to.
     * @param customKey any additional custom string that will be appended as the local storage key. (default to empty string)
     * @param type the data type of the value that will be retrieved.
     * @return The value stored by the local storage key, if the key exists. Otherwise returns null.
     */
    fun getLocalStorageValue(localKey: LocalStorageKeys, type: LocalStorageDataTypes, customKey: String = DEFAULT_CUSTOM_KEY_STRING): Any? {
        // The returned value.
        var retVal: Any? = null

        // The combined key.
        val key = getCompositeKey(localKey, customKey)

        // Initializing the default values to the local storage store.
        with (localStorageObj) {
            retVal = when (type) {
                LocalStorageDataTypes.BOOLEAN -> getBoolean(key, LocalStorageCompanion.DEFAULT_BOOLEAN_VALUE)
                LocalStorageDataTypes.FLOAT -> getFloat(key, LocalStorageCompanion.DEFAULT_FLOAT_VALUE)
                LocalStorageDataTypes.INT -> getInt(key, LocalStorageCompanion.DEFAULT_INT_VALUE)
                LocalStorageDataTypes.LONG -> getLong(key, LocalStorageCompanion.DEFAULT_LONG_VALUE)
                LocalStorageDataTypes.STRING -> getString(key, LocalStorageCompanion.DEFAULT_STRING_VALUE)
            }
        }

        // Debug the default local storage's type.
        Logger.logTest({}, "getLocalStorageValue -> type.name: ${type.name}, key: $key, retVal: $retVal")

        // Hand over the local storage value the caller asks for.
        return retVal
    }

    /**
     * Writing a given saved local storage according to the passed key.
     * @param localKey the local storage key to refer to.
     * @param localStorageValue the value to be saved. Must be either: float, int, long, string, or boolean.
     * @param customKey any additional custom string that will be appended as the local storage key. (default to empty string)
     * @param type the data type of the value that will be retrieved.
     */
    fun setLocalStorageValue(localKey: LocalStorageKeys, localStorageValue: Any, type: LocalStorageDataTypes, customKey: String = DEFAULT_CUSTOM_KEY_STRING) {
        // The combined key.
        val key = getCompositeKey(localKey, customKey)

        // Debug the local storage key-to-write value.
        Logger.logTest({}, "setLocalStorageValue -> localStorageValue: [$localStorageValue], key: [$key], type: [$type]")

        with (localStorageObj.edit()) {
            // Detect local storage value type.
            when (type) {
                LocalStorageDataTypes.BOOLEAN -> putBoolean(key, localStorageValue as Boolean)
                LocalStorageDataTypes.FLOAT -> putFloat(key, localStorageValue as Float)
                LocalStorageDataTypes.INT -> putInt(key, localStorageValue as Int)
                LocalStorageDataTypes.LONG -> putLong(key, localStorageValue as Long)
                LocalStorageDataTypes.STRING -> putString(key, localStorageValue as String)
            }

            // Write the local storage values.
            apply()
        }
    }

}

/**
 * This companion class stores every static information related to this class file.
 * It is globally readable, exposing any values assigned to public variables stored in it.
 */
class LocalStorageCompanion : Application () {
    companion object {
        const val NAME_SHARED_PREFERENCES: String = "org.gkisalatiga.GKISPLUS_LOCAL_STORAGE"

        /* The default values of each primitive value class. */
        const val DEFAULT_BOOLEAN_VALUE = false
        const val DEFAULT_FLOAT_VALUE = 0.0f
        const val DEFAULT_INT_VALUE = 0.toInt()
        const val DEFAULT_LONG_VALUE = 0.toLong()
        const val DEFAULT_STRING_VALUE = ""

        /* String to separate LocalStorageKeys from getter key in a composite LocalStorage key.
         * The string was chosen arbitrarily.
         * DO NOT CHANGE IN FUTURE RELEASE! MAY BREAK IF UPDATED */
        const val COMPOSITE_KEY_SEPARATOR = "*8<@"
    }
}

/**
 * This class is the keying class for all local storage in this app.
 * Each preference bears its own key, represented by the following enum object.
 */
enum class LocalStorageKeys {
    /* Generic key that does not require additional custom key. */
    LOCAL_KEY_LAST_OPENED_PDF_CACHES,
    LOCAL_KEY_LAST_STATIC_DATA_UPDATE,
    LOCAL_KEY_LAST_CAROUSEL_BANNER_UPDATE,
    LOCAL_KEY_LAUNCH_COUNTS,
    LOCAL_KEY_LIST_OF_DOWNLOADED_PDF_CACHES,

    /* More sophisticated local storage keys that require the use of customKey. */
    LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION,
    LOCAL_KEY_IS_PDF_FILE_DOWNLOADED,
}

/**
 * This class determines the type of data to be stored in the local storage.
 * i.e., boolean, float, integer, long, and string.
 */
enum class LocalStorageDataTypes {
    BOOLEAN,
    FLOAT,
    INT,
    LONG,
    STRING
}