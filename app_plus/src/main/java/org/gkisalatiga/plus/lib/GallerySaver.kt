/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.lib

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.screen.ScreenGaleriViewCompanion
import java.io.IOException
import java.io.OutputStream
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import java.util.concurrent.Executors


/**
 * SOURCE: https://www.techotopia.com/index.php/A_Kotlin_Android_Storage_Access_Framework_Example
 */
class GallerySaver {

    companion object {
        /* SAF create document code. */
        const val GALLERY_SAVER_CODE = 40
    }

    fun saveImageFromURL(ctx: Context, imageURL: String, imageName: String) {
        ScreenGaleriViewCompanion.targetGoogleDrivePhotoURL = imageURL
        ScreenGaleriViewCompanion.targetSaveFilename = imageName

        // Create a new intent.
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)

        // Open the SAF dialog.
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_TITLE, imageName)
        startActivityForResult(ctx as Activity, intent, GALLERY_SAVER_CODE, null)
        Logger.logTest({}, "Is this block executed? (2)")
    }

    fun onSAFPathReceived(outputStream: OutputStream) {
        // Avoid "NetworkOnMainThread" exception.
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {

            // Display the download progress circle.
            ScreenGaleriViewCompanion.showScreenGaleriViewDownloadProgress.value = true

            try {
                // Opening the file download stream.
                Logger.logDump({}, ScreenGaleriViewCompanion.targetGoogleDrivePhotoURL)
                val streamIn = java.net.URL(ScreenGaleriViewCompanion.targetGoogleDrivePhotoURL).openStream()

                // Coverting input stream (bytes) to string.
                // SOURCE: http://stackoverflow.com/questions/49467780/ddg#49468129
                val decodedData: ByteArray = streamIn.readBytes()

                // Writing into the file.
                outputStream.flush()
                outputStream.write(decodedData)
                outputStream.close()

                // Show some successful alert. TODO: Extract string to allow localization.
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogTitle = "File Terunduh!"
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogSubtitle = "Berhasil mengunduh \"${ScreenGaleriViewCompanion.targetSaveFilename}\""
                ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = true

                GlobalCompanion.isConnectedToInternet.value = true

            } catch (e: ConnectException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "ConnectException: ${e.message}", LoggerType.ERROR)

                // Show some failure alert. TODO: Extract string to allow localization.
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogTitle = "Gagal Mengunduh!"
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogSubtitle = "Koneksi terputus. Silahkan periksa sambungan internet perangkat Anda: ${e.message}"
                ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = true
            } catch (e: IOException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "IOException: ${e.message}", LoggerType.ERROR)

                // Show some failure alert. TODO: Extract string to allow localization.
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogTitle = "Gagal Mengunduh!"
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogSubtitle = "Koneksi terputus. Silahkan periksa sambungan internet perangkat Anda: ${e.message}"
                ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = true
            } catch (e: SocketException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "SocketException: ${e.message}", LoggerType.ERROR)

                // Show some failure alert. TODO: Extract string to allow localization.
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogTitle = "Gagal Mengunduh!"
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogSubtitle = "Koneksi terputus. Silahkan periksa sambungan internet perangkat Anda: ${e.message}"
                ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = true
            } catch (e: UnknownHostException) {
                GlobalCompanion.isConnectedToInternet.value = false
                Logger.logTest({}, "UnknownHostException: ${e.message}", LoggerType.ERROR)

                // Show some failure alert. TODO: Extract string to allow localization.
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogTitle = "Gagal Mengunduh!"
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogSubtitle = "Koneksi terputus. Silahkan periksa sambungan internet perangkat Anda: ${e.message}"
                ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = true
            } catch (e: Exception) {
                Logger.logTest({}, "Exception: ${e.message}", LoggerType.ERROR)

                // Show some failure alert. TODO: Extract string to allow localization.
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogTitle = "Gagal Mengunduh!"
                ScreenGaleriViewCompanion.txtScreenGaleriViewAlertDialogSubtitle = "Eror yang belum pernah ditangani sebelumnya terdeteksi: ${e.message}"
                ScreenGaleriViewCompanion.showScreenGaleriViewAlertDialog.value = true
            }

            // Break free from this thread.
            ScreenGaleriViewCompanion.showScreenGaleriViewDownloadProgress.value = false
            executor.shutdown()
        }
    }
}