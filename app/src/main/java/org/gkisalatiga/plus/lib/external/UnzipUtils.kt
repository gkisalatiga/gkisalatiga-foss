/**
 * Copyright 2020 Nitin Prakash
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
 */
/** SOURCE: https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae */

package org.gkisalatiga.plus.lib.external

import android.util.Log
import org.gkisalatiga.plus.global.GlobalSchema
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipFile

/**
 * UnzipUtils class extracts files and sub-directories of a standard zip file to
 * a destination directory.
 *
 */
class UnzipUtils {

    /**
     * Size of the buffer to read/write data
     */
    private val BUFFER_SIZE = 4096

    /**
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    @Throws(IOException::class)
    fun unzip(zipFilePath: File, destDirectory: String) {

        File(destDirectory).run {
            if (!exists()) { mkdirs() }
        }

        ZipFile(zipFilePath).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                zip.getInputStream(entry).use { input ->

                    val filePath = destDirectory + File.separator + entry.name

                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else {
                        // Do nothing. The script is created to automatically create parent directories.
                    }

                }
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param inputStream
     * @param destFilePath
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Zip", "[UnzipUtils.extractFile] Extracting file into: $destFilePath")

        // Create the file if not exists.
        // SOURCE: https://stackoverflow.com/a/3090789
        File(destFilePath).parentFile?.mkdirs()
        File(destFilePath).createNewFile()

        // Extract and write the file.
        val bos = BufferedOutputStream(FileOutputStream(File(destFilePath)))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

}
