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
     * Track which PDF files are being opened.
     */
    fun logPDFOpen(title: String, sourceUrl: String) {
        Logger.logAnalytics({}, "Reporting PDF open event: $title from $sourceUrl ...")
        firebaseAnalytics.logEvent(BeaconLogEvents.PDF_OPEN.name,
            Bundle().let {
                it.putString(FirebaseAnalytics.Param.VALUE, title)
                it.putString(FirebaseAnalytics.Param.ORIGIN, sourceUrl)
                it
            }
        )
    }

    /**
     * Track which Bibles are being opened.
     */
    fun logBibleOpen(title: String, sourceUrl: String) {
        Logger.logAnalytics({}, "Reporting Bible open event: $title from $sourceUrl ...")
        firebaseAnalytics.logEvent(BeaconLogEvents.BIBLE_OPEN.name,
            Bundle().let {
                it.putString(FirebaseAnalytics.Param.VALUE, title)
                it.putString(FirebaseAnalytics.Param.ORIGIN, sourceUrl)
                it
            }
        )
    }

    /**
     * Track which menus (screens) are being opened.
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

    /**
     * Use this function overload only in [org.gkisalatiga.plus.ActivityLauncher]
     */
    fun logScreenOpen(routeString: String?) {
        val route = routeString ?: ""
        Logger.logAnalytics({}, "Reporting menu/screen open event: $route ...")
        firebaseAnalytics.logEvent(BeaconLogEvents.SCREEN_OPEN.name,
            Bundle().let {
                it.putString(FirebaseAnalytics.Param.VALUE, route)
                it
            }
        )
    }

    /**
     * Track which YouTube videos are being played.
     */
    fun logVideoPlay(title: String, url: String) {
        Logger.logAnalytics({}, "Reporting YouTube video play event: $title from $url ...")
        firebaseAnalytics.logEvent(BeaconLogEvents.VIDEO_PLAY.name,
            Bundle().let {
                it.putString(FirebaseAnalytics.Param.VALUE, title)
                it.putString(FirebaseAnalytics.Param.SOURCE, url)
                it
            }
        )
    }
}

private enum class BeaconLogEvents {
    APP_LAUNCH,
    BIBLE_OPEN,
    PDF_OPEN,
    SCREEN_OPEN,
    VIDEO_PLAY,
}