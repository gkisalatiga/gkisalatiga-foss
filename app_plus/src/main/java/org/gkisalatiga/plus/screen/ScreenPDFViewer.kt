/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the PDF using internal PDF viewer.
 * This ensures that the fetched online PDF file is cached,
 * ensuring readability when the app goes offline.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.rajat.pdfviewer.HeaderData
import com.rajat.pdfviewer.PdfRendererView
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Downloader
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.external.DownloadViewModel
import org.gkisalatiga.plus.lib.external.FileDownloadEvent
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.eBookUrl
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableTriggerPDFViewerRecomposition
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.navigatorLazyListState
import org.gkisalatiga.plus.services.InternalFileManager
import java.io.File


class ScreenPDFViewer : ComponentActivity() {

    // The view model for downloading files with progress.
    private val downloadWithProgressViewModel = DownloadViewModel()

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        Scaffold (topBar = { getTopBar() }) {

            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
            ) {
                // Display the main "attribution" contents.
                getMainContent()
            }

            // Also scroll the horizontal navigator to the respective position.
            val scope = rememberCoroutineScope()
            LaunchedEffect(ScreenPDFViewerCompanion.mutableCurrentPDFPage.intValue) {
                scope.launch {
                    navigatorLazyListState!!.animateScrollToItem(ScreenPDFViewerCompanion.mutableCurrentPDFPage.intValue)
                }
            }

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    private fun getMainContent() {
        val ctx = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val lifecycleScope = lifecycleOwner.lifecycleScope
        val pdfRendererViewInstance = ScreenPDFViewerCompanion.pdfRendererViewInstance!!

        /* Displaying the main PDF content. */
        Column (Modifier.fillMaxSize()) {
            val url = ScreenPDFViewerCompanion.eBookUrl

            // The page navigator.
            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { /* Displays the custom page navigator (with typing)? */ }) {
                    Text((ScreenPDFViewerCompanion.mutableCurrentPDFPage.intValue + 1).toString() + " / " + ScreenPDFViewerCompanion.mutableTotalPDFPage.intValue.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = ScreenPDFViewerCompanion.navigatorLazyListState!!) {
                    val totalPDFPage = ScreenPDFViewerCompanion.mutableTotalPDFPage.intValue
                    if (totalPDFPage > 0)
                    items(totalPDFPage) {
                        val actualPage = it + 1
                        TextButton(onClick = { pdfRendererViewInstance.jumpToPage(actualPage - 1) }) {
                            Text(actualPage.toString(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // Text(ScreenLibraryCompanion.currentpg.toString())
            Text(ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value)

            val isAlreadyDownloaded = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
            val absolutePDFPathIfCached = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String

            // Redundant logging for debugging.
            Logger.logPDF({}, "url: $url, isAlreadyDownloaded: $isAlreadyDownloaded, absolutePDFPathIfCached: $absolutePDFPathIfCached")

            // Attempt to download the file, if not exists.
            if (!isAlreadyDownloaded) {
                // Request for the downloading of a new PDF file.
                mutableTriggerPDFViewerRecomposition.value = true
                // Downloader(ctx).initRemotePDF(url, lifecycleScope, ScreenPDFViewerCompanion.pdfRendererViewInstance!!.statusListener!!)

                Logger.logTest({}, "[ABG] 1")
                // --- ensuring that this block will only be ran once every time.
                LaunchedEffect(Unit) { handlePdfDownload(ctx, url, lifecycleOwner) }
            }

            // Displaying the PDF file.
            /*if (isAlreadyDownloaded) initWithFile(File(absolutePDFPathIfCached))
            else*/ // initWithUrl(url, headers, lifecycleScope, lifecycleOwner.lifecycle)
            // TODO: Make a self-created implementation to check if the PDF is already downloaded, then also all PDF downloaded files except the latest N. (For storage management.)

            // Displaying the PDF.
            key (mutableTriggerPDFViewerRecomposition.value, true) {
                // The actual PDF renderer and viewer.
                AndroidView(
                    factory = {
                        pdfRendererViewInstance.apply {
                            // Double-checking if the file is already downloaded.
                            val isDownloaded = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean

                            // Display the file if already downloaded.
                            if (isDownloaded) initWithFile(File(absolutePDFPathIfCached))

                            // TODO: Remove.
                            // initWithUrl(url, headers, lifecycleScope, lifecycleOwner.lifecycle)
                            // initWithFile(File("/data/user/0/org.gkisalatiga.plus/cache/-1903487257.pdf"))
                        }.also {
                            // Prevents error: java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
                            // SOURCE: https://stackoverflow.com/a/73407161
                            if(it.parent != null) (it.parent as ViewGroup).removeView(it)
                        }
                    },
                    update = { /* Update logic if needed. */ },
                    modifier = Modifier
                )
            }

            // Placebo.
            Text("ew")
        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    ScreenPDFViewerCompanion.eBookTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { AppNavigation.popBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Open the info menu of the e-book/PDF viewer."
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    /**
     * This function handles the PDF downloading.
     */
    private fun handlePdfDownload(ctx: Context, url: String, lifecycleOwner: LifecycleOwner) {
        val targetDownloadDir = InternalFileManager(ctx).DOWNLOAD_FILE_CREATOR
        downloadWithProgressViewModel.downloadFile(url, "kudai.pdf", targetDownloadDir).observe(lifecycleOwner) {
            Logger.logTest({}, "[ABG] 10_${it}")
            when (it) {
                is FileDownloadEvent.Progress -> {
                    Logger.logTest({}, "[ABG] 1a")
                    ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value = "Downloading... ${it.percentage}%"
                }

                is FileDownloadEvent.Success -> {
                    Logger.logTest({}, "[ABG] 1b")
                    ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value = "Success! Downloaded to: ${it.downloadedFilePath}"

                    val outputPath = it.downloadedFilePath

                    // Ensure that we don't download this PDF file again in the future.
                    LocalStorage(ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, true, LocalStorageDataTypes.BOOLEAN, url)
                    LocalStorage(ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, outputPath, LocalStorageDataTypes.STRING, url)

                    // Register the file in the app's internal file manager so that it will be scheduled for cleaning.
                    InternalFileManager(ctx).enlistDownloadedFileForCleanUp(eBookUrl, outputPath)

                    // Trigger recomposition of the PDF viewer, if this is new download.
                    mutableTriggerPDFViewerRecomposition.value = !mutableTriggerPDFViewerRecomposition.value
                }

                is FileDownloadEvent.Failure -> {
                    Logger.logTest({}, "[ABG] 1c")
                    ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value = it.failure
                }
            }
        }
    }

}

class ScreenPDFViewerCompanion : Application() {
    companion object {
        /* Stores the state of the current PDF page reading. */
        internal val mutableCallbackStatusMessage = mutableStateOf("")
        internal val mutableCurrentPDFPage = mutableIntStateOf(0)
        internal val mutableTotalPDFPage = mutableIntStateOf(0)

        /* Set this flag to "true" in order to order for a new batch of PDF download. */
        internal var mutableTriggerPDFViewerRecomposition = mutableStateOf(false)

        /* These information are essential to the screen. */
        internal var eBookTitle: String = String()
        internal var eBookAuthor: String = String()
        internal var eBookPublisher: String = String()
        internal var eBookPublisherLoc: String = String()
        internal var eBookYear: String = String()
        internal var eBookThumbnail: String = String()
        internal var eBookUrl: String = String()
        internal var eBookSource: String = String()
        internal var eBookSize: String = String()

        /* The lazy list state of the horizontal page navigator. */
        var navigatorLazyListState: LazyListState? = null

        /**
         * This function initializes the status callback handler of the PDF viewer.
         * It returns unit and takes no argumen.
         */
        fun initPDFViewerCallbackHandler(ctx: Context) {
            val pdfRendererViewInstance = pdfRendererViewInstance!!
            pdfRendererViewInstance.statusListener = object: PdfRendererView.StatusCallBack {
                override fun onPageChanged(currentPage: Int, totalPage: Int) {
                    Logger.logRapidTest({}, "onPageChanged -> currentPage: $currentPage, totalPage: $totalPage", LoggerType.VERBOSE)
                    mutableCurrentPDFPage.intValue = currentPage
                    mutableTotalPDFPage.intValue = totalPage
                }

                // TODO: Consider removing.
                /*override fun onError(error: Throwable) {
                    super.onError(error)
                    mutableCallbackStatusMessage.value = error.message!!
                    Logger.logPDF({}, "onError -> error.message!!: ${error.message!!}")
                }

                override fun onPdfLoadProgress(
                    progress: Int,
                    downloadedBytes: Long,
                    totalBytes: Long?
                ) {
                    super.onPdfLoadProgress(progress, downloadedBytes, totalBytes)
                    mutableCallbackStatusMessage.value = "Mengunduh: $progress. $downloadedBytes/$totalBytes"  // --- TODO: Extract string to XML.
                    Logger.logRapidTest({}, "onPdfLoadProgress -> progress: $progress, downloadedBytes: $downloadedBytes, totalBytes: $totalBytes")
                }

                override fun onPdfLoadSuccess(absolutePath: String) {
                    super.onPdfLoadSuccess(absolutePath)
                    mutableCallbackStatusMessage.value = "Sukses mengunduh: $absolutePath"  // --- TODO: Extract string to XML.
                    Logger.logPDF({}, "onPdfLoadSuccess -> absolutePath: $absolutePath")

                    // Trigger the composition of the PDF file.
                    mutableTriggerPDFViewerRecomposition.value = !mutableTriggerPDFViewerRecomposition.value
                }

                override fun onPdfLoadStart() {
                    super.onPdfLoadStart()
                    mutableCallbackStatusMessage.value = "Memuat PDF..."  // --- TODO: Extract string to XML.
                    Logger.logPDF({}, "onPdfLoadStart (no parameter provided).")
                }*/
            }
        }

        /**
         * This function neatly and thoroughly passes the respective arguments to the screen's handler.
         * @param title the title of the e-book.
         * @param author the author(s) of the e-book.
         * @param publisher the publisher of the e-book.
         * @param publisherLoc the publisher location of the e-book.
         * @param year the publication year of the e-book.
         * @param thumbnail the image thumbnail of the e-book.
         * @param url the download URL of the e-book.
         * @param source the download source of the e-book.
         * @param size the size (in MB) of the e-book.
         */
        fun putArguments(title: String, author: String, publisher: String, publisherLoc: String, year: String, thumbnail: String, url: String, source: String, size: String) {
            eBookTitle = title
            eBookAuthor = author
            eBookPublisher = publisher
            eBookPublisherLoc = publisherLoc
            eBookYear = year
            eBookThumbnail = thumbnail
            eBookUrl = url
            eBookSource = source
            eBookSize = size
        }

        /* This view instance ensures the PDF file can be viewed. */
        @SuppressLint("StaticFieldLeak")
        var pdfRendererViewInstance: PdfRendererView? = null
    }
}