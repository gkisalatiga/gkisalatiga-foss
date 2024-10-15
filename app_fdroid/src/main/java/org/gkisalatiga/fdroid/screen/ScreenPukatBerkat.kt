/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display the Bible.
 */

package org.gkisalatiga.fdroid.screen

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gkisalatiga.fdroid.R
import org.gkisalatiga.fdroid.fragment.FragmentGalleryList
import org.gkisalatiga.fdroid.fragment.FragmentGalleryStory
import org.gkisalatiga.fdroid.global.GlobalSchema

class ScreenPukatBerkat : ComponentActivity() {

    // The pager state.
    private val horizontalPagerState = ScreenPukatBerkatCompanion.pukatBerkatPagerState!!

    // The currently selected tab index.
    private val selectedTabIndex = ScreenPukatBerkatCompanion.currentHorizontalPagerValue

    // The coroutine scope.
    private lateinit var scope: CoroutineScope

    // The horizontal pager tab selections.
    private val topBarTitle = GlobalSchema.displayedAlbumTitle
    private val tabs = listOf(
        R.string.pukatberkat_section_food,
        R.string.pukatberkat_section_goods,
        R.string.pukatberkat_section_service,
    )
    private val icons = listOf(
        Icons.Outlined.Restaurant,
        Icons.Outlined.Category,
        Icons.Outlined.VolunteerActivism,
    )
    private val iconsSelected = listOf(
        Icons.Filled.Restaurant,
        Icons.Filled.Category,
        Icons.Filled.VolunteerActivism,
    )

    // Each item of the following list represents the dict value in the "data/pukat-berkat/[N]/type" path
    // of the "gkisplus-main.json" JSON file, where N is an arbitrary non-negative integer.
    private val pukatBerkatDictIndices = listOf(
        "food",
        "non-food",
        "service"
    )

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        // Initializing pre-init variables.
        scope = rememberCoroutineScope()

        Scaffold (topBar = { getTopBar() }) {
            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
            ) {
                Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                    getMainContent()
                }
            }
        }  // --- end of Scaffold.

        // Integrate the horizontal pager with the top tab.
        LaunchedEffect(horizontalPagerState.currentPage) {
            selectedTabIndex.intValue = horizontalPagerState.currentPage
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler {
            GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
        }
    }

    @Composable
    private fun getMainContent() {

        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxHeight()
                .wrapContentHeight()
                .padding(top = 0.dp),
            // Without this property, the left-right page scrolling would be insanely laggy!
            beyondViewportPageCount = 2
        ) {
            getSectionUI(pukatBerkatDictIndices[it])
        }

    }

    @Composable
    private fun getSectionUI(dictIndex: String) {
        // Display the markdown text.
        Column {
            val md: String = """
                        # Sample Pukat Berkat. (3)
                        ${dictIndex}.
                        """.trimIndent()
            MarkdownText(
                modifier = Modifier.padding(20.dp).fillMaxSize(),
                markdown = md.trimIndent(),
                style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Justify)
            )

            // TODO: Upgrade to schema v2.0
            // GlobalSchema.globalJSONObject.getJSONArray("pukat-berkat")
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {

        Column(modifier = Modifier.fillMaxWidth()) {
            /* The navigational topBar. */
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        stringResource(R.string.screenpukatberkat_title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        GlobalSchema.pushScreen.value = GlobalSchema.popBackScreen.value
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = { }
            )

            /* The tab row underneath the top bar. */
            TabRow(
                selectedTabIndex = selectedTabIndex.intValue
            ) {
                tabs.forEachIndexed { index, tabTitleId ->
                    val tabFontWeight = if (selectedTabIndex.intValue == index) FontWeight.Bold else FontWeight.Normal
                    val tabIcon = if (selectedTabIndex.intValue == index) iconsSelected[index] else icons[index]

                    Tab(
                        modifier = Modifier.height(75.dp),
                        selected = selectedTabIndex.intValue == index,
                        icon = { Icon(tabIcon, "") },
                        text = { Text(stringResource(tabTitleId), fontWeight = tabFontWeight, fontSize = 18.sp) },
                        onClick = {
                            selectedTabIndex.intValue = index
                            scope.launch {
                                horizontalPagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }  // --- end of Column.

    }
}

/**
 * This companion class stores every static information related to this class file.
 * It is globally readable, exposing any values assigned to public variables stored in it.
 */
class ScreenPukatBerkatCompanion : Application() {
    companion object {
        /* Saves information about the currently/last selected Pukat Berkat tab. */
        val currentHorizontalPagerValue = mutableIntStateOf(0)
        var pukatBerkatPagerState: PagerState? = null
    }
}