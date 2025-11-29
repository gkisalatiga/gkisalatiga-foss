/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.PersistentLogger
import org.gkisalatiga.plus.lib.PreferenceKeys
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.Executors

/**
 * Manages the app's internal file management.
 * @param ctx the current local context of the app.
 */
class InternalFileManager (private val ctx: Context) {
    // The file creator to create the private file (as a downloaded file).
    @Suppress("PropertyName")
    val CONTENT_PROVIDER_FILE_CREATOR = File(ctx.filesDir, "provider")
    val DATA_DIR_FILE_CREATOR = ctx.getDir(InternalFileManagerCompanion.FILE_CREATOR_TARGET_DATA_DIR, Context.MODE_PRIVATE)
    val PDF_POOL_PDF_FILE_CREATOR = ctx.getDir(InternalFileManagerCompanion.FILE_CREATOR_TARGET_PDF_POOL_DIR, Context.MODE_PRIVATE)
    val BIBLE_POOL_FILE_CREATOR = ctx.getDir(InternalFileManagerCompanion.FILE_CREATOR_TARGET_BIBLE_POOL_DIR, Context.MODE_PRIVATE)

    // The string delimiter for encoding pdfAssociatedKey-pdfAbsolutePath pair as string.
    // (Chosen at random. DO NOT CHANGE IN FUTURE RELEASES!!!)
    @Suppress("PrivatePropertyName")
    private val PDF_LIST_DELIMITER = "$$#^"
    @Suppress("PrivatePropertyName")
    private val PDF_CONTENT_TUPLE_DELIMITER = ",,^,"

    // The FileProvider authority name.
    // (DO NOT CHANGE IN FUTURE RELEASES!!!)
    @Suppress("PropertyName", "SpellCheckingInspection")
    val FILE_PROVIDER_AUTHORITY = "org.gkisalatiga.plus.fileprovider"

    /**
     * Performs PDF cleaning-up.
     * i.e., removal of PDF files not read after X period.
     * This function does not delete a PDF file if it is marked as favorite.
     */
    fun doPdfCleanUp() {
        LocalStorage(ctx).getAll().entries.filter { it.key.contains(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_MARKED_AS_FAVORITE.name) }.forEach {
            // Only clean-up PDF files that are not marked as favorite.
            if (!(it.value as Boolean)) {
                val url = LocalStorage(ctx).getDecomposeKey(it.key).second
                val absolutePdfPath = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String
                val lastAccessMillis = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_LAST_ACCESS_MILLIS, LocalStorageDataTypes.LONG, url) as Long
                val prefKeepDaysOfCachedPdf = AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES) as Long

                // Debug only.
                Logger.logTest({}, "lastAccessMillis: $lastAccessMillis, prefKeepDaysOfCachedPdf: $prefKeepDaysOfCachedPdf, currentTimeMillis: ${System.currentTimeMillis()}")

                // Determines if we should keep this PDF file intact.
                if (lastAccessMillis + prefKeepDaysOfCachedPdf <= System.currentTimeMillis()) {
                    // Actually deleting the PDF file.
                    File(absolutePdfPath).let { f -> if (f.exists()) f.delete() }

                    // Remove any reference to the PDF file in the LocalStorage.
                    LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, url)
                    LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_LAST_ACCESS_MILLIS, url)
                    LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_LAST_DOWNLOAD_MILLIS, url)
                    LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_METADATA, url)
                    LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, url)
                    LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_MARKED_AS_FAVORITE, url)

                    // Log the removal message persistently.
                    PersistentLogger(ctx).write({}, "Removed PDF file due to clean-up of $prefKeepDaysOfCachedPdf millis: $url")
                }
            }
        }
    }

    /**
     * This function downloads a given file from URL and then share it using the share sheet mechanism.
     * @param url the source URL to be downloaded.
     * @param savedFilename the local filename to be saved as.
     * @param shareSheetTitle the title of the share sheet.
     * @param shareSheetText the text body of the share sheet.
     * @param clipboardLabel for image files, the label of the clipboard graphic.
     */
    fun downloadAndShare(url: String, savedFilename: String, shareSheetTitle: String, shareSheetText: String, clipboardLabel: String = "") {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                // Downloading the data.
                val streamIn = URL(url).openStream()
                val decodedData: ByteArray = streamIn.readBytes()

                // Determining the output file location.
                val contentProviderFileCreator = InternalFileManager(ctx).CONTENT_PROVIDER_FILE_CREATOR
                val absoluteFileLocation = File(contentProviderFileCreator, savedFilename).absolutePath

                // Writing into the file.
                val outFile = File(absoluteFileLocation)
                FileOutputStream(outFile).let {
                    it.flush()
                    it.write(decodedData)
                    it.close()
                    it
                }

                // Get the file's mime type.
                // SOURCE: https://stackoverflow.com/a/8591230
                val mimetype: String = MimeTypeMap.getSingleton().getMimeTypeFromExtension(outFile.extension).let { it ?: "" }
                Logger.logTest({}, "The mimetype is $mimetype for the content provider file path: absoluteFileLocation", LoggerType.VERBOSE)

                // The URI to the QRIS image provided by FileProvider.
                val authority = InternalFileManager(ctx).FILE_PROVIDER_AUTHORITY
                val secureUri = FileProvider.getUriForFile(ctx, authority, outFile)

                // Creating the intent.
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, secureUri)
                    putExtra(Intent.EXTRA_TEXT, shareSheetText)
                    putExtra(Intent.EXTRA_TITLE, shareSheetTitle)  // --- this is not currently successful at setting the chooser title.
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = mimetype

                    // Attach clipboard.
                    // This prevents "Writing exception to parcel" (java.lang.SecurityException) when attempting to load thumbnails.
                    // The error in itself is not harmful; the images can still be shared or saved to the destination file.
                    // SOURCE: https://stackoverflow.com/a/64541741
                    clipData = ClipData(
                        clipboardLabel,
                        listOf(mimetype).toTypedArray(),
                        ClipData.Item(secureUri)
                    )
                }

                // Sharing the data.
                ctx.startActivity(Intent.createChooser(shareIntent, null))
            } catch (e: Exception) {
                Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)
                e.printStackTrace()
            }
        }
    }

    /**
     * This function deletes individual Bible files from the internal Bible pool.
     * @param url the source URL associated with a given Bible to be deleted.
     */
    fun deleteBible(url: String) {
        val absolutePdfPath = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_BIBLE_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String

        // Actually deleting the PDF file.
        File(absolutePdfPath).let { f -> if (f.exists()) f.delete() }

        // Remove any reference to the PDF file in the LocalStorage.
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_BIBLE_FILE_LOCATION, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_BIBLE_LAST_ACCESS_MILLIS, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_BIBLE_LAST_DOWNLOAD_MILLIS, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_BIBLE_METADATA, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_BIBLE_FILE_DOWNLOADED, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_BIBLE_FILE_MARKED_AS_FAVORITE, url)

        // Log the removal message persistently.
        PersistentLogger(ctx).write({}, "Manually removed PDF file: $url")
    }

    /**
     * This function deletes individual PDF files from the internal PDF pool.
     * @param url the source URL associated with a given PDF to be deleted.
     */
    fun deletePdf(url: String) {
        val absolutePdfPath = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String

        // Actually deleting the PDF file.
        File(absolutePdfPath).let { f -> if (f.exists()) f.delete() }

        // Remove any reference to the PDF file in the LocalStorage.
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_LAST_ACCESS_MILLIS, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_LAST_DOWNLOAD_MILLIS, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_METADATA, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, url)
        LocalStorage(ctx).removeLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_MARKED_AS_FAVORITE, url)

        // Log the removal message persistently.
        PersistentLogger(ctx).write({}, "Manually removed PDF file: $url")
    }

    /**
     * (Primarily for use in ScreenPDFViewer.)
     * Enlists downloaded files and their local absolute path tuple in incremental manner.
     * The list will later be used to determine which PDF files to remove, to conserve space.
     * @param pdfAssociatedUrl the source URL associated with the downloaded PDF file.
     * @param pdfAbsolutePath the absolute path of the PDF file to be registered.
     */
    @Deprecated("The PDF cleaning up now uses the serialization of all LocalStorage shared preferences pair reading.")
    fun enlistDownloadedFileForCleanUp(pdfAssociatedUrl: String, pdfAbsolutePath: String) {
        // Get the date of the enlisting of this PDF file.
        val currentDate = System.currentTimeMillis()

        // Encoding the string.
        // (DO NOT CHANGE THE CONCATENATION ORDER IN FUTURE RELEASES!!!)
        val encodedString = pdfAssociatedUrl + PDF_CONTENT_TUPLE_DELIMITER + pdfAbsolutePath + PDF_CONTENT_TUPLE_DELIMITER + currentDate

        // Reading the current list of downloaded PDF files.
        var currentList = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LIST_OF_DOWNLOADED_PDF_CACHES, LocalStorageDataTypes.STRING) as String

        // Prevent duplication. Remove all previous instances of this file.
        currentList = currentList.replace(encodedString + PDF_LIST_DELIMITER, "")

        // Storing the new record.
        val newRecordString = currentList + encodedString + PDF_LIST_DELIMITER
        LocalStorage(ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LIST_OF_DOWNLOADED_PDF_CACHES, newRecordString, LocalStorageDataTypes.STRING)
    }
}

class InternalFileManagerCompanion {
    companion object {
        /* The "app_..." suffix name for downloading files. */
        const val FILE_CREATOR_TARGET_DATA_DIR = "data"
        const val FILE_CREATOR_TARGET_PDF_POOL_DIR = "pdf_pool"
        const val FILE_CREATOR_TARGET_BIBLE_POOL_DIR = "bible_pool"
    }
}