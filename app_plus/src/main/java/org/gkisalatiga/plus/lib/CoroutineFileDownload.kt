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

package org.gkisalatiga.plus.lib

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.model.CoroutineViewModel
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.URL
import java.net.UnknownHostException
import java.util.concurrent.Executors

class CoroutineFileDownload : CoroutineViewModel() {

    companion object {
        private var isDownloadSucceeded = false
        private var isDownloadCancelled = false
    }

    private lateinit var downloadsDirectory: File
    private val coroutineThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    /**
     * Cancels the download process.
     */
    fun cancelDownload() {
        Logger.logDownloader({}, "Retrieved call to cancel the download process.", LoggerType.VERBOSE)
        isDownloadCancelled = true
    }

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

        // Dispatches the actual downloader.
        val job = launch(coroutineThread) {
            try {
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

                    // Negative download percentage means the link cannot be downloaded using this method.
                    if (progress < 0) throw FileNotDownloadableException("Negative download progress detected: $progress%. The remote link $fileUrl may not be downloadable!")

                    Logger.logRapidTest({}, "Download progress of $fileUrl: $progress%")
                    result.postValue(FileDownloadEvent.Progress(progress))
                }

                // Closing the file.
                Logger.logDownloader({}, "Closing the local file ...", LoggerType.VERBOSE)
                outputStream.flush()  // --- ensuring all data is written to the file.
                outputStream.close()
                inputStream.close()

                // Report successful download.
                if (currentRead == -1) isDownloadSucceeded = true
                return@launch

            } catch (e: ConnectException) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(FileDownloadEvent.Failure(msg))
                Logger.logDownloader({}, msg, LoggerType.ERROR)

            } catch (e: SocketException) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(FileDownloadEvent.Failure(msg))
                Logger.logDownloader({}, msg, LoggerType.ERROR)

            } catch (e: UnknownHostException) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(FileDownloadEvent.Failure(msg))
                Logger.logDownloader({}, msg, LoggerType.ERROR)

            } catch (e: IOException) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(FileDownloadEvent.Failure(msg))
                Logger.logDownloader({}, msg, LoggerType.ERROR)

            } catch (e: FileNotDownloadableException) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(FileDownloadEvent.Failure(msg))
                Logger.logDownloader({}, msg, LoggerType.ERROR)

            } catch (e: Exception) {
                val msg = "${e::class.simpleName}: ${e.message} ${e.stackTraceToString()}"
                result.postValue(FileDownloadEvent.Failure(msg))
                Logger.logDownloader({}, msg, LoggerType.ERROR)

            }
        }

        // The default message displayed when the job finishes.
        job.invokeOnCompletion {
            Logger.logDownloader({}, "Invoked the completion of the downloading of: ${fileUrl}", LoggerType.VERBOSE)
        }

        // Detects if the download is cancelled by the client.
        // SOURCE: https://stackoverflow.com/a/76646799
        launch(Dispatchers.Default) {
            try {
                while (true) {
                    if (isDownloadCancelled) {
                        isDownloadCancelled = false
                        Logger.logDownloader({}, "Cancellation signal accepted and the download has been terminated!", LoggerType.VERBOSE)
                        job.cancelChildren()
                        job.cancel(); job.join()
                        coroutineThread.close()
                        throw DownloadCancelledByClientException()
                    }
                    if (isDownloadSucceeded) {
                        isDownloadSucceeded = false
                        val downloadTarget = File(downloadsDirectory, targetFilename)
                        Logger.logDownloader({}, "Download successful: $downloadTarget", LoggerType.VERBOSE)
                        result.postValue(FileDownloadEvent.Success(downloadTarget.path))
                        break
                    }
                }
            } catch (e: DownloadCancelledByClientException) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(FileDownloadEvent.Cancelled(msg))
                Logger.logDownloader({}, msg, LoggerType.WARNING)
            }
        }

    }  // --- end of proceedFileDownload().

}

sealed class FileDownloadEvent {
    data class Progress(val percentage: Int) : FileDownloadEvent()
    data class Success(val downloadedFilePath: String) : FileDownloadEvent()
    data class Failure(val failure: String) : FileDownloadEvent()
    data class Cancelled(val message: String) : FileDownloadEvent()
}