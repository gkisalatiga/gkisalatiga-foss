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

    /**
     * The default values of local storage keys.
     */
    private val DEFAULT_LOCAL_KEY_VALUES: Map<LocalStorageKeys, Any> = mapOf(
        LocalStorageKeys.LOCAL_KEY_LAST_OPENED_PDF_CACHES to "",
        LocalStorageKeys.LOCAL_KEY_LAST_STATIC_DATA_UPDATE to Long.MIN_VALUE,
        LocalStorageKeys.LOCAL_KEY_LAST_CAROUSEL_BANNER_UPDATE to Long.MIN_VALUE,
        LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS to -1.toInt(),
    )

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
     * Initializes the default local storage values.
     * Useful during first launch where local storage values are unset.
     */
    fun initDefaultLocalStorage() {
        // Assign each local storage value in the map individually.
        for (localKey in DEFAULT_LOCAL_KEY_VALUES.keys) {
            // The default value, as declared in the default local storage key value.
            val defaultVal = DEFAULT_LOCAL_KEY_VALUES[localKey]!!

            // Debug the default local storage's type.
            Logger.logTest({}, "Type of defaultVal ${defaultVal::class.qualifiedName}, valued = $defaultVal")

            // Initializing the default values to the local storage store.
            with (localStorageObj.edit()) {
                if (defaultVal::class == Long::class) putLong(localKey.name, defaultVal as Long)
                else if (defaultVal::class == Int::class) putInt(localKey.name, defaultVal as Int)
                else if (defaultVal::class == String::class) putString(localKey.name, defaultVal as String)
                else if (defaultVal::class == Boolean::class) putBoolean(localKey.name, defaultVal as Boolean)
                else if (defaultVal::class == Float::class) putFloat(localKey.name, defaultVal as Float)

                // Writing the default local storage values.
                apply()
            }
        }
    }

    /**
     * Get the saved value of a given local storage key, on an individual basis.
     * @param localKey the local storage key to refer to.
     * @return The value stored by the local storage key, if the key exists. Otherwise returns null.
     */
    fun getLocalStorageValue(localKey: LocalStorageKeys): Any? {
        // The returned value.
        var retVal: Any? = null

        // Find the key's default value, as declared in the default local storage key value.
        val defaultVal = DEFAULT_LOCAL_KEY_VALUES[localKey]!!

        // Initializing the default values to the local storage store.
        with (localStorageObj) {
            if (defaultVal::class == Long::class) retVal = getLong(localKey.name, defaultVal as Long)
            else if (defaultVal::class == Int::class) retVal = getInt(localKey.name, defaultVal as Int)
            else if (defaultVal::class == String::class) retVal = getString(localKey.name, defaultVal as String)
            else if (defaultVal::class == Boolean::class) retVal = getBoolean(localKey.name, defaultVal as Boolean)
            else if (defaultVal::class == Float::class) retVal = getFloat(localKey.name, defaultVal as Float)
        }

        // Debug the default local storage's type.
        Logger.logTest({}, "Type of defaultVal ${defaultVal::class.qualifiedName}, valued = $defaultVal")

        // Hand over the local storage value the caller asks for.
        return retVal
    }

    /**
     * Writing a given saved local storage according to the passed key.
     * @param localKey the local storage key to refer to.
     * @param localStorageValue the value to be saved. Must be either: float, int, long, string, or boolean.
     */
    fun setLocalStorageValue(localKey: LocalStorageKeys, localStorageValue: Any) {
        // Debug the local storage key-to-write value.
        Logger.logTest({}, "Writing the local storage value: $localStorageValue under the key ${localKey.name} with class type: ${localStorageValue::class.qualifiedName}")

        with (localStorageObj.edit()) {
            // Detect local storage value type.
            if (localStorageValue::class == Long::class) {
                putLong(localKey.name, localStorageValue as Long)
            } else if (localStorageValue::class == Int::class) {
                putInt(localKey.name, localStorageValue as Int)
            } else if (localStorageValue::class == String::class) {
                putString(localKey.name, localStorageValue as String)
            } else if (localStorageValue::class == Boolean::class) {
                putBoolean(localKey.name, localStorageValue as Boolean)
            } else if (localStorageValue::class == Float::class) {
                putFloat(localKey.name, localStorageValue as Float)
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
    }
}

/**
 * This class is the keying class for all local storage in this app.
 * Each preference bears its own key, represented by the following enum object.
 */
enum class LocalStorageKeys {
    LOCAL_KEY_LAST_OPENED_PDF_CACHES,
    LOCAL_KEY_LAST_STATIC_DATA_UPDATE,
    LOCAL_KEY_LAST_CAROUSEL_BANNER_UPDATE,
    LOCAL_KEY_LAUNCH_COUNTS,
}
