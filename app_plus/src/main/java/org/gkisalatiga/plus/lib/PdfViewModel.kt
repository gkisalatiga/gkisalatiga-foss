/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.lib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Handler
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.model.CoroutineViewModel
import java.io.File

/**
 * Creates a view model for asynchronous PDF rendering-to-bitmap.
 * SOURCE: https://developer.android.com/topic/libraries/architecture/viewmodel
 */
class PdfViewModel(ctx: Context) : CoroutineViewModel() {

    companion object {
        var pdfRenderer: PdfRenderer? = null
    }

    // The rendering quality of this PDF instance.
    private val pdfRenderQuality = AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_PDF_RENDER_QUALITY_FACTOR) as Int

    /**
     * Opens a PDF file and return its pdfRenderer object.
     * @param absolutePath the absolute path to the PDF file to be opened.
     * @return the PdfRenderer file if the PDF file exists, null if otherwise.
     */
    fun initPdfRenderer(absolutePath: String) : PdfRenderer? {
        if (File(absolutePath).exists()) {
            val fileDescriptor = ParcelFileDescriptor.open(
                File(absolutePath),
                ParcelFileDescriptor.MODE_READ_ONLY,
                Handler()
            ) { Logger.logPDF({}, "Someone tried to close the PDF file!") }
            val renderer = PdfRenderer(fileDescriptor)

            // Statically store the PdfRenderer so that it can be accessed later.
            pdfRenderer = renderer
        }

        return pdfRenderer
    }

    /**
     * Renders a given PDF page as bitmap.
     * @param pdfRenderer the PdfRenderer object to be used in the rendering.
     * @param pageNumber the page number to be rendered as bitmap. Cannot be larger than [pdfPageCount].
     * @param renderQuality determines the resolution of the rendered PDF page. higher value means better quality but slower rendering. The value must be > 0.
     * @return the [PdfUiEvent] resembling the current state of PDF rendering.
     */
    fun loadPdfPage(pageNumber: Int, renderQuality: Int) : MutableLiveData<PdfPageUiEvent> {
        val result = MutableLiveData<PdfPageUiEvent>()

        // Debug logging, for solving problems.
        Logger.logPDF({}, "Companion.pdfRenderer is null?: ${Companion.pdfRenderer == null}")

        if (Companion.pdfRenderer != null) {
            Logger.logTest({}, "Companion.pdfRenderer pageCount: ${Companion.pdfRenderer!!.pageCount}")
        } else {
            // Prevents NPE.
            result.postValue(PdfPageUiEvent.Error("NPE detected in the PdfRenderer!"))
            return result
        }

        // Assigning class-wide internal variable.
        val pdfRenderer = Companion.pdfRenderer!!

        launch(Dispatchers.IO) {
            while (true) {
                try {
                    // Mark this operation as "loading".
                    result.postValue(PdfPageUiEvent.PageLoading("Loading page number: $pageNumber"))

                    // Counts how many pages are in the PDF file.
                    val pdfPageCount = pdfRenderer.pageCount
                    Logger.logPDF({}, "pdfPageCount: $pdfPageCount")

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
                    result.postValue(PdfPageUiEvent.Error("IllegalStateException [pg. $pageNumber]: ${e.message}"))

                    // Do loop until we get the PDF page rendered!
                    delay(50L)
                    continue
                } catch (e: RuntimeException) {
                    result.postValue(PdfPageUiEvent.Error("RuntimeException [pg. $pageNumber]: ${e.message}"))
                    break
                }
            }
        }

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