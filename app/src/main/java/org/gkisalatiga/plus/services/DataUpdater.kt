/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Attempts to download the latest JSON file from online source and update the app's data.
 */

package org.gkisalatiga.plus.services

import android.content.Context
import android.util.Log
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.AppGallery
import org.gkisalatiga.plus.lib.AppStatic
import org.gkisalatiga.plus.lib.Downloader
import org.json.JSONObject
import java.io.InputStream
import java.net.UnknownHostException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DataUpdater(private val ctx: Context) {

    companion object {
        // The location of the remote feeds.json file to check for updates.
        private const val FEEDS_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/feeds.json"
    }

    private fun getLastGalleryUpdate() : Int {
        return AppGallery(ctx).getGalleryMetadata().getInt("last-update")
    }

    private fun getLastMainDataUpdate() : Int {
        return AppDatabase(ctx).getMainMetadata().getInt("last-update")
    }

    private fun getLastStaticUpdate() : Int {
        return AppStatic(ctx).getStaticMetadata().getInt("last-update")
    }

    /**
     * Get the cloud-stored feed information
     * in order to determine whether we should update the data or not.
     */
    private fun getMostRecentFeeds() : JSONObject {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.d("Groaker-Updater", "[DataUpdater.getMostRecentFeeds] Fetching the update feeds ...")
        val streamInput: InputStream = java.net.URL(FEEDS_JSON_SOURCE).openStream()
        val inputAsString: String = streamInput.bufferedReader().use { it.readText() }

        // Dumps the JSON string, and compare them with the currently cached JSON last update values.
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_DUMP) Log.d("Groaker-Dump", inputAsString)
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) {
            Log.d("Groaker-Dump", "Last Main Data Update: ${getLastMainDataUpdate()}")
            Log.d("Groaker-Dump", "Last Gallery Data Update: ${getLastGalleryUpdate()}")
            Log.d("Groaker-Dump", "Last Static Data Update: ${getLastStaticUpdate()}")
        }

        // Returns the feeds data.
        return JSONObject(inputAsString).getJSONObject("feeds")
    }

    public fun updateData() {
        // Upon successful data download, we manage the app's internal variable storage
        // according to the downloaded JSON file's schema.
        // We also make any appropriate settings accordingly.
        // ---
        // This is all done in a multi-thread so that we do not interrupt the main GUI.
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.d("Groaker-Updater", "[DataUpdater.updateData] Attempting to update and assign the latest JSON files ...")

            try {
                // Retrieving the feed JSON file to check if update is even necessary.
                val feedJSONObject = getMostRecentFeeds()

                // Set the flag to "false" to signal that we need to have the new data now,
                // then make the attempt to download the JSON files.
                if (getLastMainDataUpdate() < feedJSONObject.getInt("last-maindata-update")) {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.i("Groaker-Updater", "[DataUpdater.updateData] Updating main data ...")
                    GlobalSchema.isJSONMainDataInitialized.value = false
                    Downloader(ctx).initMainData(autoReloadGlobalData = true)
                } else {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.i("Groaker-Updater", "[DataUpdater.updateData] Main data is up-to-date!")
                }

                if (getLastGalleryUpdate() < feedJSONObject.getInt("last-gallery-update")) {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.i("Groaker-Updater", "[DataUpdater.updateData] Updating gallery data ...")
                    GlobalSchema.isGalleryDataInitialized.value = false
                    Downloader(ctx).initGalleryData(autoReloadGlobalData = true)
                } else {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.i("Groaker-Updater", "[DataUpdater.updateData] Gallery data is up-to-date!")
                }

                if (getLastStaticUpdate() < feedJSONObject.getInt("last-static-update")) {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.i("Groaker-Updater", "[DataUpdater.updateData] Updating static data ...")
                    GlobalSchema.isStaticDataInitialized.value = false
                    Downloader(ctx).initStaticData(autoReloadGlobalData = true)
                } else {
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_UPDATER) Log.i("Groaker-Updater", "[DataUpdater.updateData] Static data is up-to-date!")
                }

            } catch (e: UnknownHostException) {
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) Log.e("Groaker-Test", "[DataUpdater.updateData] Network error! Cannot retrieve the latest feeds JSON file data!")
            }

            // Assign the JSON data globally.
            GlobalSchema.globalJSONObject = AppDatabase(ctx).getMainData()
            GlobalSchema.globalGalleryObject = AppGallery(ctx).getGalleryData()
            GlobalSchema.globalStaticObject = AppStatic(ctx).getStaticData()

            // End the thread.
            executor.shutdown()

        }  // --- end of executor.execute()
    }  // --- end of fun updateData()

}