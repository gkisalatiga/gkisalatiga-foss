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
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.SearchItemData
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.model.PdfViewModel
import org.gkisalatiga.plus.model.SearchDataType
import org.gkisalatiga.plus.model.SearchUiEvent
import org.gkisalatiga.plus.model.SearchViewModel
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutablePdfUiCurrentPage
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.mutablePdfUiTotalPageCount
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.navigatorLazyListState
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.rememberedViewerPagerState
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion.Companion.txtLoadingPercentageAnimatable

class ScreenSearch(private val current: ActivityData) : ComponentActivity() {

    // The view model for querying the search terms.
    private val searchViewModel = SearchViewModel(current.ctx)

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

        }

        // Show the filter/sorter dialog.
        fun filterApplyPressed() {
            ScreenSearchCompanion.mutableFilterDialogVisibilityState.value = false
        }
        val filterDialogTitle = stringResource(R.string.screen_search_filter_dialog_title)
        val filterDialogSubtitle = stringResource(R.string.screen_pdfviewer_loading_dialog_title)
        val filterDialogButton = stringResource(R.string.screen_search_filter_dialog_apply_button)
        if (ScreenSearchCompanion.mutableFilterDialogVisibilityState.value) {
            AlertDialog(
                onDismissRequest = { filterApplyPressed() },
                title = { Text(filterDialogTitle) },
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
                    Button(onClick = { filterApplyPressed() }) {
                        Text(filterDialogButton)
                    }
                }
            )
        }

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

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun getMainContent() {

        val filterAndSortButtonText = stringResource(R.string.screen_search_filter_and_sort_button)
        val searchHistoryButtonText = stringResource(R.string.screen_search_history_button)
        val searchFieldLabel = stringResource(R.string.screen_search_field_label)
        val searchFieldPlaceholder = stringResource(R.string.screen_search_field_placeholder)

        /* This allows for the use of a sticky header. */
        val text = remember { mutableStateOf("") }
        LazyColumn (Modifier.padding(20.dp)) {
            stickyHeader {
                OutlinedTextField(
                    value = text.value,
                    trailingIcon = { Icon(Icons.Default.Search, "Search Icon") },
                    onValueChange = {
                        if (it == "\n") {
                            text.value = "Gag!"
                        } else text.value = it
                    },
                    label = { Text(searchFieldLabel) },
                    placeholder = { Text(searchFieldPlaceholder) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    enabled = true,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { handleSearchQuery(text.value, listOf(SearchDataType.PDF_TATA_IBADAH)) })
                )
            }

            item {
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
            }

            item {
                /* Add a visually dividing divider :D */
                HorizontalDivider(Modifier.padding(vertical = 20.dp))
            }

            item {
                Text(ScreenSearchCompanion.mutableSearchResultMessage.value)
            }

            item {
                if (ScreenSearchCompanion.mutableSearchResults.value != null) {
                    ScreenSearchCompanion.mutableSearchResults.value!!.forEach {
                        Text(it.content.getString("title"))
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
                IconButton(onClick = { AppNavigation.popBack() }) {
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
    private fun handleSearchQuery(searchTerm: String, searchFilter: List<SearchDataType>) {
        ScreenSearchCompanion.mutableSearchResults.value = null
        searchViewModel.querySearch(searchTerm, searchFilter).observe(current.lifecycleOwner) {
            when (it) {
                is SearchUiEvent.SearchLoading -> ScreenSearchCompanion.mutableSearchResultMessage.value = it.message
                is SearchUiEvent.SearchError -> ScreenSearchCompanion.mutableSearchResultMessage.value = it.message
                is SearchUiEvent.SearchFinished -> ScreenSearchCompanion.mutableSearchResults.value = it.searchResult
                is SearchUiEvent.SearchNotFound -> ScreenSearchCompanion.mutableSearchResultMessage.value = it.message
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
    }
}