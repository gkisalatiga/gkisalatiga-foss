/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the app preferences.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.PrefItemData
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.PreferenceKeys
import org.gkisalatiga.plus.lib.PreferenceSettingItem


class ScreenSettings(private val current: ActivityData) : ComponentActivity() {

    /* The currently set preferences. */
    private val mutableCurrentSettingOfAppTheme: MutableState<PrefItemData?> = mutableStateOf(null)
    private val mutableCurrentSettingOfYouTubeUi: MutableState<PrefItemData?> = mutableStateOf(null)
    private val mutableCurrentSettingOfPdfQuality: MutableState<PrefItemData?> = mutableStateOf(null)
    private val mutableCurrentSettingOfPdfAutoRemove: MutableState<PrefItemData?> = mutableStateOf(null)

    /* Stores preference item list. */
    private lateinit var APP_THEME_PREF_ITEM_LIST: List<PrefItemData>
    private lateinit var YOUTUBE_UI_PREF_ITEM_LIST: List<PrefItemData>
    private lateinit var PDF_QUALITY_PREF_ITEM_LIST: List<PrefItemData>
    private lateinit var PDF_AUTO_DELETE_PREF_ITEM_LIST: List<PrefItemData>

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        // Init the data objects.
        initPrefItemData()
        initCurrentPrefValues()

        // Draw the screen.
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

        // Prepare the help dialog.
        getHelp()

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }
    }

    @Composable
    private fun getHelp() {
        if (ScreenSettingsCompanion.mutableShowSettingsHelpDialog.value) {
            // The dialog title.
            val helpDialogTitle = stringResource(R.string.screen_settings_help_dialog_title)

            // The documentation icon.
            val helpDocIcons = listOf(
                Icons.Default.Palette,
                Icons.Default.VideoLibrary,
                Icons.Default.PictureAsPdf,
                Icons.Default.AutoDelete,
            )

            // The documentation title.
            val helpDocTitles = listOf(
                stringResource(R.string.screen_settings_pref_app_theme),
                stringResource(R.string.screen_settings_pref_youtube_ui),
                stringResource(R.string.screen_settings_pref_pdf_quality),
                stringResource(R.string.screen_settings_pref_pdf_remove),
            )

            // The documentation actual strings.
            val helpDocContents = listOf(
                stringResource(R.string.screen_settings_pref_app_theme_documentation),
                stringResource(R.string.screen_settings_pref_youtube_ui_documentation),
                stringResource(R.string.screen_settings_pref_pdf_quality_documentation),
                stringResource(R.string.screen_settings_pref_pdf_remove_documentation),
            )

            AlertDialog(
                onDismissRequest = {
                    ScreenSettingsCompanion.mutableShowSettingsHelpDialog.value = false
                },
                title = { Text(helpDialogTitle) },
                text = {
                    Column (Modifier.fillMaxWidth().height(400.dp).verticalScroll(
                        rememberScrollState()
                    )) {
                        helpDocIcons.forEachIndexed { i, _ ->
                            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
                                Icon(helpDocIcons[i], contentDescription = "Help doc icon $i", modifier = Modifier.size(20.dp).weight(1.0f))
                                Text(helpDocTitles[i], fontWeight = FontWeight.Bold, modifier = Modifier.weight(7.0f).padding(start = 5.dp))
                            }
                            Spacer(Modifier.fillMaxWidth().height(5.dp))
                            Text(helpDocContents[i])
                            Spacer(Modifier.fillMaxWidth().height(15.dp))
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { ScreenSettingsCompanion.mutableShowSettingsHelpDialog.value = false }) {
                        Text("OK", color = Color.White)
                    }
                }
            )
        }
    }

    @Composable
    private fun getMainContent() {

        /* Settings: modify app theme. */
        val expandedAppTheme = remember { mutableStateOf(false) }
        val titleAppTheme = stringResource(R.string.screen_settings_pref_app_theme)
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Surface (onClick = { expandedAppTheme.value = true }, modifier = Modifier.fillMaxWidth()) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.Palette, contentDescription = "Settings menu icon", modifier = Modifier.size(40.dp))
                    Column (modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(titleAppTheme, fontWeight = FontWeight.Bold)
                        Text(mutableCurrentSettingOfAppTheme.value!!.stringText)
                    }
                }
            }
            DropdownMenu(expanded = expandedAppTheme.value, onDismissRequest = { expandedAppTheme.value = false }, modifier = Modifier.width(300.dp)) {
                APP_THEME_PREF_ITEM_LIST.forEach {
                    DropdownMenuItem(
                        text = { Text(it.stringText) },
                        onClick = { expandedAppTheme.value = false; mutableCurrentSettingOfAppTheme.value = it; saveAndWritePref(); (current.ctx as Activity).recreate() },
                        leadingIcon = {
                            if (it == mutableCurrentSettingOfAppTheme.value) Icon(Icons.Default.RadioButtonChecked, contentDescription = null)
                            else Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null)
                        }
                    )
                }
            }

        }

        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        /* Settings: modify the UI of the YouTube player. */
        val expandedVideoUi = remember { mutableStateOf(false) }
        val titleVideoUi = stringResource(R.string.screen_settings_pref_youtube_ui)
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Surface (onClick = { expandedVideoUi.value = true }, modifier = Modifier.fillMaxWidth()) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = "Settings menu icon", modifier = Modifier.size(40.dp))
                    Column (modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(titleVideoUi, fontWeight = FontWeight.Bold)
                        Text(mutableCurrentSettingOfYouTubeUi.value!!.stringText)
                    }
                }
            }
            DropdownMenu(expanded = expandedVideoUi.value, onDismissRequest = { expandedVideoUi.value = false }, modifier = Modifier.width(300.dp)) {
                YOUTUBE_UI_PREF_ITEM_LIST.forEach {
                    DropdownMenuItem(
                        text = { Text(it.stringText) },
                        onClick = { expandedVideoUi.value = false; mutableCurrentSettingOfYouTubeUi.value = it; saveAndWritePref() },
                        leadingIcon = {
                            if (it == mutableCurrentSettingOfYouTubeUi.value) Icon(Icons.Default.RadioButtonChecked, contentDescription = null)
                            else Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null)
                        }
                    )
                }
            }

        }

        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        /* Settings: modify the render quality of the PDF viewer. */
        val expandedPdfQuality = remember { mutableStateOf(false) }
        val titlePdfQuality = stringResource(R.string.screen_settings_pref_pdf_quality)
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Surface (onClick = { expandedPdfQuality.value = true }, modifier = Modifier.fillMaxWidth()) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Settings menu icon", modifier = Modifier.size(40.dp))
                    Column (modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(titlePdfQuality, fontWeight = FontWeight.Bold)
                        Text(mutableCurrentSettingOfPdfQuality.value!!.stringText)
                    }
                }
            }
            DropdownMenu(expanded = expandedPdfQuality.value, onDismissRequest = { expandedPdfQuality.value = false }, modifier = Modifier.width(300.dp)) {
                PDF_QUALITY_PREF_ITEM_LIST.forEach {
                    DropdownMenuItem(
                        text = { Text(it.stringText) },
                        onClick = { expandedPdfQuality.value = false; mutableCurrentSettingOfPdfQuality.value = it; saveAndWritePref() },
                        leadingIcon = {
                            if (it == mutableCurrentSettingOfPdfQuality.value) Icon(Icons.Default.RadioButtonChecked, contentDescription = null)
                            else Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null)
                        }
                    )
                }
            }
        }

        HorizontalDivider(Modifier.padding(horizontal = 20.dp))

        /* Settings: automatic deletion of PDF files that are no longer read after N days. */
        val expandedPdfAutoDelete = remember { mutableStateOf(false) }
        val titlePdfAutoDelete = stringResource(R.string.screen_settings_pref_pdf_remove)
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Surface (onClick = { expandedPdfAutoDelete.value = true }, modifier = Modifier.fillMaxWidth()) {
                Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 20.dp)) {
                    Icon(Icons.Default.AutoDelete, contentDescription = "Settings menu icon", modifier = Modifier.size(40.dp))
                    Column (modifier = Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                        Text(titlePdfAutoDelete, fontWeight = FontWeight.Bold)
                        Text(mutableCurrentSettingOfPdfAutoRemove.value!!.stringText)
                    }
                }
            }
            DropdownMenu(expanded = expandedPdfAutoDelete.value, onDismissRequest = { expandedPdfAutoDelete.value = false }, modifier = Modifier.width(300.dp)) {
                PDF_AUTO_DELETE_PREF_ITEM_LIST.forEach {
                    DropdownMenuItem(
                        text = { Text(it.stringText) },
                        onClick = { expandedPdfAutoDelete.value = false; mutableCurrentSettingOfPdfAutoRemove.value = it; saveAndWritePref() },
                        leadingIcon = {
                            if (it == mutableCurrentSettingOfPdfAutoRemove.value) Icon(Icons.Default.RadioButtonChecked, contentDescription = null)
                            else Icon(Icons.Default.RadioButtonUnchecked, contentDescription = null)
                        }
                    )
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
                    stringResource(R.string.screensettings_title),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { AppNavigation.popBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            },
            actions = {
                IconButton(onClick = { ScreenSettingsCompanion.mutableShowSettingsHelpDialog.value = true }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Help,
                        contentDescription = "Open the help menu of the settings."
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )
    }

    private fun initCurrentPrefValues() {
        APP_THEME_PREF_ITEM_LIST.forEach {
            if (it.isActive) mutableCurrentSettingOfAppTheme.value = it
        }

        YOUTUBE_UI_PREF_ITEM_LIST.forEach {
            if (it.isActive) mutableCurrentSettingOfYouTubeUi.value = it
        }

        PDF_QUALITY_PREF_ITEM_LIST.forEach {
            if (it.isActive) mutableCurrentSettingOfPdfQuality.value = it
        }

        PDF_AUTO_DELETE_PREF_ITEM_LIST.forEach {
            if (it.isActive) mutableCurrentSettingOfPdfAutoRemove.value = it
        }
    }

    @Composable
    @SuppressLint("ComposableNaming")
    private fun initPrefItemData(ctx: Context = current.ctx) {
        /* Preference item data for the app's dark/light mode UI. */
        APP_THEME_PREF_ITEM_LIST = listOf(
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_THEME_DARK,
                stringText = stringResource(R.string.screen_settings_pref_app_theme_item_dark),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_THEME_DARK) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_THEME_UI) as String)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_THEME_LIGHT,
                stringText = stringResource(R.string.screen_settings_pref_app_theme_item_light),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_THEME_LIGHT) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_THEME_UI) as String)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_THEME_SYSTEM,
                stringText = stringResource(R.string.screen_settings_pref_app_theme_item_system),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_THEME_SYSTEM) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_THEME_UI) as String)
            ),
        )

        /* Preference item data of YouTube UI. */
        YOUTUBE_UI_PREF_ITEM_LIST = listOf(
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_YOUTUBE_UI_NEW,
                stringText = stringResource(R.string.screen_settings_pref_youtube_ui_item_new),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_YOUTUBE_UI_NEW) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_YOUTUBE_UI_THEME) as Boolean)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_YOUTUBE_UI_OLD,
                stringText = stringResource(R.string.screen_settings_pref_youtube_ui_item_old),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_YOUTUBE_UI_OLD) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_YOUTUBE_UI_THEME) as Boolean)
            )
        )

        /* Preference item data of PDF quality. */
        PDF_QUALITY_PREF_ITEM_LIST = listOf(
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_QUALITY_BEST,
                stringText = stringResource(R.string.screen_settings_pref_pdf_quality_item_best),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_QUALITY_BEST) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_PDF_RENDER_QUALITY_FACTOR) as Int)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_QUALITY_HIGH,
                stringText = stringResource(R.string.screen_settings_pref_pdf_quality_item_high),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_QUALITY_HIGH) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_PDF_RENDER_QUALITY_FACTOR) as Int)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_QUALITY_MEDIUM,
                stringText = stringResource(R.string.screen_settings_pref_pdf_quality_item_medium),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_QUALITY_MEDIUM) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_PDF_RENDER_QUALITY_FACTOR) as Int)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_QUALITY_LOW,
                stringText = stringResource(R.string.screen_settings_pref_pdf_quality_item_low),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_QUALITY_LOW) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_PDF_RENDER_QUALITY_FACTOR) as Int)
            )
        )

        /* Preference item data of PDF autodelete. */
        PDF_AUTO_DELETE_PREF_ITEM_LIST = listOf(
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_REMOVE_ALWAYS,
                stringText = stringResource(R.string.screen_settings_pref_pdf_remove_item_always),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_REMOVE_ALWAYS) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES) as Long)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_7,
                stringText = stringResource(R.string.screen_settings_pref_pdf_remove_item_7_days),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_7) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES) as Long)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_14,
                stringText = stringResource(R.string.screen_settings_pref_pdf_remove_item_14_days),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_14) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES) as Long)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_30,
                stringText = stringResource(R.string.screen_settings_pref_pdf_remove_item_30_days),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_REMOVE_DAY_30) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES) as Long)
            ),
            PrefItemData(
                prefItem = PreferenceSettingItem.PREF_VAL_PDF_REMOVE_NEVER,
                stringText = stringResource(R.string.screen_settings_pref_pdf_remove_item_never),
                isActive = AppPreferences(ctx).getActualItemValue(PreferenceSettingItem.PREF_VAL_PDF_REMOVE_NEVER) == (AppPreferences(ctx).getPreferenceValue(PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES) as Long)
            )
        )
    }

    private fun saveAndWritePref(ctx: Context = current.ctx) {
        AppPreferences(ctx).setPreferenceValue(
            PreferenceKeys.PREF_KEY_THEME_UI,
            AppPreferences(ctx).getActualItemValue(mutableCurrentSettingOfAppTheme.value!!.prefItem)!!
        )

        AppPreferences(ctx).setPreferenceValue(
            PreferenceKeys.PREF_KEY_YOUTUBE_UI_THEME,
            AppPreferences(ctx).getActualItemValue(mutableCurrentSettingOfYouTubeUi.value!!.prefItem)!!
        )

        AppPreferences(ctx).setPreferenceValue(
            PreferenceKeys.PREF_KEY_PDF_RENDER_QUALITY_FACTOR,
            AppPreferences(ctx).getActualItemValue(mutableCurrentSettingOfPdfQuality.value!!.prefItem)!!
        )

        AppPreferences(ctx).setPreferenceValue(
            PreferenceKeys.PREF_KEY_KEEP_DAYS_OF_CACHED_PDF_FILES,
            AppPreferences(ctx).getActualItemValue(mutableCurrentSettingOfPdfAutoRemove.value!!.prefItem)!!
        )
    }

}

class ScreenSettingsCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* Whether to display settings help. */
        val mutableShowSettingsHelpDialog = mutableStateOf(false)
    }
}