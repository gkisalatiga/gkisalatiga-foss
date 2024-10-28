/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Regularly checks whether the app is connected to the internet.
 */

package org.gkisalatiga.plus.services

import android.content.Context
import android.net.ConnectivityManager
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConnectionChecker(private val ctx: Context) {

    // Non-blocking the main GUI by creating a separate thread for the download
    // Preparing the thread.
    private val executor = Executors.newSingleThreadExecutor()

    // How frequent should we check for internet connectivity? (In milliseconds)
    private val offlineCheckFrequency = 5000.toLong()

    /**
     * Runs the network connectivity checker indefinitely.
     */
    fun execute() {
        Logger.log({}, "Initiating the network checker ...")

        // Use coroutine instead of regular single-thread for efficiency.
        // SOURCE: https://discuss.kotlinlang.org/t/how-can-i-use-co-routines-to-single-thread-asynchronous-responses/23045/15
        executor.execute {
            while (true) {
                // Check for internet connectivity.
                // SOURCE: https://stackoverflow.com/a/59750435
                val manager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = manager.activeNetworkInfo
                if (networkInfo != null && networkInfo.isConnected) {
                    // We are connected to the internet!
                    GlobalSchema.isConnectedToInternet.value = true
                    Logger.logConnTest({}, "ONLINE::0", LoggerType.INFO)
                } else {
                    GlobalSchema.isConnectedToInternet.value = false
                    Logger.logConnTest({}, "OFFLINE::1", LoggerType.INFO)
                }

                // Wait for a certain time before re-checking the internet connection again.
                TimeUnit.MILLISECONDS.sleep(offlineCheckFrequency)
            }  // --- end of while loop.
        }  // --- end of executor.execute()
    }  // --- end of fun.

}