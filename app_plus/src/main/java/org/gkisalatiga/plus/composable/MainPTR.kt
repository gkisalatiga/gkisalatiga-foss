/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.composable

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.services.DataUpdater
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Carries out the pull-to-refresh rendering in any screen that calls this function.
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainPTR(calculatedTopPadding: Dp) {
    PullToRefreshBox(
        modifier = Modifier.fillMaxWidth().padding(top = calculatedTopPadding),
        isRefreshing = MainPTRCompanion.isPTRRefreshing.value,
        state = MainPTRCompanion.mainPTRState!!,
        onRefresh = {},
        content = {},
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = MainPTRCompanion.isPTRRefreshing.value,
                state = MainPTRCompanion.mainPTRState!!
            )
        },
    )
}

class MainPTRCompanion : Application() {
    companion object {
        /* Controls the pull-to-refresh (PTR) states and variables. */
        val isPTRRefreshing = mutableStateOf(false)
        val pullToRefreshExecutor = Executors.newSingleThreadExecutor()

        /* This PTR state is initialized upon app start. */
        @OptIn(ExperimentalMaterial3Api::class)
        var mainPTRState: PullToRefreshState? = null

        /**
         * Performs actions when the PTR state changes.
         */
        fun launchOnRefresh(ctx: Context) {
            pullToRefreshExecutor.execute {
                // Assumes there is an internet connection.
                // (If there isn't, the boolean state change will trigger the snack bar.)
                GlobalCompanion.isConnectedToInternet.value = true

                // Attempts to update the data.
                isPTRRefreshing.value = true
                DataUpdater(ctx).updateData()
                TimeUnit.SECONDS.sleep(5)
                isPTRRefreshing.value = false

                // Update/recompose the UI.
                AppNavigation.recomposeUi()
            }
        }  // --- end of launchOnRefresh.

    }
}