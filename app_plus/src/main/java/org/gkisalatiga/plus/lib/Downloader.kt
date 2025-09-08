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
import org.gkisalatiga.plus.db.Gallery
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.Modules
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.db.Static
import org.gkisalatiga.plus.db.StaticCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.services.InternalFileManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import java.util.concurrent.Executors

/**
 * Attempts to download an online data.
 * SOURCE: https://stackoverflow.com/a/53128216
 */
class Downloader(private val ctx: Context) {

    // The file creator to create the private file.
    private val fileCreator = InternalFileManager(ctx).DATA_DIR_FILE_CREATOR

    // The download wait time before retrying. (In millisecond.)
    private val DOWNLOAD_WAIT_DELAY = 5000L

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
                if (autoReloadGlobalData) MainCompanion.api = Main(ctx).getMainData()

                GlobalCompanion.isConnectedToInternet.value = true
                Logger.log({}, "JSON metadata was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: ConnectException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "ConnectException: ${e.message}", LoggerType.ERROR)
            } catch (e: IOException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "IOException: ${e.message}", LoggerType.ERROR)
            } catch (e: SocketException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "SocketException: ${e.message}", LoggerType.ERROR)
            } catch (e: UnknownHostException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "UnknownHostException: ${e.message}", LoggerType.ERROR)
            } catch (e: Exception) {
                Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)
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
                if (autoReloadGlobalData) GalleryCompanion.api = Gallery(ctx).getGalleryData()

                GlobalCompanion.isConnectedToInternet.value = true
                Logger.log({}, "Gallery was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: ConnectException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "ConnectException: ${e.message}", LoggerType.ERROR)
            } catch (e: IOException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "IOException: ${e.message}", LoggerType.ERROR)
            } catch (e: SocketException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "SocketException: ${e.message}", LoggerType.ERROR)
            } catch (e: UnknownHostException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "UnknownHostException: ${e.message}", LoggerType.ERROR)
            } catch (e: Exception) {
                Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)
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
                if (autoReloadGlobalData) ModulesCompanion.api = Modules(ctx).getModulesData()

                GlobalCompanion.isConnectedToInternet.value = true
                Logger.log({}, "Modules was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: ConnectException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "ConnectException: ${e.message}", LoggerType.ERROR)
            } catch (e: IOException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "IOException: ${e.message}", LoggerType.ERROR)
            } catch (e: SocketException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "SocketException: ${e.message}", LoggerType.ERROR)
            } catch (e: UnknownHostException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "UnknownHostException: ${e.message}", LoggerType.ERROR)
            } catch (e: Exception) {
                Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)
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
                if (autoReloadGlobalData) StaticCompanion.api = Static(ctx).getStaticData()

                GlobalCompanion.isConnectedToInternet.value = true
                Logger.log({}, "Static data was successfully downloaded into: ${privateFile.absolutePath}")

            } catch (e: ConnectException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "ConnectException: ${e.message}", LoggerType.ERROR)
            } catch (e: IOException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "IOException: ${e.message}", LoggerType.ERROR)
            } catch (e: SocketException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "SocketException: ${e.message}", LoggerType.ERROR)
            } catch (e: UnknownHostException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "UnknownHostException: ${e.message}", LoggerType.ERROR)
            } catch (e: Exception) {
                Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)
            }

            // Break free from this thread.
            executor.shutdown()
        }
    }

}