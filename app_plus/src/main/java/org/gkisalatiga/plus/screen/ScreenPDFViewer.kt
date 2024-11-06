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
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.MutableState
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.composable.PdfPage
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.PdfPageUiEvent
import org.gkisalatiga.plus.lib.PdfViewModel
import org.gkisalatiga.plus.lib.external.DownloadViewModel
import org.gkisalatiga.plus.lib.external.FileDownloadEvent
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.eBookUrl
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableBitmapMap
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableCurrentPDFPage
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutablePdfUiEventMessage
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutablePdfUiPageCount
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableTotalPDFPage
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableTriggerPDFPageRecomposition
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableTriggerPDFViewerRecomposition
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.navigatorLazyListState
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.rememberedViewerPagerState
import org.gkisalatiga.plus.services.InternalFileManager


class ScreenPDFViewer : ComponentActivity() {

    // The view model for downloading files with progress.
    private val downloadWithProgressViewModel = DownloadViewModel()

    // The view model for rendering the PDF files.
    private val pdfRendererViewModel = PdfViewModel()

    // When the value of this mutable variable is not null, it means the PDF file is loaded already.
    private var currentFilePdfRenderer: MutableState<PdfRenderer?> = mutableStateOf(null)

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        Scaffold (topBar = { getTopBar() }) {

            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
            ) { getMainContent() }

            // Scroll the horizontal navigator to the respective position when the pager is scrolled against.
            val scope = rememberCoroutineScope()
            LaunchedEffect(mutableCurrentPDFPage.intValue) {
                scope.launch {
                    navigatorLazyListState!!.animateScrollToItem(rememberedViewerPagerState!!.currentPage)
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
        val filePath = "/data/data/org.gkisalatiga.plus/app_Downloads/1730456953302.pdf"
        val lifecycleOwner = LocalLifecycleOwner.current
        val scope = rememberCoroutineScope()

        /* Displaying the main PDF content. */
        Column (Modifier.fillMaxSize()) {

            // The page navigator.
            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { /* Displays the custom page navigator (with typing)? */ }) {
                    Text((mutableCurrentPDFPage.intValue + 1).toString() + " / " + mutableTotalPDFPage.intValue.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = navigatorLazyListState!!) {
                    val totalPDFPage = mutableTotalPDFPage.intValue
                    if (totalPDFPage > 0) items(totalPDFPage) {
                        val actualPage = it + 1
                        TextButton(onClick = { scope.launch { rememberedViewerPagerState!!.animateScrollToPage(actualPage - 1) } }) {
                            Text(actualPage.toString(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // Attempt to download the file, if not exists.
            LaunchedEffect(Unit) {
                if (!isAlreadyDownloaded) {
                    handlePdfDownload(ctx, url, lifecycleOwner)
                }
            }

            // Ensures that the PDF is downloaded and loaded.
            LaunchedEffect(Unit) {
                /*pdfRendererViewModel.loadPdf(filePath).observe(lifecycleOwner) {
                    when (it) {
                        is PdfUiEvent.Error -> {
                            // DO NOTHING.
                            // mutablePdfUiEventMessage.value = it.message
                            // mutablePdfUiEventIdentifier.value = it.eventIdentifier
                        }
                        is PdfUiEvent.FileLoaded -> {
                            mutablePdfUiEventMessage.value = it.message
                            mutablePdfUiPageCount.intValue = it.pdfPageCount
                        }
                        is PdfUiEvent.FileLoading -> {}
                    }

                    // Trigger recomposition.
                    mutableTriggerPDFViewerRecomposition.value = !mutableTriggerPDFViewerRecomposition.value
                }*/

                currentFilePdfRenderer.value = pdfRendererViewModel.initPdfRenderer(filePath)
                mutableTriggerPDFViewerRecomposition.value = !mutableTriggerPDFViewerRecomposition.value
                mutablePdfUiPageCount.intValue = currentFilePdfRenderer.value!!.pageCount
                mutableTotalPDFPage.intValue = currentFilePdfRenderer.value!!.pageCount
            }

            // Only render the PDF if the file exists and the PDF viewer composable is triggerred. TODO: remove?
            key (mutableTriggerPDFViewerRecomposition.value, currentFilePdfRenderer.value) {
                if (rememberedViewerPagerState != null) Logger.logPDF({}, "mutablePdfUiPageCount.intValue: ${mutablePdfUiPageCount.intValue}, mutableTotalPDFPage.intValue: ${mutableTotalPDFPage.intValue}, rememberedViewerPagerState!!.currentPage: ${rememberedViewerPagerState!!.currentPage}, mutableCurrentPDFPage.intValue: ${mutableCurrentPDFPage.intValue}")

                // Initiating the pager state.
                val initialPage = 0
                rememberedViewerPagerState = rememberPagerState(initialPage = initialPage) { mutablePdfUiPageCount.intValue }

                HorizontalPager(rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) { pagerPage ->
                    // Updating the state of the currently selected PDF page.
                    // mutableCurrentPDFPage.intValue = pagerPage
                    mutableCurrentPDFPage.intValue = rememberedViewerPagerState!!.currentPage

                    // Request PDF page rendering.
                    LaunchedEffect(Unit) {
                        Logger.logTest({}, "pagerPage: $pagerPage, pagerPage+1: ${pagerPage + 1}", LoggerType.WARNING)
                        pdfRendererViewModel.loadPdfPage(pagerPage, 2).observe(lifecycleOwner) {
                            when (it) {
                                is PdfPageUiEvent.Error -> {
                                    Logger.logPDF({}, it.message)
                                }
                                is PdfPageUiEvent.PageLoading -> {
                                    Logger.logPDF({}, it.message)
                                    mutablePdfUiEventMessage.value = it.message
                                }
                                is PdfPageUiEvent.PageRendered -> {
                                    if (!mutableBitmapMap.containsKey(it.pageNumber) || mutableBitmapMap[it.pageNumber] == null || mutableBitmapMap[it.pageNumber]!!::class != Bitmap::class) {
                                        Logger.logPDF({}, it.message, LoggerType.INFO)
                                        // mutableBitmapList[pagerPage] = it.pageBitmap
                                        // if (mutableBitmapList.size < it.pageNumber) mutableBitmapList.add(it.pageNumber, it.pageBitmap)
                                        // else mutableBitmapList[it.pageNumber] = it.pageBitmap
                                        mutableBitmapMap[it.pageNumber] = it.pageBitmap

                                        Logger.logDump({}, mutableBitmapMap.toString(), LoggerType.VERBOSE)
                                    }

                                    // Triggering the recomposition, only if we are at the right page.
                                    if (mutableCurrentPDFPage.intValue == it.pageNumber) mutableTriggerPDFPageRecomposition.value = !mutableTriggerPDFPageRecomposition.value
                                }
                            }
                        }
                    }

                    key(mutableTriggerPDFPageRecomposition.value) {
                        // if (mutableBitmapList.size >= pagerPage && mutableBitmapList.size != 0) {
                        if (mutableBitmapMap.containsKey(pagerPage) && mutableBitmapMap[pagerPage] != null && mutableBitmapMap[pagerPage]!!::class == Bitmap::class) {
                            // PdfPage(mutableBitmapList[pagerPage])
                            PdfPage(mutableBitmapMap[pagerPage]!!)
                        }
                    }

                }  // --- end HorizontalPager.
            }  // --- end key.
        }  // --- end Column.
    }  // --- end getMainContent().

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
        val targetDownloadDir = InternalFileManager(ctx).PDF_POOL_FILE_CREATOR
        val targetFilename = System.currentTimeMillis()
        downloadWithProgressViewModel.downloadFile(url, "$targetFilename.pdf", targetDownloadDir).observe(lifecycleOwner) {
            when (it) {
                is FileDownloadEvent.Progress -> {
                    ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value = "Downloading... ${it.percentage}%"
                }

                is FileDownloadEvent.Success -> {
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
                    ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value = it.failure
                }
            }
        }
    }

    /**
     * This function handles the PDF file converstion to bitmaps
     * and stores the converted bitmaps locally in the app's internal storage.
     */

}

class ScreenPDFViewerCompanion : Application() {
    companion object {
        /* Stores the state of the current PDF page reading. */
        internal val mutableCallbackStatusMessage = mutableStateOf("")
        internal val mutableCurrentPDFPage = mutableIntStateOf(0)
        internal val mutableTotalPDFPage = mutableIntStateOf(0)

        /* TODO: DEBUG FOR TEST_004. */
        internal val mutablePdfUiEventMessage = mutableStateOf("")
        internal val mutablePdfUiPageCount = mutableIntStateOf(0)

        /* Set this flag to "true" in order to order for a new batch of PDF download. */
        internal var mutableBitmapMap: MutableMap<Int, Bitmap> = mutableMapOf()
        internal var mutableTriggerPDFViewerRecomposition = mutableStateOf(false)
        internal var mutableTriggerPDFPageRecomposition = mutableStateOf(false)

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

            /* Resetting the state of the current PDF viewer due to the existence of a new PDF file. */
            mutableBitmapMap = mutableMapOf<Int, Bitmap>()
            mutablePdfUiPageCount.intValue = 0
        }

        /* The pager state for the PDF viewer. */
        var rememberedViewerPagerState: PagerState? = null
    }
}