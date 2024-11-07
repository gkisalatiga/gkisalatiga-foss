/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.composable

import android.app.Application
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

/**
 * This snack bar will be displayed when the app goes offline.
 */
@Composable
fun OfflineSnackbarHost() {
    SnackbarHost(OfflineSnackbarHostCompanion.snackbarHostState)
}

class OfflineSnackbarHostCompanion : Application() {
    companion object {
        /* Controls the scaffolding of the snack bar. */
        val snackbarHostState = SnackbarHostState()
    }
}