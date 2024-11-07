/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's internally stored session storage, stored until app closes.
 * SOURCE: https://developer.android.com/training/data-storage/shared-preferences
 */
package org.gkisalatiga.plus.lib

/* TODO: Create Session storage to store data that gets void once the app closes. For storing last opened PDF page.
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SessionStorage(private val ctx: Context) {
    private val sessionStorageObj = ctx.getSharedPreferences(SessionStorageCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)

    /* The default custom key value. */
    private val DEFAULT_CUSTOM_KEY_STRING = ""

    /**
     * Removes all values stored in the previous session and start over.
     */
    fun flushStorage() {

        // Assign each preference in the map individually.
        for (prefKey in DEFAULT_PREFERENCE_KEY_VALUES.keys) {
            // The default value, as declared in the default preference key value.
            val defaultVal = DEFAULT_PREFERENCE_KEY_VALUES[prefKey]!!

            // Debug the default preference's type.
            Logger.logTest({}, "Type of defaultVal = ${defaultVal::class.qualifiedName}, key = $prefKey, valued = $defaultVal")

            // Initializing the default values to the preference store.
            with (prefObj.edit()) {
                if (defaultVal::class == kotlin.Long::class) putLong(prefKey.name, defaultVal as Long)
                else if (defaultVal::class == kotlin.Int::class) putInt(prefKey.name, defaultVal as Int)
                else if (defaultVal::class == kotlin.String::class) putString(prefKey.name, defaultVal as String)
                else if (defaultVal::class == kotlin.Boolean::class) putBoolean(prefKey.name, defaultVal as Boolean)
                else if (defaultVal::class == kotlin.Float::class) putFloat(prefKey.name, defaultVal as Float)

                // Writing the default preference values.
                apply()
            }
        }
    }

    /**
     * Returns the composite key if a given custom key is passed for a SessionStorageKeys.
     * @param sessionKey the base SessionStorageKeys to be considered.
     * @param customKey the custom key which will compound the base sessionKey.
     */
    fun getCompositeKey(sessionKey: SessionStorageKeys, customKey: String = DEFAULT_CUSTOM_KEY_STRING): String {
        return sessionKey.name + SessionStorageCompanion.COMPOSITE_KEY_SEPARATOR + customKey
    }

    /**
     * Returns the SessionStorage object.
     * It must be set to private so that only this class may be allowed to alter
     * the app's internal session storage.
     */
    @Deprecated("Substituted by the constructor's `sessionStorageObj` variable, automatically assigned upon class instantiation.")
    private fun getSessionStorageObj(): SharedPreferences {
        return ctx.getSharedPreferences(SessionStorageCompanion.NAME_SHARED_PREFERENCES, MODE_PRIVATE)
    }

    /**
     * Get the saved value of a given session storage key, on an individual basis.
     * @param sessionKey the session storage key to refer to.
     * @param customKey any additional custom string that will be appended as the session storage key. (default to empty string)
     * @param type the data type of the value that will be retrieved.
     * @return The value stored by the session storage key, if the key exists. Otherwise returns null.
     */
    fun getSessionStorageValue(sessionKey: SessionStorageKeys, type: SessionStorageDataTypes, customKey: String = DEFAULT_CUSTOM_KEY_STRING): Any? {
        // The returned value.
        var retVal: Any? = null

        // The combined key.
        val key = getCompositeKey(sessionKey, customKey)

        // Initializing the default values to the session storage store.
        with (sessionStorageObj) {
            retVal = when (type) {
                SessionStorageDataTypes.BOOLEAN -> getBoolean(key, SessionStorageCompanion.DEFAULT_BOOLEAN_VALUE)
                SessionStorageDataTypes.FLOAT -> getFloat(key, SessionStorageCompanion.DEFAULT_FLOAT_VALUE)
                SessionStorageDataTypes.INT -> getInt(key, SessionStorageCompanion.DEFAULT_INT_VALUE)
                SessionStorageDataTypes.LONG -> getLong(key, SessionStorageCompanion.DEFAULT_LONG_VALUE)
                SessionStorageDataTypes.STRING -> getString(key, SessionStorageCompanion.DEFAULT_STRING_VALUE)
            }
        }

        // Debug the default session storage's type.
        Logger.logTest({}, "getSessionStorageValue -> type.name: ${type.name}, key: $key, retVal: $retVal")

        // Hand over the session storage value the caller asks for.
        return retVal
    }

    /**
     * Writing a given saved session storage according to the passed key.
     * @param sessionKey the session storage key to refer to.
     * @param sessionStorageValue the value to be saved. Must be either: float, int, long, string, or boolean.
     * @param customKey any additional custom string that will be appended as the session storage key. (default to empty string)
     * @param type the data type of the value that will be retrieved.
     */
    fun setSessionStorageValue(sessionKey: SessionStorageKeys, sessionStorageValue: Any, type: SessionStorageDataTypes, customKey: String = DEFAULT_CUSTOM_KEY_STRING) {
        // The combined key.
        val key = getCompositeKey(sessionKey, customKey)

        // Debug the session storage key-to-write value.
        Logger.logTest({}, "setSessionStorageValue -> SessionStorageValue: [$sessionStorageValue], key: [$key], type: [$type]")

        with (sessionStorageObj.edit()) {
            // Detect session storage value type.
            when (type) {
                SessionStorageDataTypes.BOOLEAN -> putBoolean(key, sessionStorageValue as Boolean)
                SessionStorageDataTypes.FLOAT -> putFloat(key, sessionStorageValue as Float)
                SessionStorageDataTypes.INT -> putInt(key, sessionStorageValue as Int)
                SessionStorageDataTypes.LONG -> putLong(key, sessionStorageValue as Long)
                SessionStorageDataTypes.STRING -> putString(key, sessionStorageValue as String)
            }

            // Write the session storage values.
            apply()
        }
    }

}

/**
 * This companion class stores every static information related to this class file.
 * It is globally readable, exposing any values assigned to public variables stored in it.
 */
class SessionStorageCompanion : Application () {
    companion object {
        const val NAME_SHARED_PREFERENCES: String = "org.gkisalatiga.GKISPLUS_SESSION_STORAGE"

        /* The default values of each primitive value class. */
        const val DEFAULT_BOOLEAN_VALUE = false
        const val DEFAULT_FLOAT_VALUE = 0.0f
        const val DEFAULT_INT_VALUE = 0.toInt()
        const val DEFAULT_LONG_VALUE = 0.toLong()
        const val DEFAULT_STRING_VALUE = ""

        /* String to separate SessionStorageKeys from getter key in a composite SessionStorage key.
         * The string was chosen arbitrarily.
         * DO NOT CHANGE IN FUTURE RELEASE! MAY BREAK IF UPDATED */
        const val COMPOSITE_KEY_SEPARATOR = "X}_a"

        /* The map that stores all session storage objects. */
        val sessionStorageMap: MutableMap<String, Any> = mutableMapOf()
    }
}

/**
 * This class is the keying class for all session storage in this app.
 * Each preference bears its own key, represented by the following enum object.
 */
enum class SessionStorageKeys {
    /* Generic key that does not require additional custom key. */
    // TODO

    /* More sophisticated session storage keys that require the use of customKey. */
    SESSION_KEY_GET_LAST_OPENED_PDF_PAGE,
}

/**
 * This class determines the type of data to be stored in the session storage.
 * i.e., boolean, float, integer, long, and string.
 */
enum class SessionStorageDataTypes {
    BOOLEAN,
    FLOAT,
    INT,
    LONG,
    STRING
}

 */