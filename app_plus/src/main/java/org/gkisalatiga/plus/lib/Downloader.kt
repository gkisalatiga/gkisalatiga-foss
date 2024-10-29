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
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.db.Gallery
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.Modules
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.db.Static
import org.gkisalatiga.plus.db.StaticCompanion
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

    // The file creator to create the private file.
    private val fileCreator = ctx.getDir(GlobalSchema.FILE_CREATOR_TARGET_DOWNLOAD_DIR, Context.MODE_PRIVATE)

    /**
     * Downloads and initiates the main JSON data source file from the CDN.
     * This function will then assign the downloaded JSON path to the appropriate global variable.
     * Requires no argument and does not return any return value.
     * @param autoReloadGlobalData whether to reload the global JSON data after successful download
     */
    fun initMainData(autoReloadGlobalData: Boolean = false) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Logger.log({}, "Attempting to download the JSON metadata file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(MainCompanion.REMOTE_JSON_SOURCE).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val privateFile = File(fileCreator, MainCompanion.savedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                MainCompanion.mutableIsDataInitialized.value = true
                if (autoReloadGlobalData) MainCompanion.jsonRoot = Main(ctx).getMainData()

                Logger.log({}, "JSON metadata was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet.value = false
                Logger.log({}, "Network unreachable during download: $e", LoggerType.ERROR)
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
    fun initGalleryData(autoReloadGlobalData: Boolean = false) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Logger.log({}, "Attempting to download the gallery JSON file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(GalleryCompanion.REMOTE_JSON_SOURCE).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val privateFile = File(fileCreator, GalleryCompanion.savedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                GalleryCompanion.mutableIsDataInitialized.value = true
                if (autoReloadGlobalData) GalleryCompanion.jsonRoot = Gallery(ctx).getGalleryData()

                Logger.log({}, "Gallery was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet.value = false
                Logger.log({}, "Network unreachable when downloading the gallery data: $e", LoggerType.ERROR)
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

    /**
     * Downloads and initiates the modules JSON source file from the CDN.
     * This function will then assign the downloaded JSON path to the appropriate global variable.
     * Requires no argument and does not return any return value.
     * @param autoReloadGlobalData whether to reload the global JSON data after successful download
     */
    fun initModulesData(autoReloadGlobalData: Boolean = false) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Logger.log({}, "Attempting to download the modules JSON file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(ModulesCompanion.REMOTE_JSON_SOURCE).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val privateFile = File(fileCreator, ModulesCompanion.savedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                ModulesCompanion.mutableIsDataInitialized.value = true
                if (autoReloadGlobalData) ModulesCompanion.jsonRoot = Modules(ctx).getModulesData()

                Logger.log({}, "Modules was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet.value = false
                Logger.log({}, "Network unreachable when downloading the modules data: $e", LoggerType.ERROR)
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
    fun initStaticData(autoReloadGlobalData: Boolean = false) {
        // Non-blocking the main GUI by creating a separate thread for the download
        // Preparing the thread.
        val executor = Executors.newSingleThreadExecutor()

        // Fetching the data
        Logger.log({}, "Attempting to download the static JSON file ...")
        executor.execute {

            try {
                // Opening the file download stream.
                val streamIn = java.net.URL(StaticCompanion.REMOTE_JSON_SOURCE).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Creating the private file.
                val privateFile = File(fileCreator, StaticCompanion.savedFilename)

                // Writing into the file.
                val out = FileOutputStream(privateFile)
                out.flush()
                out.write(decodedData)
                out.close()

                // Notify all the other functions about the JSON file path.
                StaticCompanion.mutableIsDataInitialized.value = true
                if (autoReloadGlobalData) StaticCompanion.jsonRoot = Static(ctx).getStaticData()

                Logger.log({}, "Static data was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: UnknownHostException) {
                GlobalSchema.isConnectedToInternet.value = false
                Logger.log({}, "Network unreachable when downloading the static data: $e", LoggerType.ERROR)
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

}