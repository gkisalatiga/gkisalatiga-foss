/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the Bible.
 */

@file:SuppressLint("ComposableNaming")

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.composable.YouTubeViewCompanion
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.SearchItemData
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.model.SearchDataSortType
import org.gkisalatiga.plus.model.SearchDataType
import org.gkisalatiga.plus.model.SearchUiEvent
import org.gkisalatiga.plus.model.SearchViewModel

class ScreenSearch(private val current: ActivityData) : ComponentActivity() {

    // The view model for querying the search terms.
    private val searchViewModel = SearchViewModel(current.ctx)

    // The search text.
    private val text = mutableStateOf("")

    // The filter checkbox state.
    private val listFilterEnum = listOf(
        SearchDataType.PDF_WARTA_JEMAAT,
        SearchDataType.PDF_TATA_IBADAH,
        SearchDataType.RENUNGAN_YKB,
        SearchDataType.YOUTUBE_VIDEO,
    )

    // The filter checkbox label.
    private lateinit var listFilterLabel: List<String>

    // The sorter radio button state.
    private val listSortEnum = listOf(
        SearchDataSortType.SORT_BY_NAME_ASCENDING,
        SearchDataSortType.SORT_BY_NAME_DESCENDING,
        SearchDataSortType.SORT_BY_DATE_ASCENDING,
        SearchDataSortType.SORT_BY_DATE_DESCENDING,
    )

    // The sorter radio button label.
    private lateinit var listSortLabel: List<String>

    // The predefined search messages.
    private lateinit var searchMsgFilterEmpty: String
    private lateinit var searchMsgLoading: String
    private lateinit var searchMsgNotFound: String
    private lateinit var searchMsgOk: String

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {

        listFilterLabel = listOf(
            stringResource(R.string.screen_search_qualifier_filter_wj),
            stringResource(R.string.screen_search_qualifier_filter_liturgi),
            stringResource(R.string.screen_search_qualifier_filter_ykb),
            stringResource(R.string.screen_search_qualifier_filter_yt),
        )

        listSortLabel = listOf(
            stringResource(R.string.screen_search_qualifier_sort_name_asc),
            stringResource(R.string.screen_search_qualifier_sort_name_dsc),
            stringResource(R.string.screen_search_qualifier_sort_date_asc),
            stringResource(R.string.screen_search_qualifier_sort_date_dsc),
        )

        searchMsgFilterEmpty = stringResource(R.string.screen_search_message_filter_empty)
        searchMsgLoading = stringResource(R.string.screen_search_message_loading)
        searchMsgNotFound = stringResource(R.string.screen_search_message_not_found)
        searchMsgOk = stringResource(R.string.screen_search_message_ok)

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

        }

        if (ScreenSearchCompanion.mutableActiveFilterCriteria.value == null) {
            ScreenSearchCompanion.mutableActiveFilterCriteria.value = mutableListOf(
                SearchDataType.PDF_TATA_IBADAH,
                SearchDataType.PDF_WARTA_JEMAAT,
                SearchDataType.RENUNGAN_YKB,
                SearchDataType.YOUTUBE_VIDEO,
            )
        }

        if (ScreenSearchCompanion.mutableActiveSorterCriteria.value == null)
            ScreenSearchCompanion.mutableActiveSorterCriteria.value = SearchDataSortType.SORT_BY_DATE_DESCENDING

        // Prepare the dialogs.
        getFilterDialog()
        getHistoryDialog()

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { current.keyboardController!!.hide(); AppNavigation.popBack() }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun getFilterDialog() {

        // Handle events.
        fun filterApplyPressed() {
            ScreenSearchCompanion.mutableFilterDialogVisibilityState.value = false; handleSearchQuery()
        }

        // Actually display the filter dialog.
        val filterDialogTitle = stringResource(R.string.screen_search_filter_dialog_title)
        val filterDialogButton = stringResource(R.string.screen_search_filter_dialog_apply_button)
        if (ScreenSearchCompanion.mutableFilterDialogVisibilityState.value) {
            AlertDialog(
                modifier = Modifier.height(500.dp),
                onDismissRequest = { filterApplyPressed() },
                title = { Text(filterDialogTitle) },
                text = {
                    Column (horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                        /* Filter. */
                        Text(stringResource(R.string.screen_search_filter_dialog_subtitle_filter), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 2.dp))
                        listFilterEnum.forEachIndexed { idx, filterType ->

                            val isChecked = ScreenSearchCompanion.mutableActiveFilterCriteria.value!!.contains(filterType)
                            fun changeState() {
                                /** We don't use MutableList inside MutableState here because changing the list's content does not trigger recomposition. */
                                if (isChecked)
                                    ScreenSearchCompanion.mutableActiveFilterCriteria.value = ScreenSearchCompanion.mutableActiveFilterCriteria.value!! - filterType
                                else
                                    ScreenSearchCompanion.mutableActiveFilterCriteria.value = ScreenSearchCompanion.mutableActiveFilterCriteria.value!! + filterType
                            }

                            Row (Modifier.padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                                CompositionLocalProvider(LocalMinimumInteractiveComponentSize.provides(Dp.Unspecified)) {
                                    Checkbox(
                                        modifier = Modifier.padding(5.dp),
                                        checked = isChecked,
                                        onCheckedChange = { changeState() }
                                    )
                                }
                                Spacer(Modifier.size(10.dp))
                                ClickableText(AnnotatedString(listFilterLabel[idx]), onClick = { changeState() }, modifier = Modifier.fillMaxWidth())
                            }
                        }

                        Spacer(Modifier.size(10.dp))

                        /* Sorter. */
                        Text(stringResource(R.string.screen_search_filter_dialog_subtitle_sort), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 2.dp))
                        listSortEnum.forEachIndexed { idx, sortType ->

                            val changeState: () -> Unit = { ScreenSearchCompanion.mutableActiveSorterCriteria.value = sortType }

                            Row (Modifier.padding(horizontal = 10.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                                CompositionLocalProvider(LocalMinimumInteractiveComponentSize.provides(Dp.Unspecified)) {
                                    RadioButton(
                                        modifier = Modifier.padding(5.dp),
                                        selected = ScreenSearchCompanion.mutableActiveSorterCriteria.value == sortType,
                                        onClick = { changeState() }
                                    )
                                }
                                Spacer(Modifier.size(10.dp))
                                ClickableText(AnnotatedString(listSortLabel[idx]), onClick = { changeState() }, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { filterApplyPressed() }) {
                        Text(filterDialogButton)
                    }
                }
            )
        }
    }

    @Composable
    private fun getHistoryDialog() {
        // Show the history dialog.
        fun historyApplyPressed(selectedSearchTerm: String = String()) {
            ScreenSearchCompanion.mutableHistoryDialogVisibilityState.value = false
        }
        val historyDialogTitle = stringResource(R.string.screen_search_history_dialog_title)
        val historyDialogButton = stringResource(R.string.screen_search_history_dialog_close_button)
        if (ScreenSearchCompanion.mutableHistoryDialogVisibilityState.value) {
            AlertDialog(
                onDismissRequest = { historyApplyPressed() },
                title = { Text(historyDialogTitle) },
                text = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            // The filter and sorter.
                            Row (Modifier.height(75.dp).padding(10.dp), horizontalArrangement = Arrangement.Start) {
                                Button(onClick = {  }) {
                                    Row {
                                        Icon(Icons.AutoMirrored.Default.Sort, "Search results sorter")
                                        Text("Urutkan [EXTRACT]")
                                    }
                                }
                                Checkbox(checked = true, onCheckedChange = null)
                                Text("Juga cari isi konten [EXTRACT]")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { historyApplyPressed() }) {
                        Text(historyDialogButton)
                    }
                }
            )
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun getMainContent() {

        val filterAndSortButtonText = stringResource(R.string.screen_search_filter_and_sort_button)
        val searchHistoryButtonText = stringResource(R.string.screen_search_history_button)
        val searchFieldLabel = stringResource(R.string.screen_search_field_label)
        val searchFieldPlaceholder = stringResource(R.string.screen_search_field_placeholder)

        // Focus requester for the search TextField.
        val focusRequester = remember { FocusRequester() }
        val requestSearchFocus: () -> Unit = { focusRequester.requestFocus(); current.keyboardController!!.show() }

        Column (Modifier.padding(top = 20.dp).padding(horizontal = 20.dp)) {
            /* The search bar. */
            OutlinedTextField(
                value = text.value,
                trailingIcon = {
                    if (text.value.isBlank()) {
                        Surface(shape = CircleShape, onClick = { requestSearchFocus() }) { Icon(Icons.Default.Search, "Search Icon") }
                    } else {
                        Surface(shape = CircleShape, onClick = { requestSearchFocus(); text.value = ""; handleSearchQuery() }) { Icon(Icons.Default.Close, "Close Icon") }
                    }
                },
                onValueChange = { text.value = it; handleSearchQuery() },
                label = { Text(searchFieldLabel) },
                placeholder = { Text(searchFieldPlaceholder) },
                modifier = Modifier.focusRequester(focusRequester).fillMaxWidth().padding(bottom = 10.dp),
                enabled = true,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { handleSearchQuery(); current.keyboardController!!.hide() })
            )

            /* Filter & history buttons. */
            Row {
                Button(onClick = { ScreenSearchCompanion.mutableFilterDialogVisibilityState.value = true }) {
                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.AutoMirrored.Default.Sort, "Search results sorter")
                        Spacer(Modifier.size(5.dp))
                        Text(filterAndSortButtonText, textAlign = TextAlign.Center)
                    }
                }
                Spacer(Modifier.size(10.dp))
                Button(onClick = { ScreenSearchCompanion.mutableHistoryDialogVisibilityState.value = true }) {
                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.History, "Search history view")
                        Spacer(Modifier.size(5.dp))
                        Text(searchHistoryButtonText, textAlign = TextAlign.Center)
                    }
                }
            }

            Text(ScreenSearchCompanion.mutableSearchResultMessage.value, modifier = Modifier.padding(top = 10.dp, bottom = 5.dp))

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 10.dp))

            // Focus on the search TextField at first face.
            LaunchedEffect(Unit) { requestSearchFocus() }
        }

        /* Displaying the search result items. */
        LazyColumn {
            listFilterEnum.forEachIndexed { idx, enum ->
                val searchResultNodes: MutableList<SearchItemData> = mutableListOf<SearchItemData>().let { list -> ScreenSearchCompanion.mutableSearchResults.value?.forEach { if (it.dataType == enum) list.add(it) }; list }
                if (searchResultNodes.isNotEmpty()) {
                    stickyHeader {
                        Box (Modifier.fillMaxWidth().padding(horizontal = 20.dp).background(Color.White)) { Text(listFilterLabel[idx].uppercase(), fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), fontSize = 16.sp) }
                    }
                    item {
                        /* Filtering search item types. */
                        var isFirstCard = true
                        searchResultNodes.forEach {
                            if (!isFirstCard) HorizontalDivider(Modifier.padding(horizontal = 20.dp)); isFirstCard = false

                            when (it.dataType) {

                                SearchDataType.PDF_TATA_IBADAH -> {

                                    // Preparing the arguments.
                                    val title = it.content.getString("title")
                                    val urlId = StringFormatter.getGoogleDriveId( it.content.getString("link") )

                                    // Let's obtain the download URL.
                                    val url = StringFormatter.getGoogleDriveDownloadURL(urlId)

                                    // Additional information of the PDF not present in the remote JSON main data.
                                    val author = "Gereja Kristen Indonesia Salatiga"
                                    val publisher = "Gereja Kristen Indonesia Salatiga"
                                    val publisherLoc = "Kota Salatiga, Jawa Tengah 50742"
                                    val year = it.content.getString("date").split('-')[0]
                                    val thumbnail = it.content.getString("thumbnail")
                                    val source = it.content.getString("post-page")
                                    val size = it.content.getString("size")

                                    Surface (
                                        onClick = {
                                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, title, Toast.LENGTH_SHORT).show()

                                            // Navigate to the PDF viewer.
                                            ScreenPDFViewerCompanion.putArguments(title, author, publisher, publisherLoc, year, thumbnail, url, source, size)
                                            AppNavigation.navigate(NavigationRoutes.SCREEN_PDF_VIEWER)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(5.dp),
                                                modifier = Modifier.weight(1.0f).fillMaxWidth()
                                            ) {
                                                AsyncImage(
                                                    thumbnail,
                                                    contentDescription = "Liturgy Book: $title",
                                                    error = painterResource(R.drawable.menu_cover_liturgi),
                                                    placeholder = painterResource(R.drawable.thumbnail_placeholder_vert_notext),
                                                    contentScale = ContentScale.FillWidth
                                                )
                                            }
                                            Column (modifier = Modifier.weight(6.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Top) {
                                                Text(title, fontWeight = FontWeight.Bold)

                                                val isDownloaded = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
                                                val isDownloadedTitle = stringResource(R.string.pdf_already_downloaded_localized)
                                                val badgeColor = Colors.MAIN_PDF_DOWNLOADED_BADGE_COLOR
                                                if (isDownloaded) {
                                                    // The downloaded PDF badge.
                                                    Row (verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.CheckCircle, "File downloaded icon", modifier = Modifier.size(18.dp).padding(end = 6.dp), tint = badgeColor)
                                                        Text(isDownloadedTitle, maxLines = 1, overflow = TextOverflow.Ellipsis, color = badgeColor, fontSize = 12.5.sp, lineHeight = 13.sp)
                                                    }
                                                } else {
                                                    // The file size.
                                                    Row (verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Download, "File download size icon", modifier = Modifier.size(18.dp).padding(end = 6.dp))
                                                        Text(size, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.5.sp, lineHeight = 13.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                SearchDataType.PDF_WARTA_JEMAAT -> {

                                    // Preparing the arguments.
                                    val title = it.content.getString("title")
                                    val urlId = StringFormatter.getGoogleDriveId( it.content.getString("link") )

                                    // Let's obtain the download URL.
                                    val url = StringFormatter.getGoogleDriveDownloadURL(urlId)

                                    // Additional information of the PDF not present in the remote JSON main data.
                                    val author = "Gereja Kristen Indonesia Salatiga"
                                    val publisher = "Gereja Kristen Indonesia Salatiga"
                                    val publisherLoc = "Kota Salatiga, Jawa Tengah 50742"
                                    val year = it.content.getString("date").split('-')[0]
                                    val thumbnail = it.content.getString("thumbnail")
                                    val source = it.content.getString("post-page")
                                    val size = it.content.getString("size")

                                    Surface (
                                        onClick = {
                                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, title, Toast.LENGTH_SHORT).show()

                                            // Navigate to the PDF viewer.
                                            ScreenPDFViewerCompanion.putArguments(title, author, publisher, publisherLoc, year, thumbnail, url, source, size)
                                            AppNavigation.navigate(NavigationRoutes.SCREEN_PDF_VIEWER)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(5.dp),
                                                modifier = Modifier.weight(1.0f).fillMaxWidth()
                                            ) {
                                                AsyncImage(
                                                    thumbnail,
                                                    contentDescription = "Church News Book: $title",
                                                    error = painterResource(R.drawable.menu_cover_wj),
                                                    placeholder = painterResource(R.drawable.thumbnail_placeholder_vert_notext),
                                                    contentScale = ContentScale.FillWidth
                                                )
                                            }
                                            Column (modifier = Modifier.weight(6.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Top) {
                                                Text(title, fontWeight = FontWeight.Bold)

                                                val isDownloaded = LocalStorage(current.ctx).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_PDF_FILE_DOWNLOADED, LocalStorageDataTypes.BOOLEAN, url) as Boolean
                                                val isDownloadedTitle = stringResource(R.string.pdf_already_downloaded_localized)
                                                val badgeColor = Colors.MAIN_PDF_DOWNLOADED_BADGE_COLOR
                                                if (isDownloaded) {
                                                    // The downloaded PDF badge.
                                                    Row (verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.CheckCircle, "File downloaded icon", modifier = Modifier.size(18.dp).padding(end = 6.dp), tint = badgeColor)
                                                        Text(isDownloadedTitle, maxLines = 1, overflow = TextOverflow.Ellipsis, color = badgeColor, fontSize = 12.5.sp, lineHeight = 13.sp)
                                                    }
                                                } else {
                                                    // The file size.
                                                    Row (verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.Download, "File download size icon", modifier = Modifier.size(18.dp).padding(end = 6.dp))
                                                        Text(size, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.5.sp, lineHeight = 13.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                SearchDataType.RENUNGAN_YKB -> {

                                    // Preparing the arguments.
                                    val title = it.content.getString("title")
                                    val thumbnail = it.content.getString("featured-image")
                                    val date = StringFormatter.convertDateFromJSON(it.content.getString("date"))
                                    val html = it.content.getString("html")

                                    Surface (
                                        onClick = {
                                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, title, Toast.LENGTH_SHORT).show()

                                            // Navigate to the WebView viewer.
                                            ScreenInternalHTMLCompanion.internalWebViewTitle = title
                                            ScreenInternalHTMLCompanion.targetHTMLContent = html

                                            // Pushing the screen.
                                            AppNavigation.navigate(NavigationRoutes.SCREEN_INTERNAL_HTML)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(5.dp),
                                                modifier = Modifier.weight(1.0f).fillMaxWidth()
                                            ) {
                                                AsyncImage(
                                                    if (thumbnail == "") it.tag2 else thumbnail,
                                                    contentDescription = "YKB item banner: $title",
                                                    error = painterResource(R.drawable.thumbnail_error),
                                                    placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                                    modifier = Modifier.aspectRatio(1f),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Column (modifier = Modifier.weight(6.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Top) {
                                                CompositionLocalProvider(LocalMinimumInteractiveComponentSize.provides(Dp.Unspecified)) {

                                                    Text(title, fontWeight = FontWeight.Bold)

                                                    // The devotion date.
                                                    Row (verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.CalendarMonth, "File download size icon", modifier = Modifier.size(18.dp).padding(end = 6.dp))
                                                        Text(date, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.5.sp, lineHeight = 13.sp)
                                                    }

                                                    // The "tag".
                                                    Row (verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Default.FolderOpen, "Folder icon", modifier = Modifier.size(18.dp).padding(end = 6.dp))
                                                        Text(it.tag1, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.5.sp, lineHeight = 13.sp)
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }

                                SearchDataType.YOUTUBE_VIDEO -> {

                                    // Preparing the arguments.
                                    val title = it.content.getString("title")
                                    val date = StringFormatter.convertDateFromJSON(it.content.getString("date"))
                                    val url = it.content.getString("link")
                                    val desc = it.content.getString("desc")
                                    val thumbnail = it.content.getString("thumbnail")

                                    Surface (
                                        onClick = {
                                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, title, Toast.LENGTH_SHORT).show()

                                            // Trying to switch to the YouTube viewer and open the stream.
                                            Logger.log({}, "Opening YouTube stream: $url.")
                                            YouTubeViewCompanion.seekToZero()

                                            // Put arguments to the YouTube video viewer.
                                            YouTubeViewCompanion.putArguments(
                                                date = date,
                                                desc = desc,
                                                thumbnail = thumbnail,
                                                title = title,
                                                yt_id = StringFormatter.getYouTubeIDFromUrl(url),
                                                yt_link = url
                                            )

                                            // Navigate to the screen.
                                            AppNavigation.navigate(NavigationRoutes.SCREEN_LIVE)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(5.dp),
                                                modifier = Modifier.weight(2.5f).fillMaxWidth()
                                            ) {
                                                AsyncImage(
                                                    thumbnail,
                                                    contentDescription = "YouTube thumbnaiil: $title",
                                                    error = painterResource(R.drawable.thumbnail_error_stretched),
                                                    placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                                    modifier = Modifier.aspectRatio(1.77778f).fillMaxHeight(),
                                                    contentScale = ContentScale.Crop
                                                )
                                            }
                                            Column (modifier = Modifier.weight(6.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Top) {
                                                Text(title, fontWeight = FontWeight.Bold, maxLines = 3, overflow = TextOverflow.Ellipsis)

                                                // The "tag".
                                                Row (verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Subscriptions, "Playlist icon", modifier = Modifier.size(18.dp).padding(end = 6.dp))
                                                    Text(it.tag1, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 12.5.sp, lineHeight = 13.sp)
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screensearch_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { current.keyboardController!!.hide(); AppNavigation.popBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            },
            actions = { },
            scrollBehavior = scrollBehavior
        )
    }

    /**
     * Handles the searching and observes for any state change.
     */
    private fun handleSearchQuery(searchTerm: String = text.value, searchFilter: List<SearchDataType> = ScreenSearchCompanion.mutableActiveFilterCriteria.value!!, searchSort: SearchDataSortType = ScreenSearchCompanion.mutableActiveSorterCriteria.value!!) {
        ScreenSearchCompanion.mutableSearchResults.value = null
        searchViewModel.querySearch(searchTerm, searchFilter, searchSort).observe(current.lifecycleOwner) {
            when (it) {
                is SearchUiEvent.SearchError -> {
                    ScreenSearchCompanion.mutableSearchResultMessage.value = it.message
                    Logger.log({}, it.message, LoggerType.VERBOSE)
                }
                is SearchUiEvent.SearchFilterEmpty -> {
                    ScreenSearchCompanion.mutableSearchResultMessage.value = searchMsgFilterEmpty
                    Logger.log({}, it.message, LoggerType.VERBOSE)
                }
                is SearchUiEvent.SearchFinished -> {
                    ScreenSearchCompanion.mutableSearchResults.value = it.searchResult
                    ScreenSearchCompanion.mutableSearchResultMessage.value = searchMsgOk.replace("%%%NUM%%%", it.searchResult.size.toString())
                    Logger.log({}, it.message, LoggerType.VERBOSE)
                }
                is SearchUiEvent.SearchLoading -> {
                    ScreenSearchCompanion.mutableSearchResultMessage.value = searchMsgLoading
                    Logger.log({}, it.message, LoggerType.VERBOSE)
                }
                is SearchUiEvent.SearchNotFound -> {
                    ScreenSearchCompanion.mutableSearchResultMessage.value = searchMsgNotFound
                    Logger.log({}, it.message, LoggerType.VERBOSE)
                }
                is SearchUiEvent.SearchQueryEmpty -> {
                    ScreenSearchCompanion.mutableSearchResultMessage.value = ""
                    Logger.log({}, it.message, LoggerType.VERBOSE)
                }
            }
        }
    }

}

/**
 * This companion class stores every static information related to this class file.
 * It is globally readable, exposing any values assigned to public variables stored in it.
 */
class ScreenSearchCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* Handles responses from the search view model. */
        var mutableSearchResults: MutableState<List<SearchItemData>?> = mutableStateOf(null)
        val mutableSearchResultMessage = mutableStateOf("")

        /* The filter/sorter and history dialog. */
        val mutableFilterDialogVisibilityState = mutableStateOf(false)
        val mutableHistoryDialogVisibilityState = mutableStateOf(false)

        /* Stores active filter and sort criteria. */
        val mutableActiveFilterCriteria: MutableState<List<SearchDataType>?> = mutableStateOf(null)
        val mutableActiveSorterCriteria: MutableState<SearchDataSortType?> = mutableStateOf(null)
    }
}
