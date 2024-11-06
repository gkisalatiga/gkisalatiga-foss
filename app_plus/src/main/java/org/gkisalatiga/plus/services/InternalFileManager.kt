/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.content.Context
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys

/**
 * Manages the app's internal file management.
 * @param ctx the current local context of the app.
 */
class InternalFileManager (private val ctx: Context) {
    // The file creator to create the private file (as a downloaded file).
    val CACHE_FILE_CREATOR = ctx.cacheDir
    val DATA_DIR_FILE_CREATOR = ctx.getDir(InternalFileManagerCompanion.FILE_CREATOR_TARGET_DATA_DIR, Context.MODE_PRIVATE)
    val DOWNLOAD_FILE_CREATOR = ctx.getDir(InternalFileManagerCompanion.FILE_CREATOR_TARGET_DOWNLOAD_DIR, Context.MODE_PRIVATE)
    val PDF_POOL_PDF_FILE_CREATOR = ctx.getDir(InternalFileManagerCompanion.FILE_CREATOR_TARGET_PDF_POOL_DIR, Context.MODE_PRIVATE)

    // The string delimiter for encoding pdfAssociatedKey-pdfAbsolutePath pair as string.
    // (Chosen at random. DO NOT CHANGE IN FUTURE RELEASES!!!)
    private val PDF_LIST_DELIMITER = "$$#^"
    private val PDF_CONTENT_TUPLE_DELIMITER = ",,^,"

    /**
     * (Primarily for use in ScreenPDFViewer.)
     * Enlists downloaded files and their local absolute path tuple in incremental manner.
     * The list will later be used to determine which PDF files to remove, to conserve space.
     * @param pdfAssociatedUrl the source URL associated with the downloaded PDF file.
     * @param pdfAbsolutePath the absolute path of the PDF file to be registered.
     */
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
        const val FILE_CREATOR_TARGET_DOWNLOAD_DIR = "Downloads"
        const val FILE_CREATOR_TARGET_PDF_POOL_DIR = "pdf_pool"
    }
}