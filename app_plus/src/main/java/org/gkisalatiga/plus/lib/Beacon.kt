/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's inter-screen navigation.
 */

package org.gkisalatiga.plus.lib

import android.R.attr.name
import android.R.attr.text
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import org.gkisalatiga.plus.data.ActivityData

/**
 * This library centralizes analytics management.
 * This library also handles sending analytics data to Google Firebase.
 */
class Beacon(private val firebaseAnalytics: FirebaseAnalytics) {
    // Overloading for uses in screens and fragments.
    constructor(current: ActivityData) : this(current.firebaseAnalytics)

    /**
     * Track app launches from the home screen.
     */
    fun logAppLaunch() {
        Logger.logAnalytics({}, "Reporting app launch event ...")
        firebaseAnalytics.logEvent(BeaconLogEvents.APP_LAUNCH.name,
            Bundle().let {
                it.putString(FirebaseAnalytics.Param.VALUE, "1")
                it
            }
        )
    }

    /**
     * Track which menus (screens) are being opened.
     * TODO: Implement this method in all screens in the app.
     */
    fun logScreenOpen(routes: NavigationRoutes) {
        Logger.logAnalytics({}, "Reporting menu/screen open event: ${routes.name} ...")
        firebaseAnalytics.logEvent(BeaconLogEvents.SCREEN_OPEN.name,
            Bundle().let {
                it.putString(FirebaseAnalytics.Param.VALUE, routes.name)
                it
            }
        )
    }
}

private enum class BeaconLogEvents {
    APP_LAUNCH,
    SCREEN_OPEN
}