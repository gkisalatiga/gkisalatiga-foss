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
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.InvalidConnectionTestStringException
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.PreferenceKeys
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ConnectionChecker(private val ctx: Context) {

    companion object {
        const val CONNECTION_TEST_FILE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/refs/heads/main/test.txt"
        const val CONNECTION_TEST_STRING = "--conn. test file. do not remove"
    }

    // Non-blocking the main GUI by creating a separate thread for the download
    // Preparing the thread.
    private val executor = Executors.newSingleThreadExecutor()

    // How frequent should we check for internet connectivity? (In milliseconds)
    private val offlineCheckFrequency: Long = AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_OFFLINE_CHECK_FREQUENCY) as Long

    /**
     * Runs the network connectivity checker indefinitely.
     */
    fun execute() {
        Logger.log({}, "Initiating the network checker ...")

        // Check if first execution.
        var isFirstExecution = true

        // Use coroutine instead of regular single-thread for efficiency.
        // SOURCE: https://discuss.kotlinlang.org/t/how-can-i-use-co-routines-to-single-thread-asynchronous-responses/23045/15
        executor.execute {
            while (true) {
                /**
                 * Previously, this is how we test for internet connectivity:
                 * =======================================================================
                 * // Check for internet connectivity.
                 * // SOURCE: https://stackoverflow.com/a/59750435
                 * val manager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                 * val networkInfo = manager.activeNetworkInfo
                 * if (networkInfo != null && networkInfo.isConnected) {
                 *     // We are connected to the internet!
                 *     GlobalCompanion.isConnectedToInternet.value = true
                 *     Logger.logConnTest({}, "ONLINE::0", LoggerType.INFO)
                 * } else {
                 *     GlobalCompanion.isConnectedToInternet.value = false
                 *     Logger.logConnTest({}, "OFFLINE::1", LoggerType.INFO)
                 * }
                 * * =======================================================================
                 * However, it turns out this code gives false positives.
                 * When the phone is connected to any Wi-Fi network but with no data traffic,
                 * the above code still results in "ONLINE::0" conn. test log string.
                 * This is replaced with the following code in v0.6.0.
                 */

                // Wait for a certain time before re-checking the internet connection again.
                if (!isFirstExecution) TimeUnit.MILLISECONDS.sleep(offlineCheckFrequency) else isFirstExecution = false

                // Ping for internet connectivity.
                try {
                    // Opening the file download stream and coverting input stream (bytes) to string.
                    val streamIn = java.net.URL(CONNECTION_TEST_FILE).openStream()
                    val decodedData: ByteArray = streamIn.readBytes()
                    val decodedStringData = decodedData.decodeToString()

                    // Log successful pinging.
                    if (decodedStringData == CONNECTION_TEST_STRING) {
                        GlobalCompanion.isConnectedToInternet.value = true
                        Logger.logConnTest({}, "0, ONLINE, <NoError>", LoggerType.INFO)
                    } else {
                        throw InvalidConnectionTestStringException("Downloaded string does not match with the app's test string: $decodedStringData")
                    }
                } catch (e: ConnectException) {
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logConnTest({}, "1, OFFLINE, ConnectException, ${e.message}", LoggerType.INFO)
                } catch (e: InvalidConnectionTestStringException) {
                    GlobalCompanion.isConnectedToInternet.value = true
                    Logger.logConnTest({}, "0, ONLINE, InvalidConnectionTestStringException, ${e.message}", LoggerType.INFO)
                } catch (e: IOException) {
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logConnTest({}, "1, OFFLINE, IOException, ${e.message}", LoggerType.INFO)
                } catch (e: SocketException) {
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logConnTest({}, "1, OFFLINE, SocketException, ${e.message}", LoggerType.INFO)
                } catch (e: UnknownHostException) {
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logConnTest({}, "1, OFFLINE, UnknownHostException, ${e.message}", LoggerType.INFO)
                } catch (e: Exception) {
                    Logger.logConnTest({}, "2, UNKNOWN, Exception, ${e.message}", LoggerType.INFO)
                }

            }  // --- end of while loop.
        }  // --- end of executor.execute()
    }  // --- end of fun.

}