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
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.PdfPage
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.CoroutineFileDownload
import org.gkisalatiga.plus.lib.FileDownloadEvent
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.model.PdfPageUiEvent
import org.gkisalatiga.plus.model.PdfViewModel
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.eBookUrl
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableBitmapMap
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutablePdfUiCurrentPage
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutablePdfUiEventMessage
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutablePdfUiTotalPageCount
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableTriggerPDFPageRecomposition
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutableTriggerPDFViewerRecomposition
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.navigatorLazyListState
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.rememberedViewerPagerState
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.txtLoadingPercentageAnimatable
import org.gkisalatiga.plus.services.InternalFileManager
import org.json.JSONObject


class ScreenPDFViewer(private val current: ActivityData) : ComponentActivity() {

    // The view model for downloading files with progress.
    private val downloadWithProgressViewModel = CoroutineFileDownload()

    // The view model for rendering the PDF files.
    private val pdfRendererViewModel = PdfViewModel(current.ctx)

    // When the value of this mutable variable is not null, it means the PDF file is loaded already.
    private var currentFilePdfRenderer: MutableState<PdfRenderer?> = mutableStateOf(null)

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        val scope = rememberCoroutineScope()

        Box (Modifier.fillMaxSize()) {
            // Let the top and bottom bars be below the scrim.
            Scaffold (topBar = { getTopBar() }) {
                Column (
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                        .fillMaxSize()
                ) { getMainContent() }
            }

            // Show some error alert dialogs.
            fun alertDialogDismiss() {
                ScreenPDFViewerCompanion.mutableShowAlertDialog.value = false
                AppNavigation.popBack()
            }
            val alertDialogTitle = stringResource(R.string.screen_pdfviewer_error_dialog_title)
            val alertDialogBtnString = stringResource(R.string.screen_pdfviewer_error_dialog_btn_string)
            if (ScreenPDFViewerCompanion.mutableShowAlertDialog.value) {
                AlertDialog(
                    onDismissRequest = { alertDialogDismiss() },
                    title = { Text(alertDialogTitle) },
                    text = { Text(ScreenPDFViewerCompanion.txtAlertDialogSubtitle.value) },
                    confirmButton = {
                        Button(onClick = { alertDialogDismiss() }) {
                            Text(alertDialogBtnString, color = Color.White)
                        }
                    }
                )
            }

            // Show some dialogs displayed during downloads/loadings.
            fun loadingDialogDismiss() {
                downloadWithProgressViewModel.cancelDownload()
                ScreenPDFViewerCompanion.mutableShowLoadingDialog.value = false
                AppNavigation.popBack()
            }
            val loadingDialogTitle = stringResource(R.string.screen_pdfviewer_loading_dialog_title)
            val loadingDialogSubtitle = stringResource(R.string.screen_pdfviewer_loading_dialog_subtitle)
                .replace("%%%", ScreenPDFViewerCompanion.eBookTitle)
            val loadingDialogBtnString = stringResource(R.string.screen_pdfviewer_loading_dialog_btn_string)
            if (ScreenPDFViewerCompanion.mutableShowLoadingDialog.value) {
                AlertDialog(
                    onDismissRequest = { loadingDialogDismiss() },
                    title = { Text(loadingDialogTitle) },
                    text = {
                        Column (horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(loadingDialogSubtitle)
                            Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                LinearProgressIndicator(
                                    progress = { txtLoadingPercentageAnimatable.value },
                                    modifier = Modifier.fillMaxWidth().weight(5.0f),
                                )
                                Text("${ScreenPDFViewerCompanion.mutablePdfDownloadPercentage.intValue}%",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1.0f),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { loadingDialogDismiss() }) {
                            Text(loadingDialogBtnString, color = Color.White)
                        }
                    }
                )
            }

            // Animating the progress bar during downloads.
            LaunchedEffect(key1 = ScreenPDFViewerCompanion.mutablePdfDownloadPercentage.intValue) {
                val floatPercentage = ScreenPDFViewerCompanion.mutablePdfDownloadPercentage.intValue.toFloat() / 100
                txtLoadingPercentageAnimatable.animateTo(targetValue = floatPercentage, animationSpec = tween(durationMillis = 75, easing = { FastOutSlowInEasing.transform(it) /*OvershootInterpolator(2f).getInterpolation(it)*/ }))
            }

            // Show the PDF information dialog.
            val infoDialogTitle = stringResource(R.string.screen_pdfviewer_info_dialog_title)
            val infoDialogContentNameDocTitle = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_title)
            val infoDialogContentNameDocAuthor = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_author)
            val infoDialogContentNameDocPublisher = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_publisher)
            val infoDialogContentNameDocPublisherLoc = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_publisher_loc)
            val infoDialogContentNameDocYear = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_year)
            val infoDialogContentNameDocSource = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_source)
            val infoDialogContentNameDocLocalPath = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_local_path)
            val infoDialogContentNameDocSize = stringResource(R.string.screen_pdfviewer_info_dialog_content_name_doc_size)
            if (ScreenPDFViewerCompanion.mutableShowPdfInfoDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        ScreenPDFViewerCompanion.mutableShowPdfInfoDialog.value = false
                    },
                    title = { Text(infoDialogTitle) },
                    text = {
                        Column (Modifier.fillMaxWidth().height(400.dp).verticalScroll(rememberScrollState())) {
                            Text(infoDialogContentNameDocTitle, fontWeight = FontWeight.Bold)
                            Text(ScreenPDFViewerCompanion.eBookTitle)
                            Spacer(Modifier.fillMaxWidth().height(5.dp))

                            Text(infoDialogContentNameDocAuthor, fontWeight = FontWeight.Bold)
                            Text(ScreenPDFViewerCompanion.eBookAuthor)
                            Spacer(Modifier.fillMaxWidth().height(5.dp))

                            Text(infoDialogContentNameDocPublisher, fontWeight = FontWeight.Bold)
                            Text(ScreenPDFViewerCompanion.eBookPublisher)
                            Spacer(Modifier.fillMaxWidth().height(5.dp))

                            Text(infoDialogContentNameDocPublisherLoc, fontWeight = FontWeight.Bold)
                            Text(ScreenPDFViewerCompanion.eBookPublisherLoc)
                            Spacer(Modifier.fillMaxWidth().height(5.dp))

                            Text(infoDialogContentNameDocYear, fontWeight = FontWeight.Bold)
                            Text(ScreenPDFViewerCompanion.eBookYear)
                            Spacer(Modifier.fillMaxWidth().height(5.dp))

                            Text(infoDialogContentNameDocSource, fontWeight = FontWeight.Bold)
                            Text(buildAnnotatedString {
                                withLink(
                                    LinkAnnotation.Url(ScreenPDFViewerCompanion.eBookSource, TextLinkStyles(style = SpanStyle(color = Color.Blue)))
                                ) { append(ScreenPDFViewerCompanion.eBookSource) }
                            })
                            Spacer(Modifier.fillMaxWidth().height(5.dp))

                            if (GlobalCompanion.DEBUG_SHOW_INFO_PDF_LOCAL_PATH_INFO) {
                                val pdfLocalPath = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, eBookUrl) as String
                                Text(infoDialogContentNameDocLocalPath, fontWeight = FontWeight.Bold)
                                Text(pdfLocalPath)
                                Spacer(Modifier.fillMaxWidth().height(5.dp))
                            }

                            Text(infoDialogContentNameDocSize, fontWeight = FontWeight.Bold)
                            Text(ScreenPDFViewerCompanion.eBookSize)
                            Spacer(Modifier.fillMaxWidth().height(5.dp))
                        }
                    },
                    confirmButton = {
                        Button(onClick = { ScreenPDFViewerCompanion.mutableShowPdfInfoDialog.value = false }) {
                            Text("OK", color = Color.White)
                        }
                    }
                )
            }

            // Show the PDF page navigator dialog.
            val minimumPdfPage = 1
            val maximumPdfPage = mutablePdfUiTotalPageCount.intValue
            val pageStringLocalized = stringResource(R.string.screen_pdfviewer_nav_page_string)
            val navigatorDialogTitle = stringResource(R.string.screen_pdfviewer_nav_title)
            if (ScreenPDFViewerCompanion.mutableShowPdfNavigatorDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        ScreenPDFViewerCompanion.mutableShowPdfNavigatorDialog.value = false
                    },
                    title = { Text(navigatorDialogTitle) },
                    text = {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = ScreenPDFViewerCompanion.mutableCurrentFieldPageNumberValue.value,
                            onValueChange = {
                                val inputStr = it.replace(".", "").replace(",", "").replace(" ", "").replace("-", "")
                                if (inputStr.isNotBlank() && inputStr.isNotEmpty() && inputStr != "") {
                                    if (inputStr.toInt() < minimumPdfPage) ScreenPDFViewerCompanion.mutableCurrentFieldPageNumberValue.value = minimumPdfPage.toString()
                                    else if (inputStr.toInt() > maximumPdfPage) ScreenPDFViewerCompanion.mutableCurrentFieldPageNumberValue.value = maximumPdfPage.toString()
                                    else ScreenPDFViewerCompanion.mutableCurrentFieldPageNumberValue.value = inputStr
                                } else {
                                    ScreenPDFViewerCompanion.mutableCurrentFieldPageNumberValue.value = inputStr
                                }
                            },
                            label = { Text("$pageStringLocalized 1-$maximumPdfPage") },
                            enabled = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            val inputStr = ScreenPDFViewerCompanion.mutableCurrentFieldPageNumberValue.value
                            if (inputStr.isNotBlank() && inputStr.isNotEmpty() && inputStr != "") {
                                scope.launch { rememberedViewerPagerState!!.animateScrollToPage(inputStr.toInt() - 1) }
                                ScreenPDFViewerCompanion.mutableShowPdfNavigatorDialog.value = false
                            }
                        }) { Text("OK", color = Color.White) }
                    }
                )
            }

        }

        // Scroll the horizontal navigator to the respective position when the pager is scrolled against.
        LaunchedEffect(mutablePdfUiCurrentPage.intValue) {
            scope.launch {
                navigatorLazyListState!!.animateScrollToItem(rememberedViewerPagerState!!.currentPage)
            }
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }
    }

    @Composable
    @SuppressLint("ComposableNaming")
    private fun getMainContent() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val scope = rememberCoroutineScope()

        /* Displaying the main PDF content. */
        Column (Modifier.fillMaxSize()) {

            // The page navigator.
            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { ScreenPDFViewerCompanion.mutableCurrentFieldPageNumberValue.value = (mutablePdfUiCurrentPage.intValue + 1).toString(); ScreenPDFViewerCompanion.mutableShowPdfNavigatorDialog.value = true }) {
                    Text((mutablePdfUiCurrentPage.intValue + 1).toString() + " / " + mutablePdfUiTotalPageCount.intValue.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = navigatorLazyListState!!) {
                    val totalPDFPage = mutablePdfUiTotalPageCount.intValue
                    if (totalPDFPage > 0) items(totalPDFPage) {
                        val actualPage = it + 1
                        TextButton(onClick = { scope.launch { rememberedViewerPagerState!!.animateScrollToPage(actualPage - 1) } }) {
                            Text(actualPage.toString(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            // Redundant logging for debugging.
            val url = eBookUrl
            val isAlreadyDownloaded = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
            val absolutePDFPathIfCached = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String
            Logger.logPDF({}, "url: $url, isAlreadyDownloaded: $isAlreadyDownloaded, absolutePDFPathIfCached: $absolutePDFPathIfCached")

            // Ensures that this block will only be ran once every other recomposition.
            LaunchedEffect(Unit) {
                // Attempt to download the file, if not exists.
                if (!isAlreadyDownloaded) {
                    Logger.logPDF({}, "Downloading the PDF file: $url")
                    handlePdfDownload(url, lifecycleOwner)

                } else {
                    // Load the file directly if it exists.
                    currentFilePdfRenderer.value = pdfRendererViewModel.initPdfRenderer(absolutePDFPathIfCached)
                    mutablePdfUiTotalPageCount.intValue = currentFilePdfRenderer.value!!.pageCount

                    // Trigger recompositioning.
                    mutableTriggerPDFViewerRecomposition.value = !mutableTriggerPDFViewerRecomposition.value
                }
            }

            // Only render the PDF if the file exists and the PDF viewer composable is triggerred.
            key (mutableTriggerPDFViewerRecomposition.value, currentFilePdfRenderer.value) {
                if (rememberedViewerPagerState != null) Logger.logPDF({}, "mutablePdfUiPageCount.intValue: ${mutablePdfUiTotalPageCount.intValue}, mutableTotalPDFPage.intValue: ${mutablePdfUiTotalPageCount.intValue}, rememberedViewerPagerState!!.currentPage: ${rememberedViewerPagerState!!.currentPage}, mutableCurrentPDFPage.intValue: ${mutablePdfUiCurrentPage.intValue}")

                // Mark this PDF file as "just accessed" so that it won't be scheduled for deletion soon.
                LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_LAST_ACCESS_MILLIS, System.currentTimeMillis(), LocalStorageDataTypes.LONG, url)

                // Initiating the pager state.
                val initialPage = 0
                rememberedViewerPagerState = rememberPagerState(initialPage = initialPage) { mutablePdfUiTotalPageCount.intValue }

                HorizontalPager(rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) { pagerPage ->
                    // Updating the state of the currently selected PDF page.
                    mutablePdfUiCurrentPage.intValue = rememberedViewerPagerState!!.currentPage

                    // Request PDF page rendering.
                    LaunchedEffect(Unit) {
                        Logger.logTest({}, "pagerPage: $pagerPage, pagerPage+1: ${pagerPage + 1}", LoggerType.WARNING)
                        pdfRendererViewModel.loadPdfPage(pagerPage).observe(lifecycleOwner) {
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
                                        // Storing the bitmap in the respective page's mapping.
                                        Logger.logPDF({}, it.message, LoggerType.INFO)
                                        mutableBitmapMap[it.pageNumber] = it.pageBitmap

                                        Logger.logDump({}, mutableBitmapMap.toString(), LoggerType.VERBOSE)
                                    }

                                    // Triggering the recomposition, only if we are at the right page.
                                    if (mutablePdfUiCurrentPage.intValue == it.pageNumber) mutableTriggerPDFPageRecomposition.value = !mutableTriggerPDFPageRecomposition.value
                                }
                            }
                        }
                    }

                    key(mutableTriggerPDFPageRecomposition.value) {
                        if (mutableBitmapMap.containsKey(pagerPage) && mutableBitmapMap[pagerPage] != null && mutableBitmapMap[pagerPage]!!::class == Bitmap::class) {
                            // PdfPage(mutableBitmapMap[pagerPage]!!, current.colors.mainZoomableBoxBackgroundColor)
                            PdfPage(mutableBitmapMap[pagerPage]!!, Color.White)
                        }
                    }

                }  // --- end HorizontalPager.
            }  // --- end key.
        }  // --- end Column.
    }  // --- end getMainContent().

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("ComposableNaming")
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
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
                IconButton(onClick = { ScreenPDFViewerCompanion.mutableShowPdfInfoDialog.value = true }) {
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
    private fun handlePdfDownload(url: String, lifecycleOwner: LifecycleOwner) {
        val targetDownloadDir = InternalFileManager(current.ctx).PDF_POOL_PDF_FILE_CREATOR
        val targetFilename = System.currentTimeMillis()

        // Display the loading dialog during download.
        val message = "Downloading $targetFilename.pdf into ${targetDownloadDir.absolutePath}"
        ScreenPDFViewerCompanion.showLoadingDialog(message, 0)

        // Do the downloading.
        downloadWithProgressViewModel.downloadFile(url, "$targetFilename.pdf", targetDownloadDir).observe(lifecycleOwner) {
            when (it) {
                is FileDownloadEvent.Progress -> {
                    ScreenPDFViewerCompanion.showLoadingDialog(message, it.percentage)
                }

                is FileDownloadEvent.Success -> {
                    ScreenPDFViewerCompanion.mutablePdfDownloadStatusMessage.value = "Success! Downloaded to: ${it.downloadedFilePath}"

                    val outputPath = it.downloadedFilePath

                    // Saving the download PDF file's metadata.
                    val jsonMetadata = JSONObject()
                        .put("title", ScreenPDFViewerCompanion.eBookTitle)
                        .put("author", ScreenPDFViewerCompanion.eBookAuthor)
                        .put("publisher", ScreenPDFViewerCompanion.eBookPublisher)
                        .put("publisher-loc", ScreenPDFViewerCompanion.eBookPublisherLoc)
                        .put("year", ScreenPDFViewerCompanion.eBookYear)
                        .put("thumbnail", ScreenPDFViewerCompanion.eBookThumbnail)
                        .put("download-url", eBookUrl)
                        .put("source", ScreenPDFViewerCompanion.eBookSource)
                        .put("size", ScreenPDFViewerCompanion.eBookSize)
                        .toString(0)

                    // Ensure that we don't download this PDF file again in the future.
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, true, LocalStorageDataTypes.BOOLEAN, url)
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_PDF_FILE_LOCATION, outputPath, LocalStorageDataTypes.STRING, url)
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_LAST_DOWNLOAD_MILLIS, System.currentTimeMillis(), LocalStorageDataTypes.LONG, url)
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_PDF_METADATA, jsonMetadata, LocalStorageDataTypes.STRING, url)

                    // Only if this PDF file has not been made favorite before.
                    if (!LocalStorage(current.ctx).hasKey(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_MARKED_AS_FAVORITE, url))
                        LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_MARKED_AS_FAVORITE, false, LocalStorageDataTypes.BOOLEAN, url)

                    // Load the file directly if it exists.
                    currentFilePdfRenderer.value = pdfRendererViewModel.initPdfRenderer(outputPath)
                    mutablePdfUiTotalPageCount.intValue = currentFilePdfRenderer.value!!.pageCount

                    // Trigger recomposition of the PDF viewer, if this is new download.
                    mutableTriggerPDFViewerRecomposition.value = !mutableTriggerPDFViewerRecomposition.value

                    // Close the download dialog.
                    ScreenPDFViewerCompanion.mutableShowLoadingDialog.value = false
                }

                is FileDownloadEvent.Failure -> {
                    ScreenPDFViewerCompanion.mutablePdfDownloadStatusMessage.value = it.failure

                    // Displaying the error message.
                    ScreenPDFViewerCompanion.showAlertDialog(it.failure)
                }

                is FileDownloadEvent.Cancelled -> Unit

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
        /* Stores the bitmap of all pages of the currently opened PDF file. */
        internal var mutableBitmapMap: MutableMap<Int, Bitmap> = mutableMapOf()

        /* Stores the state of the current PDF page reading. */
        internal val mutablePdfUiEventMessage = mutableStateOf("")
        internal val mutablePdfUiCurrentPage = mutableIntStateOf(0)
        internal val mutablePdfUiTotalPageCount = mutableIntStateOf(0)

        /* Stores the state of the current PDF downloading/loading. */
        internal val mutablePdfDownloadPercentage = mutableIntStateOf(0)
        internal val mutablePdfDownloadStatusMessage = mutableStateOf("")

        /* Mutable trigger signals for PDF composition. */
        internal val mutableTriggerPDFViewerRecomposition = mutableStateOf(false)
        internal val mutableTriggerPDFPageRecomposition = mutableStateOf(false)

        /* Displaying the PDF info dialog. */
        internal val mutableShowPdfInfoDialog = mutableStateOf(false)

        /* Displaying the PDF page navigator dialog. */
        internal val mutableShowPdfNavigatorDialog = mutableStateOf(false)
        internal val mutableCurrentFieldPageNumberValue = mutableStateOf("")

        /* Displaying alert messages. */
        internal val mutableShowAlertDialog = mutableStateOf(false)
        internal val mutableShowLoadingDialog = mutableStateOf(false)
        internal val txtAlertDialogSubtitle = mutableStateOf("")
        internal val txtLoadingPercentageAnimatable = Animatable(0.0f)

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
            mutablePdfUiCurrentPage.intValue = 0
            mutablePdfUiTotalPageCount.intValue = 0
        }

        /**
         * Displaying the alert dialog.
         * @param subtitle the subtitle/content of the alert dialog.
         */
        fun showAlertDialog(subtitle: String) {
            txtAlertDialogSubtitle.value = subtitle
            if (!mutableShowAlertDialog.value) mutableShowAlertDialog.value = true
            if (mutableShowLoadingDialog.value) mutableShowLoadingDialog.value = false
        }

        /**
         * Displaying the dialog when downloading/loading the PDF files.
         * @param status the status of the current PDF download.
         * @param percentage the download percentage of the PDF file.
         */
        fun showLoadingDialog(status: String, percentage: Int) {
            // Updating the states.
            mutablePdfDownloadStatusMessage.value = status
            mutablePdfDownloadPercentage.intValue = percentage

            // Displaying (hiding) the dialogs.
            if (mutableShowAlertDialog.value) mutableShowAlertDialog.value = false
            if (!mutableShowLoadingDialog.value) mutableShowLoadingDialog.value = true
        }

        /* The pager state for the PDF viewer. */
        var rememberedViewerPagerState: PagerState? = null
    }
}