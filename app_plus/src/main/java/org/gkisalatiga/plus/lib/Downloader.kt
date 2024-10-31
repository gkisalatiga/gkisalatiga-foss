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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleObserver
import com.rajat.pdfviewer.HeaderData
import com.rajat.pdfviewer.PdfRendererView.StatusCallBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.db.Gallery
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.Modules
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.db.Static
import org.gkisalatiga.plus.db.StaticCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.eBookUrl
import org.gkisalatiga.plus.services.InternalFileManager
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.Executors

/**
 * Attempts to download an online data.
 * SOURCE: https://stackoverflow.com/a/53128216
 */
class Downloader(private val ctx: Context) {

    // The file creator to create the private file.
    private val fileCreator = InternalFileManager(ctx).DOWNLOAD_FILE_CREATOR

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
                if (autoReloadGlobalData) MainCompanion.jsonRoot = Main(ctx).getMainData()

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
                if (autoReloadGlobalData) GalleryCompanion.jsonRoot = Gallery(ctx).getGalleryData()

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
                if (autoReloadGlobalData) ModulesCompanion.jsonRoot = Modules(ctx).getModulesData()

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
                if (autoReloadGlobalData) StaticCompanion.jsonRoot = Static(ctx).getStaticData()

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

    /** TODO: Consider removing.
     * Downloads PDF file from an online source and then mark the URL as already downloaded,
     * to prevent duplication and cluttering of app's internal storage.
     * This function rewrites and modifies some parts of com.rajat.pdfviewer.PdfDownloader.kt
     * so that we only download the PDF file, not viewing it.
     * @param pdfUrl the URL of the PDF file to be downloaded.
     * @param lifecycleCoroutineScope required by com.rajat.pdfviewer.PdfDownloader
     * @param statusListener the status listener for updating the state of PDF downloading.
     */
    fun initRemotePDF(pdfUrl: String, lifecycleCoroutineScope: LifecycleCoroutineScope, statusListener: StatusCallBack) {
        lifecycleCoroutineScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    // Attempting to download the PDF file.
                    val urlConnection = URL(pdfUrl).openConnection()
                    val savedFilename = "localpdf_" + System.currentTimeMillis().toString() + ".pdf"
                    val outputFile = File(fileCreator, savedFilename)
                    val totalLength = urlConnection.contentLength.toLong()
                    BufferedInputStream(urlConnection.getInputStream()).use { inputStream ->
                        FileOutputStream(outputFile).use { outputStream ->
                            val data = ByteArray(8192)
                            var totalBytesRead = 0L
                            var bytesRead: Int
                            while (inputStream.read(data).also { bytesRead = it } != -1) {
                                outputStream.write(data, 0, bytesRead)
                                totalBytesRead += bytesRead
                                withContext(Dispatchers.Main) {
                                    // Updating the downloaded bytes status (i.e., the download progress.)
                                    var progress = (totalBytesRead.toFloat() / totalLength.toFloat() * 100F).toInt()
                                    if (progress >= 100) progress = 100

                                    // Notify the listener about the current progression of the PDF download.
                                    statusListener.onPdfLoadProgress(progress, totalBytesRead, totalLength)
                                }
                            }
                            outputStream.flush()  // --- ensure all data is written to the file.
                        }
                    }

                    // Validating the downloaded file.
                    val outputPath = outputFile.absolutePath
                    if (outputFile.length() == totalLength) {
                        // Ensure that we don't download this PDF file again in the future.
                        LocalStorage(ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, true, LocalStorageDataTypes.BOOLEAN, pdfUrl)
                        LocalStorage(ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, outputPath, LocalStorageDataTypes.STRING, pdfUrl)

                        // Register the file in the app's internal file manager so that it will be scheduled for cleaning.
                        InternalFileManager(ctx).enlistDownloadedFileForCleanUp(eBookUrl, outputPath)

                        withContext(Dispatchers.Main) { statusListener.onPdfLoadSuccess(outputPath) }
                        return@withContext
                    } else {
                        throw IOException("Incomplete downloading of the PDF file: $outputPath")
                    }
                } catch (e: ConnectException) {
                    statusListener.onError(e)
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logTest({}, "ConnectException: ${e.message}", LoggerType.ERROR)
                } catch (e: IOException) {
                    statusListener.onError(e)
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logTest({}, "IOException: ${e.message}", LoggerType.ERROR)
                } catch (e: SocketException) {
                    statusListener.onError(e)
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logTest({}, "SocketException: ${e.message}", LoggerType.ERROR)
                } catch (e: UnknownHostException) {
                    statusListener.onError(e)
                    GlobalCompanion.isConnectedToInternet.value = false
                    Logger.logTest({}, "UnknownHostException: ${e.message}", LoggerType.ERROR)
                } catch (e: Exception) {
                    statusListener.onError(e)
                    Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)
                }

                delay(DOWNLOAD_WAIT_DELAY)
            }  // --- end withContext.
        }  // --- end lifecycleCoroutineScope.
    }

}