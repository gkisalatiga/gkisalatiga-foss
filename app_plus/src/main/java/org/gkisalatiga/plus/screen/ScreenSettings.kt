/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the Bible.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.PreferenceSettingValues


class ScreenSettings : ComponentActivity() {

    /* The currently set preferences. */
    private val mutableCurrentSettingOfYouTubeUi: MutableState<PreferenceSettingValues?> = mutableStateOf(null)
    private val mutableCurrentSettingOfPdfQuality: MutableState<PreferenceSettingValues?> = mutableStateOf(null)
    private val mutableCurrentSettingOfPdfRemove: MutableState<PreferenceSettingValues?> = mutableStateOf(null)

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
                    .verticalScroll(ScreenSettingsCompanion.rememberedScrollState!!)
            ) { getMainContent() }

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }
    }

    @Composable
    private fun getMainContent() {

        /* Settings: modify the UI of the YouTube player. */
        val expandedVideoUi = remember { mutableStateOf(false) }
        val titleVideoUi = stringResource(R.string.screen_settings_pref_youtube_ui)
        val titleVideoUiItemNew = stringResource(R.string.screen_settings_pref_youtube_ui_item_new)
        val titleVideoUiItemOld = stringResource(R.string.screen_settings_pref_youtube_ui_item_old)
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Surface (onClick = { expandedVideoUi.value = true }, modifier = Modifier.fillMaxWidth()) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = "Settings menu icon", modifier = Modifier.size(40.dp))
                    Column (modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(titleVideoUi, fontWeight = FontWeight.Bold)
                        Text("Pengaturan lama di sini")  // TODO: Extract string into XML.
                    }
                }
            }
            DropdownMenu(expanded = expandedVideoUi.value, onDismissRequest = { expandedVideoUi.value = false }, modifier = Modifier.width(300.dp)) {
                DropdownMenuItem(
                    text = { Text(titleVideoUiItemNew) },
                    onClick = { expandedVideoUi.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonChecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titleVideoUiItemOld) },
                    onClick = { expandedVideoUi.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
            }

        }

        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        /* Settings: modify the render quality of the PDF viewer. */
        val expandedPdfQuality = remember { mutableStateOf(false) }
        val titlePdfQuality = stringResource(R.string.screen_settings_pref_pdf_quality)
        val titlePdfQualityBest = stringResource(R.string.screen_settings_pref_pdf_quality_item_best)
        val titlePdfQualityHigh = stringResource(R.string.screen_settings_pref_pdf_quality_item_high)
        val titlePdfQualityMedium = stringResource(R.string.screen_settings_pref_pdf_quality_item_medium)
        val titlePdfQualityLow = stringResource(R.string.screen_settings_pref_pdf_quality_item_low)
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Surface (onClick = { expandedPdfQuality.value = true }, modifier = Modifier.fillMaxWidth()) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Settings menu icon", modifier = Modifier.size(40.dp))
                    Column (modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(titlePdfQuality, fontWeight = FontWeight.Bold)
                        Text("Pengaturan lama di sini")  // TODO: Extract string into XML.
                    }
                }
            }
            DropdownMenu(expanded = expandedPdfQuality.value, onDismissRequest = { expandedPdfQuality.value = false }, modifier = Modifier.width(300.dp)) {
                DropdownMenuItem(
                    text = { Text(titlePdfQualityBest) },
                    onClick = { expandedPdfQuality.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonChecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titlePdfQualityHigh) },
                    onClick = { expandedPdfQuality.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titlePdfQualityMedium) },
                    onClick = { expandedPdfQuality.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titlePdfQualityLow) },
                    onClick = { expandedPdfQuality.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
            }
        }

        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        /* Settings: automatic deletion of PDF files that are no longer read after N days. */
        val expandedPdfAutoDelete = remember { mutableStateOf(false) }
        val titlePdfAutoDelete = stringResource(R.string.screen_settings_pref_pdf_remove)
        val titlePdfAutoDeleteAlways = stringResource(R.string.screen_settings_pref_pdf_remove_item_always)
        val titlePdfAutoDelete7Days = stringResource(R.string.screen_settings_pref_pdf_remove_item_7_days)
        val titlePdfAutoDelete14Days = stringResource(R.string.screen_settings_pref_pdf_remove_item_14_days)
        val titlePdfAutoDelete30Days = stringResource(R.string.screen_settings_pref_pdf_remove_item_30_days)
        val titlePdfAutoDeleteNever = stringResource(R.string.screen_settings_pref_pdf_remove_item_never)
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Surface (onClick = { expandedPdfAutoDelete.value = true }, modifier = Modifier.fillMaxWidth()) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.AutoDelete, contentDescription = "Settings menu icon", modifier = Modifier.size(40.dp))
                    Column (modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(titlePdfAutoDelete, fontWeight = FontWeight.Bold)
                        Text("Pengaturan lama di sini")  // TODO: Extract string into XML.
                    }
                }
            }
            DropdownMenu(expanded = expandedPdfAutoDelete.value, onDismissRequest = { expandedPdfAutoDelete.value = false }, modifier = Modifier.width(300.dp)) {
                DropdownMenuItem(
                    text = { Text(titlePdfAutoDeleteAlways) },
                    onClick = { expandedPdfAutoDelete.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonChecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titlePdfAutoDelete7Days) },
                    onClick = { expandedPdfAutoDelete.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titlePdfAutoDelete14Days) },
                    onClick = { expandedPdfAutoDelete.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titlePdfAutoDelete30Days) },
                    onClick = { expandedPdfAutoDelete.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text(titlePdfAutoDeleteNever) },
                    onClick = { expandedPdfAutoDelete.value = false },
                    leadingIcon = { Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null) }
                )
            }
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
                    stringResource(R.string.screensettings_title),
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
                        imageVector = Icons.AutoMirrored.Default.Help,
                        contentDescription = "Open the help menu of the settings."
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }
}

class ScreenSettingsCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}