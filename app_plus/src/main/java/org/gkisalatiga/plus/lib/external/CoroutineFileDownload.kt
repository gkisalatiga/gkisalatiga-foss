/**
 * Copyright 2018 Jovche Mitrejchevski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ---
 * SOURCE: https://github.com/mitrejcevski/coroutineFileDownload
 */

package org.gkisalatiga.plus.lib.external

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.lib.Logger
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext

abstract class CoroutineViewModel : ViewModel(),
    CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}

class DownloadViewModel : CoroutineViewModel() {

    private lateinit var downloadsDirectory: File

    /**
     * Download a file while showing progress change to the user as the file is downloaded.
     * @param fileUrl the URL of the file to be downloaded.
     * @param targetFilename the target filename in which this file will be saved.
     * @param targetDir the target directory of storing the downloaded file.
     */
    fun downloadFile(fileUrl: String, targetFilename: String, targetDir: File): LiveData<FileDownloadEvent> {
        downloadsDirectory = targetDir
        val result = MutableLiveData<FileDownloadEvent>()
        if (fileUrl.isBlank()) {
            result.postValue(FileDownloadEvent.Failure("R.string.bad_download_url_provided"))
        } else if (!targetDir.isDirectory) {
            result.postValue(FileDownloadEvent.Failure("R.string.target_dir_not_a_directory"))
        } else {
            proceedFileDownload(fileUrl, targetFilename, result)
        }
        return result
    }

    private fun proceedFileDownload(fileUrl: String, targetFilename: String, result: MutableLiveData<FileDownloadEvent>) {
        launch(Dispatchers.IO) {
            try {
                performFileDownload(fileUrl, targetFilename, result)
                return@launch
            } catch (exception: IOException) {
                result.postValue(FileDownloadEvent.Failure("R.string.error_downloading_file"))
            }
        }
    }

    private fun performFileDownload(fileUrl: String, targetFilename: String, result: MutableLiveData<FileDownloadEvent>) {
        val downloadTarget = File(downloadsDirectory, targetFilename)
        val connection = URL(fileUrl).openConnection() as HttpURLConnection
        val contentLength = connection.contentLength
        val inputStream = BufferedInputStream(connection.inputStream)
        val outputStream = FileOutputStream(downloadTarget.path)
        val buffer = ByteArray(4096)
        var downloadedFileSize = 0L
        var currentRead = 0
        while (currentRead != -1 && isActive) {
            downloadedFileSize += currentRead
            outputStream.write(buffer, 0, currentRead)
            currentRead = inputStream.read(buffer, 0, buffer.size)
            val progress = (100f * (downloadedFileSize.toFloat() / contentLength.toFloat())).toInt()
            Logger.logRapidTest({}, "Download progress of $fileUrl: ${progress}%")
            result.postValue(FileDownloadEvent.Progress(progress))
        }
        outputStream.flush()  // --- ensuring all data is written to the file.
        result.postValue(FileDownloadEvent.Success(downloadTarget.path))
        outputStream.close()
        inputStream.close()
    }

    private fun fileName(fileUrl: String): String = fileUrl.substring(fileUrl.lastIndexOf("/") + 1, fileUrl.length)
    private fun targetFile(fileName: String): File = File(downloadsDirectory, fileName)
}

sealed class FileDownloadEvent {
    data class Progress(val percentage: Int) : FileDownloadEvent()
    data class Success(val downloadedFilePath: String) : FileDownloadEvent()
    data class Failure(val failure: String) : FileDownloadEvent()
}