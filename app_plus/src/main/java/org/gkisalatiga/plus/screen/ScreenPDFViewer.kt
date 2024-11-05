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
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.rajat.pdfviewer.PdfRendererView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable
import org.gkisalatiga.plus.composable.PdfPage
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.PdfPageUiEvent
import org.gkisalatiga.plus.lib.PdfUiEvent
import org.gkisalatiga.plus.lib.PdfUiEventIdentifier
import org.gkisalatiga.plus.lib.PdfViewModel
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.lib.external.DownloadViewModel
import org.gkisalatiga.plus.lib.external.FileDownloadEvent
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.eBookUrl
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableBitmapList
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
import org.json.JSONObject
import java.io.File


class ScreenPDFViewer : ComponentActivity() {

    // The view model for downloading files with progress.
    private val downloadWithProgressViewModel = DownloadViewModel()

    // The view model for rendering the PDF files.
    private val pdfRendererViewModel = PdfViewModel()

    // TODO: DEBUG: Test_004
    private var currentFilePdfRenderer: MutableState<PdfRenderer?> = mutableStateOf(null)

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
            ) { /* getMainContent() */ test_004() }

            // Update the page state when the pager is scrolled against. TODO: remove?
            /*key(rememberedViewerPagerState!!.currentPage) {
                mutableCurrentPDFPage.intValue = rememberedViewerPagerState!!.currentPage
                Logger.logTest({}, "key -> rememberedViewerPagerState!!.currentPage: ${rememberedViewerPagerState!!.currentPage}")
            }*/
            /*key (rememberedViewerPagerState!!.currentPage) {
                mutableCurrentPDFPage.intValue = rememberedViewerPagerState!!.currentPage
                Logger.logRapidTest({}, "key -> rememberedViewerPagerState!!.currentPage: ${rememberedViewerPagerState!!.currentPage}")
            }*/

            // Sroll the horizontal navigator to the respective position.
            val scope = rememberCoroutineScope()
            /*LaunchedEffect(mutableCurrentPDFPage.intValue) {
                scope.launch {
                    navigatorLazyListState!!.animateScrollToItem(rememberedViewerPagerState!!.currentPage)
                }
            }*/
            LaunchedEffect(mutableCurrentPDFPage.intValue) {
                scope.launch {
                    navigatorLazyListState!!.animateScrollToItem(rememberedViewerPagerState!!.currentPage)
                }
            }
            // TODO: These cause lags when turning pages. Change the implementation?
            /*LaunchedEffect(mutableCurrentPDFPage.intValue) {
                scope.launch {
                    navigatorLazyListState!!.animateScrollToItem(mutableCurrentPDFPage.intValue)
                    rememberedViewerPagerState!!.animateScrollToPage(mutableCurrentPDFPage.intValue)
                }
            }*/
            /*LaunchedEffect(mutableCurrentPDFPage.intValue) {
                scope.launch {
                    navigatorLazyListState!!.animateScrollToItem(mutableCurrentPDFPage.intValue)
                    rememberedViewerPagerState!!.animateScrollToPage(mutableCurrentPDFPage.intValue)
                    Logger.logRapidTest({}, "key -> rememberedViewerPagerState!!.currentPage: ${rememberedViewerPagerState!!.currentPage}")
                }
            }*/

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    private fun getMainContent() {
        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()
        val lifecycleOwner = LocalLifecycleOwner.current
        val lifecycleScope = lifecycleOwner.lifecycleScope
        val pdfRendererViewInstance = ScreenPDFViewerCompanion.pdfRendererViewInstance!!

        /* Displaying the main PDF content. */
        Column (Modifier.fillMaxSize()) {
            val url = eBookUrl

            // The page navigator.
            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { /* Displays the custom page navigator (with typing)? */ }) {
                    Text((mutableCurrentPDFPage.intValue + 1).toString() + " / " + ScreenPDFViewerCompanion.mutableTotalPDFPage.intValue.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = navigatorLazyListState!!) {
                    val totalPDFPage = mutableTotalPDFPage.intValue
                    if (totalPDFPage > 0)
                    items(totalPDFPage) {
                        val actualPage = it + 1
                        TextButton(onClick = {
                            scope.launch { rememberedViewerPagerState!!.animateScrollToPage(actualPage - 1) }
                            // mutableCurrentPDFPage.intValue = actualPage - 1
                        }) {
                            Text(actualPage.toString(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // Text(ScreenLibraryCompanion.currentpg.toString())
            Text(ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value)

            // Redundant logging for debugging.
            val isAlreadyDownloaded = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
            val absolutePDFPathIfCached = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String
            Logger.logPDF({}, "url: $url, isAlreadyDownloaded: $isAlreadyDownloaded, absolutePDFPathIfCached: $absolutePDFPathIfCached")

            // Attempt to download the file, if not exists.
            // TODO: This LaunchedEffect implementation is causing lags when turning pages. Change the implementation.
            if (!isAlreadyDownloaded) {
                LaunchedEffect(Unit) { handlePdfDownload(ctx, url, lifecycleOwner) }  // --- ensuring that this block will only be ran once every time.
            }

            // Only render the PDF if the file exists and the PDF viewer composable is triggerred. TODO: remove?
            /*key (mutableTriggerPDFViewerRecomposition.value, true) {

            }*/

            if (File(absolutePDFPathIfCached).exists()) {
                // SOURCE: https://gist.github.com/grumpyshoe/cffd0bb54b8819e5e562e033445ec2f6
                val fileDescriptor = remember {
                    Logger.logPDF({}, "Remembering fileDescriptor...")
                    ParcelFileDescriptor.open(
                        File(absolutePDFPathIfCached),
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                }
                val mPdfRenderer = remember {
                    Logger.logPDF({}, "Remembering mPdfRenderer...")
                    PdfRenderer(fileDescriptor)
                }
                val mPdfPageCount = remember {
                    Logger.logPDF({}, "Remembering mPdfPageCount...")
                    mPdfRenderer.pageCount
                }

                // For synchronizing pagerState with scrollState without causing lags.
                // SOURCE: https://stackoverflow.com/a/78641547
                // TODO: DFW
                /*val firstVisibleItemIndex = remember {
                    mutableIntStateOf(0)
                }
                val firstVisibleItemScrollOffset = remember {
                    mutableIntStateOf(0)
                }*/

                // Updating the PDF page navigator.
                mutableTotalPDFPage.intValue = mPdfPageCount

                // Initiating the pager state.
                val initialPage = 0
                rememberedViewerPagerState = rememberPagerState(initialPage = initialPage) { mPdfPageCount }

                // For zooming images.
                val targetScale = 5.0f
                HorizontalPager(rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) {
                    val mPdfPage = mPdfRenderer.openPage(it)

                    // For synchronizing pagerState with scrollState without causing lags.
                    // SOURCE: https://stackoverflow.com/a/78641547
                    // TODO: DFW
                    /*val wasScrolled = remember { mutableStateOf(false) }
                    LaunchedEffect(firstVisibleItemIndex, firstVisibleItemScrollOffset) {
                        // apply global scroll state
                        wasScrolled.value = false
                        navigatorLazyListState!!.scrollToItem(firstVisibleItemIndex.intValue, firstVisibleItemScrollOffset.intValue)
                    }
                    LaunchedEffect(navigatorLazyListState!!.isScrollInProgress) {
                        if (navigatorLazyListState!!.isScrollInProgress) {
                            wasScrolled.value = true
                        } else if (wasScrolled.value) {
                            // write to global scroll state
                            firstVisibleItemIndex.intValue = navigatorLazyListState!!.firstVisibleItemIndex
                            firstVisibleItemScrollOffset.intValue = navigatorLazyListState!!.firstVisibleItemScrollOffset
                        }
                    }*/

                    // Updating the PDF page navigator state (again). TODO: remove?
                    // mutableCurrentPDFPage.intValue = rememberedViewerPagerState!!.currentPage
                    // TODO: These might be causing lags. Change the implementation?
                    /*LaunchedEffect(Unit) {
                        scope.launch { navigatorLazyListState!!.animateScrollToItem(mutableCurrentPDFPage.intValue) }
                    }*/
                    // mutableCurrentPDFPage.intValue = it

                    // TODO: Use the following implementation and make app preference key.
                    // SOURCE: https://stackoverflow.com/a/32327174
                    // ---
                    /**
                     * Beware of using this densityDpi / 72 - after I did it, it was causing OutOfMemory errors on newer high-DPI devices. So now, I simply multiplied my height and width by 2 to get a crisper image and it looks pretty good. –
                     * BlazeFast
                     * Commented Nov 11, 2016 at 21:10
                     * SOURCE: https://stackoverflow.com/a/32327174
                     */
                    /*val bitmap = Bitmap.createBitmap(
                        resources.displayMetrics.densityDpi * mCurrentPage.getWidth() / 72,
                        resources.displayMetrics.densityDpi * mCurrentPage.getHeight() / 72,
                        Bitmap.Config.ARGB_8888
                    )*/

                    // Create a new bitmap and render the page contents into it
                    val bitmap = Bitmap.createBitmap(
                        mPdfPage.width * 2,
                        mPdfPage.height * 2,
                        Bitmap.Config.ARGB_8888,
                    )
                    mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                    mPdfPage.close()

                    val zoomState = rememberZoomState()
                    Image(
                        bitmap.asImageBitmap(),
                        modifier = Modifier.zoomable(
                            zoomState,
                            onDoubleTap = { position -> zoomState.toggleScale(targetScale, position) },
                            scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
                        ).fillMaxSize().background(Color.White),
                        contentDescription = "PDF Page Bitmap Page $it",
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Displaying the PDF file.
            /*if (isAlreadyDownloaded) initWithFile(File(absolutePDFPathIfCached))
            else*/ // initWithUrl(url, headers, lifecycleScope, lifecycleOwner.lifecycle)
            // TODO: Make a self-created implementation to check if the PDF is already downloaded, then also all PDF downloaded files except the latest N. (For storage management.)

            // Displaying the PDF.
            /*key (mutableTriggerPDFViewerRecomposition.value, true) {
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
            }*/

            // Placebo.
            // Text("ew")
        }

    }

    @Composable
    private fun test_001() {
        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()

        /* Displaying the main PDF content. */
        Column (Modifier.fillMaxSize()) {
            val url = eBookUrl

            // The page navigator.
            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { /* Displays the custom page navigator (with typing)? */ }) {
                    Text((mutableCurrentPDFPage.intValue + 1).toString() + " / " + ScreenPDFViewerCompanion.mutableTotalPDFPage.intValue.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = navigatorLazyListState!!) {
                    val totalPDFPage = mutableTotalPDFPage.intValue
                    if (totalPDFPage > 0)
                        items(totalPDFPage) {
                            val actualPage = it + 1
                            TextButton(onClick = {
                                scope.launch { rememberedViewerPagerState!!.animateScrollToPage(actualPage - 1) }
                                // mutableCurrentPDFPage.intValue = actualPage - 1
                            }) {
                                Text(actualPage.toString(), textAlign = TextAlign.Center)
                            }
                        }
                }
            }

            // Text(ScreenLibraryCompanion.currentpg.toString())
            Text(ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value)

            // Redundant logging for debugging.
            // val isAlreadyDownloaded = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
            // val absolutePDFPathIfCached = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String
            // Logger.logPDF({}, "url: $url, isAlreadyDownloaded: $isAlreadyDownloaded, absolutePDFPathIfCached: $absolutePDFPathIfCached")

            // Attempt to download the file, if not exists.
            // TODO: This LaunchedEffect implementation is causing lags when turning pages. Change the implementation.
            /*if (!isAlreadyDownloaded) {
                LaunchedEffect(Unit) { handlePdfDownload(ctx, url, lifecycleOwner) }  // --- ensuring that this block will only be ran once every time.
            }*/

            // Only render the PDF if the file exists and the PDF viewer composable is triggerred. TODO: remove?
            /*key (mutableTriggerPDFViewerRecomposition.value, true) {

            }*/

            val abc = (GalleryCompanion.jsonRoot!!.getJSONArray("2024")[0] as JSONObject).getJSONArray("photos")
            // For synchronizing pagerState with scrollState without causing lags.
            // SOURCE: https://stackoverflow.com/a/78641547
            // TODO: DFW
            /*val firstVisibleItemIndex = remember {
                mutableIntStateOf(0)
            }
            val firstVisibleItemScrollOffset = remember {
                mutableIntStateOf(0)
            }*/

            // Updating the PDF page navigator.
            mutableTotalPDFPage.intValue = abc.length()

            // Initiating the pager state.
            val initialPage = 0
            rememberedViewerPagerState = rememberPagerState(initialPage = initialPage) { abc.length() }

            // For zooming images.
            val targetScale = 5.0f
            HorizontalPager(rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) {

                val zoomState = rememberZoomState()
                val id = (abc[it] as JSONObject).getString("id")
                mutableCurrentPDFPage.intValue = it
                AsyncImage(
                    StringFormatter.getGoogleDriveThumbnail(id, 600),
                    modifier = Modifier.zoomable(
                        zoomState,
                        onDoubleTap = { position -> zoomState.toggleScale(targetScale, position) },
                        scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
                    ).fillMaxSize().background(Color.White),
                    contentDescription = "PDF Page Bitmap Page $it",
                    contentScale = ContentScale.Fit
                )
            }

            // Displaying the PDF file.
            /*if (isAlreadyDownloaded) initWithFile(File(absolutePDFPathIfCached))
            else*/ // initWithUrl(url, headers, lifecycleScope, lifecycleOwner.lifecycle)
            // TODO: Make a self-created implementation to check if the PDF is already downloaded, then also all PDF downloaded files except the latest N. (For storage management.)

            // Displaying the PDF.
            /*key (mutableTriggerPDFViewerRecomposition.value, true) {
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
            }*/

            // Placebo.
            // Text("ew")
        }

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun test_002(scope: CoroutineScope, absolutePDFPathIfCached: String, pageNumber: Int): ImageBitmap = scope.async {
        // SOURCE: https://gist.github.com/grumpyshoe/cffd0bb54b8819e5e562e033445ec2f6
        Logger.logPDF({}, "Remembering fileDescriptor...")
        val fileDescriptor = ParcelFileDescriptor.open(
            File(absolutePDFPathIfCached),
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        val mPdfRenderer = PdfRenderer(fileDescriptor)
        // val mPdfPageCount = mPdfRenderer.pageCount

        // Updating the PDF page navigator.
        // mutableTotalPDFPage.intValue = mPdfPageCount

        // Opening the desired PDF page.
        val mPdfPage = mPdfRenderer.openPage(pageNumber)

        // Create a new bitmap and render the page contents into it
        val bitmap = Bitmap.createBitmap(
            mPdfPage.width * 2,
            mPdfPage.height * 2,
            Bitmap.Config.ARGB_8888,
        )
        mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
        mPdfPage.close()

        // Return the image bitmap.
        bitmap.asImageBitmap()
    }.await()

    private suspend fun test_002a(scope: CoroutineScope, absolutePDFPathIfCached: String, pageNumber: Int): ImageBitmap = scope.async {
        // SOURCE: https://gist.github.com/grumpyshoe/cffd0bb54b8819e5e562e033445ec2f6
        Logger.logPDF({}, "Remembering fileDescriptor...")
        val fileDescriptor = ParcelFileDescriptor.open(
            File(absolutePDFPathIfCached),
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        val mPdfRenderer = PdfRenderer(fileDescriptor)
        // val mPdfPageCount = mPdfRenderer.pageCount

        // Updating the PDF page navigator.
        // mutableTotalPDFPage.intValue = mPdfPageCount

        // Opening the desired PDF page.
        val mPdfPage = mPdfRenderer.openPage(pageNumber)

        // Create a new bitmap and render the page contents into it
        val bitmap = Bitmap.createBitmap(
            mPdfPage.width * 2,
            mPdfPage.height * 2,
            Bitmap.Config.ARGB_8888,
        )
        mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
        mPdfPage.close()

        // Return the image bitmap.
        bitmap.asImageBitmap()
    }.await()

    @SuppressLint("CoroutineCreationDuringComposition")
    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    @Composable
    private fun test_003() {
        val ctx = LocalContext.current
        val scope = rememberCoroutineScope()
        val lifecycleOwner = LocalLifecycleOwner.current
        val lifecycleScope = lifecycleOwner.lifecycleScope
        val pdfRendererViewInstance = ScreenPDFViewerCompanion.pdfRendererViewInstance!!

        /* Displaying the main PDF content. */
        Column (Modifier.fillMaxSize()) {
            val url = eBookUrl

            // The page navigator.
            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { /* Displays the custom page navigator (with typing)? */ }) {
                    Text((mutableCurrentPDFPage.intValue + 1).toString() + " / " + ScreenPDFViewerCompanion.mutableTotalPDFPage.intValue.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = navigatorLazyListState!!) {
                    val totalPDFPage = mutableTotalPDFPage.intValue
                    if (totalPDFPage > 0)
                        items(totalPDFPage) {
                            val actualPage = it + 1
                            TextButton(onClick = {
                                scope.launch { rememberedViewerPagerState!!.animateScrollToPage(actualPage - 1) }
                                // mutableCurrentPDFPage.intValue = actualPage - 1
                            }) {
                                Text(actualPage.toString(), textAlign = TextAlign.Center)
                            }
                        }
                }
            }

            // Text(ScreenLibraryCompanion.currentpg.toString())
            Text(ScreenPDFViewerCompanion.mutableCallbackStatusMessage.value)

            // Redundant logging for debugging.
            val isAlreadyDownloaded = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
            val absolutePDFPathIfCached = LocalStorage(ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String
            Logger.logPDF({}, "url: $url, isAlreadyDownloaded: $isAlreadyDownloaded, absolutePDFPathIfCached: $absolutePDFPathIfCached")

            // Attempt to download the file, if not exists.
            // TODO: This LaunchedEffect implementation is causing lags when turning pages. Change the implementation.
            /*if (!isAlreadyDownloaded) {
                LaunchedEffect(Unit) { handlePdfDownload(ctx, url, lifecycleOwner) }  // --- ensuring that this block will only be ran once every time.
            }*/

            // Only render the PDF if the file exists and the PDF viewer composable is triggerred. TODO: remove?
            /*key (mutableTriggerPDFViewerRecomposition.value, true) {

            }*/

            if (File(absolutePDFPathIfCached).exists()) {
                // SOURCE: https://gist.github.com/grumpyshoe/cffd0bb54b8819e5e562e033445ec2f6
                val fileDescriptor = remember {
                    Logger.logPDF({}, "Remembering fileDescriptor...")
                    ParcelFileDescriptor.open(
                        File(absolutePDFPathIfCached),
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )
                }
                val mPdfRenderer = remember {
                    Logger.logPDF({}, "Remembering mPdfRenderer...")
                    PdfRenderer(fileDescriptor)
                }
                val mPdfPageCount = remember {
                    Logger.logPDF({}, "Remembering mPdfPageCount...")
                    mPdfRenderer.pageCount
                }

                // For synchronizing pagerState with scrollState without causing lags.
                // SOURCE: https://stackoverflow.com/a/78641547
                // TODO: DFW
                /*val firstVisibleItemIndex = remember {
                    mutableIntStateOf(0)
                }
                val firstVisibleItemScrollOffset = remember {
                    mutableIntStateOf(0)
                }*/

                // Updating the PDF page navigator.
                mutableTotalPDFPage.intValue = mPdfPageCount

                // Initiating the pager state.
                val initialPage = 0
                rememberedViewerPagerState = rememberPagerState(initialPage = initialPage) { mPdfPageCount }

                // For zooming images.
                val targetScale = 5.0f
                HorizontalPager(rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) {
                    // val mPdfPage = mPdfRenderer.openPage(it)

                    // For synchronizing pagerState with scrollState without causing lags.
                    // SOURCE: https://stackoverflow.com/a/78641547
                    // TODO: DFW
                    /*val wasScrolled = remember { mutableStateOf(false) }
                    LaunchedEffect(firstVisibleItemIndex, firstVisibleItemScrollOffset) {
                        // apply global scroll state
                        wasScrolled.value = false
                        navigatorLazyListState!!.scrollToItem(firstVisibleItemIndex.intValue, firstVisibleItemScrollOffset.intValue)
                    }
                    LaunchedEffect(navigatorLazyListState!!.isScrollInProgress) {
                        if (navigatorLazyListState!!.isScrollInProgress) {
                            wasScrolled.value = true
                        } else if (wasScrolled.value) {
                            // write to global scroll state
                            firstVisibleItemIndex.intValue = navigatorLazyListState!!.firstVisibleItemIndex
                            firstVisibleItemScrollOffset.intValue = navigatorLazyListState!!.firstVisibleItemScrollOffset
                        }
                    }*/

                    // Updating the PDF page navigator state (again). TODO: remove?
                    // mutableCurrentPDFPage.intValue = rememberedViewerPagerState!!.currentPage
                    // TODO: These might be causing lags. Change the implementation?
                    /*LaunchedEffect(Unit) {
                        scope.launch { navigatorLazyListState!!.animateScrollToItem(mutableCurrentPDFPage.intValue) }
                    }*/
                    // mutableCurrentPDFPage.intValue = it

                    // TODO: Use the following implementation and make app preference key.
                    // SOURCE: https://stackoverflow.com/a/32327174
                    // ---
                    /**
                     * Beware of using this densityDpi / 72 - after I did it, it was causing OutOfMemory errors on newer high-DPI devices. So now, I simply multiplied my height and width by 2 to get a crisper image and it looks pretty good. –
                     * BlazeFast
                     * Commented Nov 11, 2016 at 21:10
                     * SOURCE: https://stackoverflow.com/a/32327174
                     */
                    /*val bitmap = Bitmap.createBitmap(
                        resources.displayMetrics.densityDpi * mCurrentPage.getWidth() / 72,
                        resources.displayMetrics.densityDpi * mCurrentPage.getHeight() / 72,
                        Bitmap.Config.ARGB_8888
                    )*/

                    // Create a new bitmap and render the page contents into it
                    /*val bitmap = Bitmap.createBitmap(
                        mPdfPage.width * 2,
                        mPdfPage.height * 2,
                        Bitmap.Config.ARGB_8888,
                    )
                    mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                    mPdfPage.close()*/

                    /*var outside: ImageBitmap? = null
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            outside = test_002(absolutePDFPathIfCached, it)
                        }
                    }*/

                    val pic: MutableState<ImageBitmap> = remember { mutableStateOf(ImageBitmap(1, 1)) }
                    LaunchedEffect(Unit) {
                        pic.value = test_002(scope, absolutePDFPathIfCached, it)
                    }

                    val zoomState = rememberZoomState()
                    AsyncImage(
                        pic,
                        modifier = Modifier.zoomable(
                            zoomState,
                            onDoubleTap = { position -> zoomState.toggleScale(targetScale, position) },
                            scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
                        ).fillMaxSize().background(Color.White),
                        contentDescription = "PDF Page Bitmap Page $it",
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Displaying the PDF file.
            /*if (isAlreadyDownloaded) initWithFile(File(absolutePDFPathIfCached))
            else*/ // initWithUrl(url, headers, lifecycleScope, lifecycleOwner.lifecycle)
            // TODO: Make a self-created implementation to check if the PDF is already downloaded, then also all PDF downloaded files except the latest N. (For storage management.)

            // Displaying the PDF.
            /*key (mutableTriggerPDFViewerRecomposition.value, true) {
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
            }*/

            // Placebo.
            // Text("ew")
        }

    }

    @Composable
    private fun test_004() {
        val filePath = "/data/data/org.gkisalatiga.plus/app_Downloads/1730456953302.pdf"
        val lifecycleOwner = LocalLifecycleOwner.current
        val scope = rememberCoroutineScope()

        /* Displaying the main PDF content. */
        Column (Modifier.fillMaxSize()) {
            // The page navigator.
            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { /* Displays the custom page navigator (with typing)? */ }) {
                    Text((mutableCurrentPDFPage.intValue + 1).toString() + " / " + ScreenPDFViewerCompanion.mutableTotalPDFPage.intValue.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = navigatorLazyListState!!) {
                    val totalPDFPage = mutableTotalPDFPage.intValue
                    if (totalPDFPage > 0)
                        items(totalPDFPage) {
                            val actualPage = it + 1
                            TextButton(onClick = {
                                scope.launch { rememberedViewerPagerState!!.animateScrollToPage(actualPage - 1) }
                                // mutableCurrentPDFPage.intValue = actualPage - 1
                            }) {
                                Text(actualPage.toString(), textAlign = TextAlign.Center)
                            }
                        }
                }
            }

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

                currentFilePdfRenderer.value = pdfRendererViewModel.getPdfRenderer(filePath)
                mutableTriggerPDFViewerRecomposition.value = !mutableTriggerPDFViewerRecomposition.value
                mutablePdfUiPageCount.intValue = currentFilePdfRenderer.value!!.pageCount
                mutableTotalPDFPage.intValue = currentFilePdfRenderer.value!!.pageCount
            }

            // Attempt to download the file, if not exists.
            // TODO: This LaunchedEffect implementation is causing lags when turning pages. Change the implementation.
            /*if (!isAlreadyDownloaded) {
                LaunchedEffect(Unit) { handlePdfDownload(ctx, url, lifecycleOwner) }  // --- ensuring that this block will only be ran once every time.
            }*/

            // Only render the PDF if the file exists and the PDF viewer composable is triggerred. TODO: remove?
            key (mutableTriggerPDFViewerRecomposition.value, currentFilePdfRenderer.value) {
                // Text(mutablePdfUiEventMessage.value)
                // Text("Page count: ${mutablePdfUiPageCount.intValue}")
                if (rememberedViewerPagerState != null) Logger.logPDF({}, "mutablePdfUiPageCount.intValue: ${mutablePdfUiPageCount.intValue}, mutableTotalPDFPage.intValue: ${mutableTotalPDFPage.intValue}, rememberedViewerPagerState!!.currentPage: ${rememberedViewerPagerState!!.currentPage}, mutableCurrentPDFPage.intValue: ${mutableCurrentPDFPage.intValue}")

                // Initiating the pager state.
                val initialPage = 0
                rememberedViewerPagerState = rememberPagerState(initialPage = initialPage) { mutablePdfUiPageCount.intValue }

                // For zooming images.
                val targetScale = 5.0f
                HorizontalPager(rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) { pagerPage ->
                    // Updating the state of the currently selected PDF page.
                    // mutableCurrentPDFPage.intValue = pagerPage
                    mutableCurrentPDFPage.intValue = rememberedViewerPagerState!!.currentPage

                    // Request PDF page rendering.
                    LaunchedEffect(Unit) {
                        Logger.logTest({}, "pagerPage: $pagerPage, pagerPage+1: ${pagerPage + 1}", LoggerType.WARNING)
                        pdfRendererViewModel.loadPdfPage(currentFilePdfRenderer.value, pagerPage, 2).observe(lifecycleOwner) {
                        // pdfRendererViewModel.renderPdfPage(filePath, pagerPage + 1, 2).observe(lifecycleOwner) {
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

                    // Text(mutablePdfUiEventMessage.value)
                    /*AsyncImage(
                        StringFormatter.getGoogleDriveThumbnail(mutablePdfUiEventMessage.value), ""
                    )*/

                    key(mutableTriggerPDFPageRecomposition.value) {
                        // if (mutableBitmapList.size >= pagerPage && mutableBitmapList.size != 0) {
                        if (mutableBitmapMap.containsKey(pagerPage) && mutableBitmapMap[pagerPage] != null && mutableBitmapMap[pagerPage]!!::class == Bitmap::class) {
                            // PdfPage(mutableBitmapList[pagerPage])
                            PdfPage(mutableBitmapMap[pagerPage]!!)
                        }
                    }

                    /*AsyncImage(
                        pic,
                        modifier = Modifier.zoomable(
                            zoomState,
                            onDoubleTap = { position -> zoomState.toggleScale(targetScale, position) },
                            scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
                        ).fillMaxSize().background(Color.White),
                        contentDescription = "PDF Page Bitmap Page $it",
                        contentScale = ContentScale.Fit
                    )*/
                }
            }

            /*// Initiating the pager state.
            val initialPage = 0
            rememberedViewerPagerState = rememberPagerState(initialPage = initialPage) { mPdfPageCount }

            // For zooming images.
            val targetScale = 5.0f
            HorizontalPager(rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) {
                // val mPdfPage = mPdfRenderer.openPage(it)

                // For synchronizing pagerState with scrollState without causing lags.
                // SOURCE: https://stackoverflow.com/a/78641547
                // TODO: DFW
                /*val wasScrolled = remember { mutableStateOf(false) }
                LaunchedEffect(firstVisibleItemIndex, firstVisibleItemScrollOffset) {
                    // apply global scroll state
                    wasScrolled.value = false
                    navigatorLazyListState!!.scrollToItem(firstVisibleItemIndex.intValue, firstVisibleItemScrollOffset.intValue)
                }
                LaunchedEffect(navigatorLazyListState!!.isScrollInProgress) {
                    if (navigatorLazyListState!!.isScrollInProgress) {
                        wasScrolled.value = true
                    } else if (wasScrolled.value) {
                        // write to global scroll state
                        firstVisibleItemIndex.intValue = navigatorLazyListState!!.firstVisibleItemIndex
                        firstVisibleItemScrollOffset.intValue = navigatorLazyListState!!.firstVisibleItemScrollOffset
                    }
                }*/

                // Updating the PDF page navigator state (again). TODO: remove?
                // mutableCurrentPDFPage.intValue = rememberedViewerPagerState!!.currentPage
                // TODO: These might be causing lags. Change the implementation?
                /*LaunchedEffect(Unit) {
                    scope.launch { navigatorLazyListState!!.animateScrollToItem(mutableCurrentPDFPage.intValue) }
                }*/
                // mutableCurrentPDFPage.intValue = it

                // TODO: Use the following implementation and make app preference key.
                // SOURCE: https://stackoverflow.com/a/32327174
                // ---
                /**
                 * Beware of using this densityDpi / 72 - after I did it, it was causing OutOfMemory errors on newer high-DPI devices. So now, I simply multiplied my height and width by 2 to get a crisper image and it looks pretty good. –
                 * BlazeFast
                 * Commented Nov 11, 2016 at 21:10
                 * SOURCE: https://stackoverflow.com/a/32327174
                 */
                /*val bitmap = Bitmap.createBitmap(
                    resources.displayMetrics.densityDpi * mCurrentPage.getWidth() / 72,
                    resources.displayMetrics.densityDpi * mCurrentPage.getHeight() / 72,
                    Bitmap.Config.ARGB_8888
                )*/

                // Create a new bitmap and render the page contents into it
                /*val bitmap = Bitmap.createBitmap(
                    mPdfPage.width * 2,
                    mPdfPage.height * 2,
                    Bitmap.Config.ARGB_8888,
                )
                mPdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                mPdfPage.close()*/

                /*var outside: ImageBitmap? = null
                LaunchedEffect(Unit) {
                    withContext(Dispatchers.IO) {
                        outside = test_002(absolutePDFPathIfCached, it)
                    }
                }*/

                val pic: MutableState<ImageBitmap> = remember { mutableStateOf(ImageBitmap(1, 1)) }
                LaunchedEffect(Unit) {
                    pic.value = test_002(scope, absolutePDFPathIfCached, it)
                }

                val zoomState = rememberZoomState()
                AsyncImage(
                    pic,
                    modifier = Modifier.zoomable(
                        zoomState,
                        onDoubleTap = { position -> zoomState.toggleScale(targetScale, position) },
                        scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
                    ).fillMaxSize().background(Color.White),
                    contentDescription = "PDF Page Bitmap Page $it",
                    contentScale = ContentScale.Fit
                )
            }*/

            // Displaying the PDF file.
            /*if (isAlreadyDownloaded) initWithFile(File(absolutePDFPathIfCached))
            else*/ // initWithUrl(url, headers, lifecycleScope, lifecycleOwner.lifecycle)
            // TODO: Make a self-created implementation to check if the PDF is already downloaded, then also all PDF downloaded files except the latest N. (For storage management.)

            // Displaying the PDF.
            /*key (mutableTriggerPDFViewerRecomposition.value, true) {
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
            }*/

            // Placebo.
            // Text("ew")
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
        internal var mutableBitmapList: MutableList<Bitmap> = mutableListOf()
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
         * This function initializes the status callback handler of the PDF viewer.
         * It returns unit and takes no argumen.
         */
        fun initPDFViewerCallbackHandler(ctx: Context) {
            // TODO: Consider removing.
            /*
            val pdfRendererViewInstance = pdfRendererViewInstance!!
            pdfRendererViewInstance.statusListener = object: PdfRendererView.StatusCallBack {
                override fun onPageChanged(currentPage: Int, totalPage: Int) {
                    Logger.logRapidTest({}, "onPageChanged -> currentPage: $currentPage, totalPage: $totalPage", LoggerType.VERBOSE)
                    mutableCurrentPDFPage.intValue = currentPage
                    mutableTotalPDFPage.intValue = totalPage
                }

                override fun onError(error: Throwable) {
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
                }
            }*/
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

            /* Resetting the state of the current PDF viewer due to the existence of a new PDF file. */
            mutableBitmapMap = mutableMapOf<Int, Bitmap>()
            mutablePdfUiPageCount.intValue = 0
        }

        /* The pager state for the PDF viewer. */
        var rememberedViewerPagerState: PagerState? = null

        /* This view instance ensures the PDF file can be viewed. */
        @SuppressLint("StaticFieldLeak")
        var pdfRendererViewInstance: PdfRendererView? = null
    }
}