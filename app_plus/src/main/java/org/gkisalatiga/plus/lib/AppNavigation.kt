/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's inter-screen navigation.
 */

package org.gkisalatiga.plus.lib

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class AppNavigation : Application() {
    companion object {
        // This is the default screen to display.
        val DEFAULT_SCREEN_ROUTE = NavigationRoutes.SCREEN_MAIN

        // The starting screen route variable is used for screen navigation during cold start.
        var startingScreenRoute = DEFAULT_SCREEN_ROUTE

        // This list stores the history of screen navigation. */
        private var navigationRouteHistory: MutableList<NavigationRoutes> = mutableListOf()

        // This integer stores the index of the current position of route in history,
        // corresponding to the above navigation route history list.
        private var currentNavigationIndex: Int = 0

        // This value is exposed so that the nav. host in ActivityLauncher can switch between screens.
        val mutableCurrentNavigationRoute: MutableState<NavigationRoutes> = mutableStateOf(DEFAULT_SCREEN_ROUTE)

        // Changing the boolean value of this mutable variable automatically triggers recomposition of the current screen.
        val mutableRecomposeCurrentScreen = mutableStateOf(false)

        /**
         * This function is the actual function that does mutableCurrentNavigationRoute value change
         * and actually trigger Jetpack recomposition in the main activity.
         * @param route the screen's route ID to which the app will navigate.
         */
        private fun composeRoute(route: NavigationRoutes) {
            mutableCurrentNavigationRoute.value = route
        }

        /**
         * Navigate forward to a given screen, removing any previous "forward" history.
         * @param route the screen's route ID to which the app will navigate.
         */
        fun navigate(route: NavigationRoutes) {
            Logger.logTest({}, "navigate -> currentNavigationIndex: $currentNavigationIndex, navigationRouteHistory.size: ${navigationRouteHistory.size}, route: ${route.name}")

            // Increment the history index.
            navigationRouteHistory.add(currentNavigationIndex, route)
            currentNavigationIndex++

            // Remove all forward history.
            navigationRouteHistory = navigationRouteHistory.subList(0, currentNavigationIndex)

            // Do navigate and recompose.
            composeRoute(route)
        }

        /**
         * This function navigates the app's screen during cold start (e.g., when handling deep links).
         * When this function is called, the target route will still be displayed even when the app already starts.
         * @param route the screen's route ID to which the app will navigate.
         */
        fun navigateCold(route: NavigationRoutes) {
            Logger.logTest({}, "navigateCold -> currentNavigationIndex: $currentNavigationIndex, navigationRouteHistory.size: ${navigationRouteHistory.size}, route: ${route.name}")

            // Set the passed route argument as the starting point for activity launch.
            startingScreenRoute = route

            // Increment the history index.
            navigationRouteHistory.add(currentNavigationIndex, route)
            currentNavigationIndex++

            // Remove all forward history.
            navigationRouteHistory = navigationRouteHistory.subList(0, currentNavigationIndex)

            // Do navigate and recompose.
            composeRoute(route)
        }

        /**
         * Navigates back to a previous screen adjacent to the current route in history.
         */
        fun popBack() {
            Logger.logTest({}, "popBack -> currentNavigationIndex: $currentNavigationIndex, navigationRouteHistory.size: ${navigationRouteHistory.size}")

            if (currentNavigationIndex > 0) currentNavigationIndex--
            if (currentNavigationIndex > 0 && navigationRouteHistory.size > 0) {
                composeRoute(navigationRouteHistory[currentNavigationIndex - 1])
            } else if (currentNavigationIndex == 0) {
                // Bail-out.
                // We render the default screen if we have reached the end of the route history.
                composeRoute(DEFAULT_SCREEN_ROUTE)
            }
        }

        /**
         * Navigates forward to the next screen adjacent to the current route in history,
         * only if the forward history has not been cleared up.
         */
        fun popForward() {
            if (currentNavigationIndex < navigationRouteHistory.size) {
                currentNavigationIndex++
                composeRoute(navigationRouteHistory[currentNavigationIndex])
            }
        }
    }
}
