/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 * This class gets around "network on main thread" error and allow for the downloading of files
 * from internet sources.
 */

package org.gkisalatiga.plus.lib

import android.content.Context
import android.util.Log
import org.gkisalatiga.plus.global.GlobalSchema
import java.io.File
import java.io.FileOutputStream
import java.net.UnknownHostException
import java.util.concurrent.Executors

/**
 * Attempts to download an online data.
 * SOURCE: https://stackoverflow.com/a/53128216
 */
class Downloader(private val ctx: Context) {

    /**
     * Downloads and initiates the main JSON data source file from the CDN.
     * This function will then assign the downloaded JSON path to the appropriate global variable.
     * Requires no argument and does not return any return value.
     * @param autoReloadGlobalData whether to reload the global JSON data after successful download
     */
    public fun initMainData(autoReloadGlobalData: Boolean = false) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Log.d("Groaker", "Attempting to download the JSON metadata file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(GlobalSchema.JSONSource).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val fileCreator = ctx.getDir("Downloads", Context.MODE_PRIVATE)
                val privateFile = File(fileCreator, GlobalSchema.JSONSavedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                GlobalSchema.absolutePathToJSONMetaData = privateFile.absolutePath
                GlobalSchema.isJSONMainDataInitialized.value = true
                if (autoReloadGlobalData) GlobalSchema.globalJSONObject = AppDatabase(ctx).getMainData()

                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "JSON metadata was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet.value = false
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Network unreachable during download: $e")
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Downloads and initiates the gallery JSON source file from the CDN.
     * This function will then assign the downloaded JSON path to the appropriate global variable.
     * Requires no argument and does not return any return value.
     * @param autoReloadGlobalData whether to reload the global JSON data after successful download
     */
    public fun initGalleryData(autoReloadGlobalData: Boolean = false) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Log.d("Groaker", "Attempting to download the gallery JSON file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(GlobalSchema.gallerySource).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val fileCreator = ctx.getDir("Downloads", Context.MODE_PRIVATE)
                val privateFile = File(fileCreator, GlobalSchema.gallerySavedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                GlobalSchema.absolutePathToGalleryData = privateFile.absolutePath
                GlobalSchema.isGalleryDataInitialized.value = true
                if (autoReloadGlobalData) GlobalSchema.globalGalleryObject = AppGallery(ctx).getGalleryData()

                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Gallery was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet.value = false
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Network unreachable when downloading the gallery data: $e")
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Downloads and initiates the static JSON source file from the CDN.
     * This function will then assign the downloaded JSON path to the appropriate global variable.
     * Requires no argument and does not return any return value.
     * @param autoReloadGlobalData whether to reload the global JSON data after successful download
     */
    public fun initStaticData(autoReloadGlobalData: Boolean = false) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Log.d("Groaker", "Attempting to download the static JSON file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(GlobalSchema.staticSource).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val fileCreator = ctx.getDir("Downloads", Context.MODE_PRIVATE)
                val privateFile = File(fileCreator, GlobalSchema.staticSavedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                GlobalSchema.absolutePathToStaticData = privateFile.absolutePath
                GlobalSchema.isStaticDataInitialized.value = true
                if (autoReloadGlobalData) GlobalSchema.globalStaticObject = AppStatic(ctx).getStaticData()

                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Static data was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet.value = false
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Network unreachable when downloading the static data: $e")
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

}