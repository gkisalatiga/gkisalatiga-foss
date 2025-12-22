/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the Bible using internal Bible viewer.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.Keep
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
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
import org.gkisalatiga.plus.composable.AlertErrorDialog
import org.gkisalatiga.plus.composable.FileDeleteDialog
import org.gkisalatiga.plus.composable.FileDownloadDialog
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.BibleVerseObject
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Beacon
import org.gkisalatiga.plus.lib.CoroutineFileDownload
import org.gkisalatiga.plus.lib.FileDownloadEvent
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.model.BibleDataModel
import org.gkisalatiga.plus.model.BibleDataModelCompanion
import org.gkisalatiga.plus.services.InternalFileManager
import org.json.JSONObject

@Keep
class ScreenBibleViewer(private val current: ActivityData) : ComponentActivity() {

    // The view model for downloading files with progress.
    private val downloadWithProgressViewModel = CoroutineFileDownload()

    @Composable
    private fun getNavigatorDialog() {
        val minimumPage = 1
        // val maximumPage = ScreenBibleViewerCompanion.mutableUiTotalPageCount.intValue
        val maximumPage = 3
        val pageStringLocalized = stringResource(R.string.screen_pdfviewer_nav_page_string)
        val navigatorDialogTitle = stringResource(R.string.screen_bibleviewer_nav_title)
        if (ScreenBibleViewerCompanion.mutableShowNavigatorDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    ScreenBibleViewerCompanion.mutableShowNavigatorDialog.value = false
                },
                title = { Text(navigatorDialogTitle) },
                text = {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = ScreenBibleViewerCompanion.mutableCurrentFieldPageNumberValue.value,
                        onValueChange = {
                            val inputStr = it.replace(".", "").replace(",", "").replace(" ", "").replace("-", "")
                            if (inputStr.isNotBlank() && inputStr.isNotEmpty() && inputStr != "") {
                                if (inputStr.toInt() < minimumPage) ScreenBibleViewerCompanion.mutableCurrentFieldPageNumberValue.value = minimumPage.toString()
                                else if (inputStr.toInt() > maximumPage) ScreenBibleViewerCompanion.mutableCurrentFieldPageNumberValue.value = maximumPage.toString()
                                else ScreenBibleViewerCompanion.mutableCurrentFieldPageNumberValue.value = inputStr
                            } else {
                                ScreenBibleViewerCompanion.mutableCurrentFieldPageNumberValue.value = inputStr
                            }
                        },
                        label = { Text("$pageStringLocalized 1-$maximumPage") },
                        enabled = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        val inputStr = ScreenBibleViewerCompanion.mutableCurrentFieldPageNumberValue.value
                        if (inputStr.isNotBlank() && inputStr.isNotEmpty() && inputStr != "") {
                            current.scope.launch { ScreenBibleViewerCompanion.rememberedViewerPagerState!!.animateScrollToPage(inputStr.toInt() - 1) }
                            ScreenBibleViewerCompanion.mutableShowNavigatorDialog.value = false
                        }
                    }) { Text("OK", color = Color.White) }
                }
            )
        }
    }

    @Composable
    private fun getInfoDialog() {
        val infoDialogTitle = stringResource(R.string.screen_bibleviewer_info_dialog_title)
        val infoDialogContentNameDocAbbr = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_abbr)
        val infoDialogContentNameDocTitle = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_title)
        val infoDialogContentNameDocAuthor = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_author)
        val infoDialogContentNameDocLang = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_lang)
        val infoDialogContentNameDocLicense = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_license)
        val infoDialogContentNameDocSource = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_source)
        val infoDialogContentNameDocSize = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_size)
        val infoDialogContentNameDocLocalPath = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_local_path)
        val infoDialogContentNameDocDesc = stringResource(R.string.screen_bibleviewer_info_dialog_content_name_doc_desc)
        if (ScreenBibleViewerCompanion.mutableShowDocInfoDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    ScreenBibleViewerCompanion.mutableShowDocInfoDialog.value = false
                },
                title = { Text(infoDialogTitle) },
                text = {
                    Column (Modifier.fillMaxWidth().height(400.dp).verticalScroll(rememberScrollState())) {
                        Text(infoDialogContentNameDocAbbr, fontWeight = FontWeight.Bold)
                        Text(ScreenBibleViewerCompanion.bibleAbbr)
                        Spacer(Modifier.fillMaxWidth().height(5.dp))

                        Text(infoDialogContentNameDocTitle, fontWeight = FontWeight.Bold)
                        Text(ScreenBibleViewerCompanion.bibleTitle)
                        Spacer(Modifier.fillMaxWidth().height(5.dp))

                        Text(infoDialogContentNameDocAuthor, fontWeight = FontWeight.Bold)
                        Text(buildAnnotatedString {
                            withLink(
                                LinkAnnotation.Url(ScreenBibleViewerCompanion.bibleAuthorUrl, TextLinkStyles(style = SpanStyle(color = Color.Blue)))
                            ) { append(ScreenBibleViewerCompanion.bibleAuthor) }
                        })
                        Spacer(Modifier.fillMaxWidth().height(5.dp))

                        Text(infoDialogContentNameDocLang, fontWeight = FontWeight.Bold)
                        Text(ScreenBibleViewerCompanion.bibleLang)
                        Spacer(Modifier.fillMaxWidth().height(5.dp))

                        Text(infoDialogContentNameDocLicense, fontWeight = FontWeight.Bold)
                        Text(buildAnnotatedString {
                            withLink(
                                LinkAnnotation.Url(ScreenBibleViewerCompanion.bibleLicenseUrl, TextLinkStyles(style = SpanStyle(color = Color.Blue)))
                            ) { append(ScreenBibleViewerCompanion.bibleLicense) }
                        })
                        Spacer(Modifier.fillMaxWidth().height(5.dp))

                        Text(infoDialogContentNameDocSource, fontWeight = FontWeight.Bold)
                        Text(buildAnnotatedString {
                            withLink(
                                LinkAnnotation.Url(ScreenBibleViewerCompanion.bibleSource, TextLinkStyles(style = SpanStyle(color = Color.Blue)))
                            ) { append(ScreenBibleViewerCompanion.bibleSource) }
                        })
                        Spacer(Modifier.fillMaxWidth().height(5.dp))

                        if (GlobalCompanion.DEBUG_SHOW_INFO_DOC_LOCAL_PATH_INFO) {
                            val bibleLocalPath = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_BIBLE_FILE_LOCATION, LocalStorageDataTypes.STRING, ScreenBibleViewerCompanion.bibleUrl) as String
                            Text(infoDialogContentNameDocLocalPath, fontWeight = FontWeight.Bold)
                            Text(bibleLocalPath)
                            Spacer(Modifier.fillMaxWidth().height(5.dp))
                        }

                        Text(infoDialogContentNameDocSize, fontWeight = FontWeight.Bold)
                        Text(ScreenBibleViewerCompanion.bibleSize)
                        Spacer(Modifier.fillMaxWidth().height(5.dp))

                        Text(infoDialogContentNameDocDesc, fontWeight = FontWeight.Bold)
                        Text(ScreenBibleViewerCompanion.bibleDesc)
                        Spacer(Modifier.fillMaxWidth().height(5.dp))
                    }
                },
                confirmButton = {
                    Button(onClick = { FileDeleteDialog().show(ScreenBibleViewerCompanion.bibleTitle) }) {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeleteForever, "", tint = Color.White)
                            Spacer(Modifier.width(5.dp))
                            Text(stringResource(R.string.screen_bibleviewer_info_dialog_delete_btn).uppercase(), color = Color.White)
                        }
                    }
                    Button(onClick = { ScreenBibleViewerCompanion.mutableShowDocInfoDialog.value = false }) {
                        Text(stringResource(R.string.screen_bibleviewer_info_dialog_ok_btn).uppercase(), color = Color.White)
                    }
                }
            )
        }
    }

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        LaunchedEffect(Unit) {
            // Screen open is inferred from Bible open. DO NOT LOG SCREEN EVENT.
            // Beacon(current).logScreenOpen(NavigationRoutes.SCREEN_BIBLE_VIEWER)
            Beacon(current).logBibleOpen(
                title = ScreenBibleViewerCompanion.bibleAbbr,
                sourceUrl = ScreenBibleViewerCompanion.bibleUrl,
            )
        }

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

            AlertErrorDialog().draw(
                onConfirmRequest = null,
                onDismissRequest = {
                    ScreenBibleViewerCompanion.mutableShowDocInfoDialog.value = false
                    AppNavigation.popBack()
                }
            )
            FileDeleteDialog().draw(
                onConfirmRequest = {
                    InternalFileManager(current.ctx).deleteBible(ScreenBibleViewerCompanion.bibleUrl)
                    ScreenBibleViewerCompanion.mutableShowDocInfoDialog.value = false
                    AppNavigation.popBack()
                },
                onDismissRequest = {
                    ScreenBibleViewerCompanion.mutableShowDocInfoDialog.value = true
                }
            )
            FileDownloadDialog().draw(
                onConfirmRequest = null,
                onDismissRequest = {
                    downloadWithProgressViewModel.cancelDownload()
                    AppNavigation.popBack()
                }
            )

            // Show the Bible information dialog.
            getInfoDialog()

            // Show the Bible page navigator dialog.
            getNavigatorDialog()

        }

        // Scroll the horizontal navigator to the respective position when the pager is scrolled against.
        LaunchedEffect(ScreenBibleViewerCompanion.mutableCurrentChapter.value) {
            current.scope.launch {
                ScreenBibleViewerCompanion.navigatorLazyListState!!.animateScrollToItem(ScreenBibleViewerCompanion.rememberedViewerPagerState!!.currentPage)
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

        /* Displaying the main Bible content. */
        Column (Modifier.fillMaxSize()) {

            // The page navigator.
            /*Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                Button(onClick = {
                    ScreenBibleViewerCompanion.mutableCurrentFieldPageNumberValue.value = (ScreenBibleViewerCompanion.mutableCurrentChapter.value + 1).toString()
                    ScreenBibleViewerCompanion.mutableShowNavigatorDialog.value = true
                }) {
                    Text((ScreenBibleViewerCompanion.mutableCurrentChapter.value + 1).toString() + " / " + ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value.toString(), textAlign = TextAlign.Center)
                }
                VerticalDivider(Modifier.fillMaxHeight().padding(vertical = 2.dp).padding(horizontal = 10.dp))
                LazyRow(state = ScreenBibleViewerCompanion.navigatorLazyListState!!) {
                    val totalBiblePage = ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value
                    if (totalBiblePage > 0) items(totalBiblePage) {
                        val actualPage = it
                        TextButton(onClick = { scope.launch { ScreenBibleViewerCompanion.rememberedViewerPagerState!!.animateScrollToPage(actualPage - 1) } }) {
                            Text(actualPage.toString(), textAlign = TextAlign.Center)
                        }
                    }
                }
            }*/

            // Redundant logging for debugging.
            val abbr = ScreenBibleViewerCompanion.bibleAbbr
            val url = ScreenBibleViewerCompanion.bibleUrl
            val isAlreadyDownloaded = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_BIBLE_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
            val absoluteBiblePathIfCached = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_BIBLE_FILE_LOCATION, LocalStorageDataTypes.STRING, url) as String
            Logger.logBible({}, "url: $url, isAlreadyDownloaded: $isAlreadyDownloaded, absoluteBiblePathIfCached: $absoluteBiblePathIfCached")

            // Ensures that this block will only be ran once every other recomposition.
            LaunchedEffect(Unit) {
                // Attempt to download the file, if not exists.
                if (!isAlreadyDownloaded) {
                    Logger.logBible({}, "Downloading the Bible file: $url")
                    handleBibleDownload(url, abbr, lifecycleOwner)

                } else {
                    // Load the file directly if it exists.
                    loadBibleJson(absoluteBiblePathIfCached)

                    // Trigger recompositioning.
                    ScreenBibleViewerCompanion.mutableTriggerBibleViewerRecomposition.value = !ScreenBibleViewerCompanion.mutableTriggerBibleViewerRecomposition.value
                }

                // Load the last set-up book.
                loadBibleBook(ScreenBibleViewerCompanion.mutableCurrentBook.value)
            }

            key (ScreenBibleViewerCompanion.mutableTriggerBibleViewerRecomposition.value) {
                // Init. the pager state.
                ScreenBibleViewerCompanion.rememberedViewerPagerState = rememberPagerState (
                    initialPage = ScreenBibleViewerCompanion.mutableCurrentChapter.value - 1
                ) { ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value }

                if (ScreenBibleViewerCompanion.currentBookData.size >= ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value) {
                    // Init. the horizontal pager.
                    HorizontalPager(ScreenBibleViewerCompanion.rememberedViewerPagerState!!, modifier = Modifier.fillMaxSize(), beyondViewportPageCount = 3) { chapterPage ->
                        // Updating the state of the currently selected PDF page.
                        ScreenBibleViewerCompanion.mutableCurrentChapter.value = ScreenBibleViewerCompanion.rememberedViewerPagerState!!.currentPage

                        Column (Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                            ScreenBibleViewerCompanion.currentBookData[chapterPage + 1].forEach { verseData ->
                                Text(verseData.text)
                            }
                            Text("${ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value}")
                            Text("${ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value}")
                            Text("${ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value}")
                        }
                    }
                }
            }

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
                    ScreenBibleViewerCompanion.bibleTitle,
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
                IconButton(onClick = { ScreenBibleViewerCompanion.mutableShowDocInfoDialog.value = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Open the info menu of the Bible viewer."
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    /**
     * This function handles the Bible downloading.
     */
    private fun handleBibleDownload(url: String, abbr: String, lifecycleOwner: LifecycleOwner) {
        val targetDownloadDir = InternalFileManager(current.ctx).BIBLE_POOL_FILE_CREATOR
        val targetFilename = abbr.lowercase()

        // Display the loading dialog during download.
        val message = "Downloading $targetFilename.json into ${targetDownloadDir.absolutePath}"
        ScreenBibleViewerCompanion.mutableDownloadStatusMessage.value = message
        FileDownloadDialog().resetPercentage().show(ScreenBibleViewerCompanion.bibleTitle)

        // Do the downloading.
        downloadWithProgressViewModel.downloadFile(url, "$targetFilename.json", targetDownloadDir).observe(lifecycleOwner) {
            when (it) {
                is FileDownloadEvent.Progress -> {
                    ScreenBibleViewerCompanion.mutableDownloadStatusMessage.value = message
                    FileDownloadDialog().show(filename = ScreenBibleViewerCompanion.bibleTitle, percentage = it.percentage)
                }

                is FileDownloadEvent.Success -> {
                    ScreenBibleViewerCompanion.mutableDownloadStatusMessage.value = "Success! Downloaded to: ${it.downloadedFilePath}"

                    val outputPath = it.downloadedFilePath

                    // Saving the download bible file's metadata.
                    val jsonMetadata = JSONObject()
                        .put("abbr", ScreenBibleViewerCompanion.bibleAbbr)
                        .put("name", ScreenBibleViewerCompanion.bibleTitle)
                        .put("author", ScreenBibleViewerCompanion.bibleAuthor)
                        .put("lang", ScreenBibleViewerCompanion.bibleLang)
                        .put("license", ScreenBibleViewerCompanion.bibleLicense)
                        .put("source", ScreenBibleViewerCompanion.bibleSource)
                        .put("sourceSize", ScreenBibleViewerCompanion.bibleSize)
                        .put("description", ScreenBibleViewerCompanion.bibleDesc)
                        .toString(0)

                    // Ensure that we don't download this bible file again in the future.
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_BIBLE_FILE_DOWNLOADED, true, LocalStorageDataTypes.BOOLEAN, url)
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_CACHED_BIBLE_FILE_LOCATION, outputPath, LocalStorageDataTypes.STRING, url)
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_BIBLE_LAST_DOWNLOAD_MILLIS, System.currentTimeMillis(), LocalStorageDataTypes.LONG, url)
                    LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_GET_BIBLE_METADATA, jsonMetadata, LocalStorageDataTypes.STRING, url)

                    // Only if this Bible file has not been made favorite before.
                    if (!LocalStorage(current.ctx).hasKey(LocalStorageKeys.LOCAL_KEY_IS_BIBLE_FILE_MARKED_AS_FAVORITE, url))
                        LocalStorage(current.ctx).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_BIBLE_FILE_MARKED_AS_FAVORITE, false, LocalStorageDataTypes.BOOLEAN, url)

                    // Load the newly downloaded bible file.
                    loadBibleJson(outputPath)

                    // Trigger recomposition of the Bible viewer, if this is new download.
                    ScreenBibleViewerCompanion.mutableTriggerBibleViewerRecomposition.value = !ScreenBibleViewerCompanion.mutableTriggerBibleViewerRecomposition.value

                    // Close the download dialog.
                    FileDownloadDialog().hide()
                }

                is FileDownloadEvent.Failure -> {
                    ScreenBibleViewerCompanion.mutableDownloadStatusMessage.value = it.failure

                    // Displaying the error message.
                    AlertErrorDialog().show(it.failure)
                    FileDownloadDialog().hide()
                }

                is FileDownloadEvent.Cancelled -> Unit

            }
        }
    }

    /**
     * This function handles the Bible parsing (initial whole Bible JSON data).
     */
    private fun loadBibleJson(localPath: String) {
        BibleDataModelCompanion.curLocalBiblePath = localPath
        BibleDataModel().initBibleData()
    }

    private fun loadBibleBook(bookCode: String) {
        // Reset.
        Logger.logBible({}, "Resetting the current in-memory bible data...")
        ScreenBibleViewerCompanion.currentBookData = mutableListOf<MutableList<BibleVerseObject>>()

        if (BibleDataModelCompanion.curData != null) {
            Logger.logBible({}, "Not null: the bible data.")

            var lastChapterIndex = 1
            var mutableChapterVerses = mutableListOf<BibleVerseObject>()
            BibleDataModelCompanion.curData!!.verses.forEach { v ->
                if (v.bookCode == bookCode) {
                    val chapterIndex = v.chapter

                    Logger.logBible({}, chapterIndex.toString())

                    // Handle change of chapter index.
                    if (lastChapterIndex != chapterIndex) {
                        ScreenBibleViewerCompanion.currentBookData.add(lastChapterIndex, mutableChapterVerses)

                        // Resetting the chapter count.
                        mutableChapterVerses = mutableListOf<BibleVerseObject>()
                        lastChapterIndex = chapterIndex
                    }

                    // Adding the current matching verse into the list.
                    mutableChapterVerses.add(BibleVerseObject(
                        text = v.text,
                        chapter = v.chapter,
                        bookCode = v.bookCode,
                        verse = v.verse,
                    ))
                }
            }

            // Finally, determine the size of the current book.
            ScreenBibleViewerCompanion.mutableCurrentBookMaxChapter.value = ScreenBibleViewerCompanion.currentBookData.size - 1
        }
    }
}

class ScreenBibleViewerCompanion : Application() {
    companion object {
        /* Stores the state of the current Bible page reading. */
        // internal val mutableUiEventMessage = mutableStateOf("")
        // internal val mutableUiCurrentPage = mutableIntStateOf(0)
        // internal val mutableUiTotalPageCount = mutableIntStateOf(0)

        /* Triggers Jetpack Compose recompositioning. */
        internal val mutableTriggerBibleViewerRecomposition = mutableStateOf(false)

        /* Displaying the Bible info dialog. */
        internal val mutableShowDocInfoDialog = mutableStateOf(false)

        /* Displaying the Bible page navigator dialog. */
        internal val mutableShowNavigatorDialog = mutableStateOf(false)
        internal val mutableCurrentFieldPageNumberValue = mutableStateOf("")

        /* The current state of the Bible. */
        internal val mutableCurrentBook = mutableStateOf("GEN")
        internal val mutableCurrentBookMaxChapter = mutableStateOf(50)
        internal val mutableCurrentChapter = mutableStateOf(1)

        /* The current Bible data. */
        internal var currentBookData = mutableListOf<MutableList<BibleVerseObject>>()

        /* Displaying the download status message. */
        internal val mutableDownloadStatusMessage = mutableStateOf("")

        /* These information are essential to the screen. */
        internal var bibleAbbr: String = String()
        internal var bibleTitle: String = String()
        internal var bibleAuthor: String = String()
        internal var bibleAuthorUrl: String = String()
        internal var bibleLang: String = String()
        internal var bibleLicense: String = String()
        internal var bibleLicenseUrl: String = String()
        internal var bibleSource: String = String()
        internal var bibleSize: String = String()
        internal var bibleDesc: String = String()
        internal var bibleUrl: String = String()

        /* The lazy list state of the horizontal page navigator. */
        var navigatorLazyListState: LazyListState? = null

        /**
         * This function neatly and thoroughly passes the respective arguments to the screen's handler.
         */
        fun putArguments(abbr: String, name: String, author: String, authorUrl: String, lang: String, license: String, licenseUrl: String, source: String, sourceSize: String, description: String, url: String) {
            bibleAbbr = abbr
            bibleTitle = name
            bibleAuthor = author
            bibleAuthorUrl = authorUrl
            bibleLang = lang
            bibleLicense = license
            bibleLicenseUrl = licenseUrl
            bibleSource = source
            bibleSize = sourceSize
            bibleDesc = description
            bibleUrl = url
        }

        /* The pager state for the Bible viewer. */
        var rememberedViewerPagerState: PagerState? = null
    }
}