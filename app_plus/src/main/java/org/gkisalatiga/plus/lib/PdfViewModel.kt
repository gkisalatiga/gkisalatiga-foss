/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.lib

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Handler
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext

abstract class CoroutineViewModel : ViewModel(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + job
    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}

/**
 * Creates a view model for asynchronous PDF rendering-to-bitmap.
 * SOURCE: https://developer.android.com/topic/libraries/architecture/viewmodel
 */
class PdfViewModel : CoroutineViewModel() {

    companion object {
        var pdfRenderer: PdfRenderer? = null
    }

    // private var pdfRenderer: PdfRenderer? = null
    // private var pdfRenderQuality: Int = 1
    // private var pdfPageCount: Int = 0

    /**
     * Opens a PDF file and return its pdfRenderer object.
     * @param absolutePath the absolute path to the PDF file to be opened.
     * @return the PdfRenderer file if the PDF file exists, null if otherwise.
     */
    fun getPdfRenderer(absolutePath: String) : PdfRenderer {
        val fileDescriptor = ParcelFileDescriptor.open(
            File(absolutePath),
            ParcelFileDescriptor.MODE_READ_ONLY,
            Handler()
        ) { Logger.logPDF({}, "Someone tried to close the PDF file!") }
        val renderer = PdfRenderer(fileDescriptor)

        Companion.pdfRenderer = renderer
        return renderer
    }

    /**
     * Opens the PDF file.
     * Only accepts locally stored path.
     * @param absolutePath the absolute path to the PDF file to be opened.
     * @param renderQuality determines the resolution of the rendered PDF page. higher value means better quality but slower rendering. The value must be > 0.
     * @return the [PdfUiEvent] resembling the current state of PDF rendering.
     */
    fun loadPdf(absolutePath: String) : MutableLiveData<PdfUiEvent> {
        val result = MutableLiveData<PdfUiEvent>()

        /*// Assigning class-wide internal variable.
        pdfRenderQuality = if (renderQuality > 0) renderQuality else 1

        // Mark this operation as "loading".
        result.value = PdfUiEvent.FileLoading("Loading and attempting to open the PDF file: $absolutePath")

        launch(Dispatchers.IO) {
            if (File(absolutePath).exists()) {
                // Opens the PDF file.
                pdfRenderer = ParcelFileDescriptor.open(
                    File(absolutePath),
                    ParcelFileDescriptor.MODE_READ_ONLY
                )?.use{ PdfRenderer(it) }

                // Counts how many pages are in the PDF file.
                pdfPageCount = pdfRenderer!!.pageCount
                result.postValue(PdfUiEvent.FileLoaded("Successfully loaded the PDF file: $absolutePath", pdfPageCount))
            } else {
                result.postValue(PdfUiEvent.Error("Cannot find the path to PDF file: $absolutePath"))
            }
        }*/
        result.value = PdfUiEvent.FileLoaded("Hail! $absolutePath", 500)

        // Expose the internal state/event of the PDF renderer.
        return result
    }

    /**
     * Renders a given PDF page as bitmap.
     * @param absolutePath the absolute path to the PDF file to be opened.
     * @param pageNumber the page number to be rendered as bitmap. Cannot be larger than [pdfPageCount].
     * @param renderQuality determines the resolution of the rendered PDF page. higher value means better quality but slower rendering. The value must be > 0.
     * @return the [PdfUiEvent] resembling the current state of PDF rendering.
     */
    fun renderPdfPage(absolutePath: String, pageNumber: Int, renderQuality: Int) : MutableLiveData<PdfPageUiEvent> {
        val result = MutableLiveData<PdfPageUiEvent>()

        // Assigning class-wide internal variable.
        val pdfRenderQuality = if (renderQuality > 0) renderQuality else 1

        // Mark this operation as "loading".
        result.value = PdfPageUiEvent.PageLoading("Rendering page number: $pageNumber")

        if (File(absolutePath).exists()) {
            launch(Dispatchers.IO) {
                // Loading the PDF file.
                val fileDescriptor = ParcelFileDescriptor.open(
                    File(absolutePath),
                    ParcelFileDescriptor.MODE_READ_ONLY
                )
                val pdfRenderer = PdfRenderer(fileDescriptor)

                // Counts how many pages are in the PDF file.
                val pdfPageCount = pdfRenderer.pageCount
                Logger.log({}, "pdfPageCount: $pdfPageCount")

                // Rendering the PDF page into bitmap.
                val pdfPage = pdfRenderer.openPage(pageNumber)
                val bitmap = Bitmap.createBitmap(
                    pdfPage.width * pdfRenderQuality,
                    pdfPage.height * pdfRenderQuality,
                    Bitmap.Config.ARGB_8888,
                )
                pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                pdfPage.close()

                // Closing the file input stream.
                pdfRenderer.close()

                // Posting the bitmap.
                // result.postValue(PdfPageUiEvent.PageRendered("Page number $pageNumber has been successfully rendered!", bitmap))
            }
        } else {
            result.postValue(PdfPageUiEvent.Error("NPE! The pdfRenderer has not yet been loaded."))
        }

        return result
    }

    /**
     * Renders a given PDF page as bitmap.
     * @param pdfRenderer the PdfRenderer object to be used in the rendering.
     * @param pageNumber the page number to be rendered as bitmap. Cannot be larger than [pdfPageCount].
     * @param renderQuality determines the resolution of the rendered PDF page. higher value means better quality but slower rendering. The value must be > 0.
     * @return the [PdfUiEvent] resembling the current state of PDF rendering.
     */
    fun loadPdfPage(renderer: PdfRenderer?, pageNumber: Int, renderQuality: Int) : MutableLiveData<PdfPageUiEvent> {
        val result = MutableLiveData<PdfPageUiEvent>()

        // DEBUG!
        Logger.logTest({}, "Companion.pdfRenderer is null?: ${Companion.pdfRenderer == null}")
        if (Companion.pdfRenderer != null) {
            Logger.logTest({}, "Companion.pdfRenderer pageCount: ${Companion.pdfRenderer!!.pageCount}")
        }

        // Prevents NPE.
        if (Companion.pdfRenderer == null) {
            result.value = PdfPageUiEvent.Error("NPE detected in the PdfRenderer!")
            return result
        }
        /*if (renderer == null) {
            result.value = PdfPageUiEvent.Error("NPE detected in the PdfRenderer!")
            return result
        }*/

        // Assigning class-wide internal variable.
        val pdfRenderQuality = if (renderQuality > 0) renderQuality else 1
        val pdfRenderer = Companion.pdfRenderer!!

        // Mark this operation as "loading".
        // result.value = PdfPageUiEvent.PageLoading("Rendering page number: $pageNumber")
        result.value = PdfPageUiEvent.PageLoading("1l2VKE9tDWoVe40JYEJXDF-7yDWrRHCIu")

        launch(Dispatchers.IO) {
            while (true) {
                try {
                    // result.postValue(PdfPageUiEvent.PageLoading("Finished rendering!"))
                    result.postValue(PdfPageUiEvent.PageLoading("1vZHZfLyfUKwqM2MJKl76hI6_J2GYmajA"))
                    // Counts how many pages are in the PDF file.
                    val pdfPageCount = pdfRenderer.pageCount
                    Logger.log({}, "pdfPageCount: $pdfPageCount")

                    // Rendering the PDF page into bitmap.
                    val pdfPage = pdfRenderer.openPage(pageNumber)
                    val bitmap = Bitmap.createBitmap(
                        pdfPage.width * pdfRenderQuality,
                        pdfPage.height * pdfRenderQuality,
                        Bitmap.Config.ARGB_8888,
                    )
                    pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                    pdfPage.close()

                    // Posting the bitmap.
                    result.postValue(PdfPageUiEvent.PageRendered("Page number $pageNumber has been successfully rendered!", pageNumber, bitmap))

                    // Break free from this loop!
                    break

                } catch (e: IllegalStateException) {
                    result.postValue(PdfPageUiEvent.Error("Encountered error at pageNumber $pageNumber as usual: ${e.message}"))

                    // Do loop until we get the PDF page rendered!
                    delay(50L)
                    continue
                }
            }
        }

        /*if (pdfRenderer != null) {

        } else {
            result.postValue(PdfPageUiEvent.Error("NPE! The pdfRenderer has not yet been loaded."))
        }*/

        return result
    }
}

sealed class PdfUiEvent {
    data class FileLoading(val message: String, val eventIdentifier: PdfUiEventIdentifier = PdfUiEventIdentifier.EVENT_FILE_LOADING) : PdfUiEvent()
    data class FileLoaded(val message: String, val pdfPageCount: Int, val eventIdentifier: PdfUiEventIdentifier = PdfUiEventIdentifier.EVENT_FILE_LOADED) : PdfUiEvent()
    data class Error(val message: String, val eventIdentifier: PdfUiEventIdentifier = PdfUiEventIdentifier.EVENT_ERROR) : PdfUiEvent()
}

sealed class PdfPageUiEvent {
    data class PageLoading(val message: String, val eventIdentifier: PdfPageUiEventIdentifier = PdfPageUiEventIdentifier.EVENT_PAGE_LOADING) : PdfPageUiEvent()
    data class PageRendered(val message: String, val pageNumber: Int, val pageBitmap: Bitmap, val eventIdentifier: PdfPageUiEventIdentifier = PdfPageUiEventIdentifier.EVENT_PAGE_RENDERED) : PdfPageUiEvent()
    data class Error(val message: String, val eventIdentifier: PdfPageUiEventIdentifier = PdfPageUiEventIdentifier.EVENT_ERROR) : PdfPageUiEvent()
}

enum class PdfUiEventIdentifier {
    EVENT_FILE_LOADING,
    EVENT_FILE_LOADED,
    EVENT_ERROR,
}

enum class PdfPageUiEventIdentifier {
    EVENT_PAGE_LOADING,
    EVENT_PAGE_RENDERED,
    EVENT_ERROR,
}