/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display meta-application information about GKI Salatiga Plus.
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.db.Modules
import org.gkisalatiga.plus.lib.AppNavigation
import org.json.JSONArray
import org.json.JSONObject


class ScreenAttribution (private val current : ActivityData) : ComponentActivity() {

    // The pager state.
    private val horizontalPagerState = ScreenAttributionCompanion.attributionPagerState!!

    // The currently selected tab index.
    private val selectedTabIndex = ScreenAttributionCompanion.currentHorizontalPagerValue

    // The horizontal pager tab selections.
    private val sectionTabName = listOf(
        R.string.screen_attrib_section_hardcoded,
        R.string.screen_attrib_section_webview,
        R.string.screen_attrib_section_books,
    )
    private val icons = listOf(
        Icons.Outlined.Code,
        Icons.Outlined.Language,
        Icons.AutoMirrored.Outlined.MenuBook,
    )
    private val iconsSelected = listOf(
        Icons.Filled.Code,
        Icons.Filled.Language,
        Icons.AutoMirrored.Filled.MenuBook,
    )

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {

        Scaffold (topBar = { getTopBar() }) {
            Box ( Modifier.fillMaxSize().padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                HorizontalPager(
                    state = ScreenAttributionCompanion.attributionPagerState!!,
                    modifier = Modifier.fillMaxSize().padding(top = 0.dp),
                    // Without this property, the left-right page scrolling would be insanely laggy!
                    beyondViewportPageCount = 2
                ) { page ->
                    if (page == 0) getAttributionHardCoded()
                    if (page == 1) getAttributionWebView()
                    if (page == 2) getAttributionLiterature()
                }
            }
        }

        // Integrate the horizontal pager with the top tab.
        LaunchedEffect(horizontalPagerState.currentPage) {
            selectedTabIndex.intValue = horizontalPagerState.currentPage
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    private fun getAttributionHardCoded() {
        val scrollState = ScreenAttributionCompanion.rememberedHardCodedScrollState!!
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            // Display the main "attribution" contents.
            // Load the JSON file containing attributions of all open source programs
            // and code which are used by this app.
            val attribJSON: JSONObject = Main(current.ctx).getAttributions()
            val attribArray: JSONArray = attribJSON.getJSONArray("opensource-attributions")

            // Convert JSONArray to regular list.
            val attribList: MutableList<JSONObject> = mutableListOf()
            for (i in 0 until attribArray.length()) {
                attribList.add(attribArray[i] as JSONObject)
            }

            /* Displaying the attribution cards. */
            var isFirstCard = true
            attribList.forEach {

                if (!isFirstCard) HorizontalDivider(Modifier.padding(horizontal = 20.dp)); isFirstCard = false

                /* The attribution card. */
                Surface (
                    onClick = { current.uriHandler.openUri(it.getString("link")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 10.dp).padding(top = 5.dp), verticalArrangement = Arrangement.Center) {
                        Text(it.getString("title"), fontWeight = FontWeight.Bold)
                        Text("Copyright (C) ${it.getString("year")} ${it.getString("author")}")
                        TextButton(onClick = { current.uriHandler.openUri(it.getString("license-url")) }) {
                            Text(it.getString("license"))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun getAttributionLiterature() {
        val scrollState = ScreenAttributionCompanion.rememberedLiteratureScrollState!!
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            // Display the main "attribution" contents.
            // Load the JSON file containing attributions of all open source programs
            // and code which are used by this app.
            // val attribJSON: JSONObject = Modules(current.ctx).getModulesData().getJSONObject("attributions")
            val attribJSON = Modules(current.ctx).getModulesData().attributions
            val attribArray = attribJSON.books

            // Convert JSONArray to regular list.
            /*val attribList: MutableList<JSONObject> = mutableListOf()
            for (i in 0 until attribArray.length()) {
                attribList.add(attribArray[i] as JSONObject)
            }*/

            /* Displaying the attribution cards. */
            var isFirstCard = true
            attribArray.forEach {

                if (!isFirstCard) HorizontalDivider(Modifier.padding(horizontal = 20.dp)); isFirstCard = false

                /* The attribution card. */
                Surface (
                    onClick = { current.uriHandler.openUri(it.link) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 10.dp).padding(top = 5.dp), verticalArrangement = Arrangement.Center) {
                        Text(it.title, fontWeight = FontWeight.Bold)
                        Text("Copyright (C) ${it.year} ${it.author}")
                        TextButton(onClick = { current.uriHandler.openUri(it.licenseUrl) }) {
                            Text(it.license)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun getAttributionWebView() {
        val scrollState = ScreenAttributionCompanion.rememberedWebViewScrollState!!
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {
            // Display the main "attribution" contents.
            // Load the JSON file containing attributions of all open source programs
            // and code which are used by this app.
            val attribJSON = Modules(current.ctx).getModulesData().attributions
            val attribArray = attribJSON.webview

            // Convert JSONArray to regular list.
            /*val attribList: MutableList<JSONObject> = mutableListOf()
            for (i in 0 until attribArray.length()) {
                attribList.add(attribArray[i] as JSONObject)
            }*/

            /* Displaying the attribution cards. */
            var isFirstCard = true
            attribArray.forEach {

                if (!isFirstCard) HorizontalDivider(Modifier.padding(horizontal = 20.dp)); isFirstCard = false

                /* The attribution card. */
                Surface (
                    onClick = { current.uriHandler.openUri(it.link) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(vertical = 10.dp).padding(top = 5.dp), verticalArrangement = Arrangement.Center) {
                        Text(it.title, fontWeight = FontWeight.Bold)
                        Text("Copyright (C) ${it.year} ${it.author}")
                        TextButton(onClick = { current.uriHandler.openUri(it.licenseUrl) }) {
                            Text(it.license)
                        }
                    }
                }
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        Column(modifier = Modifier.fillMaxWidth()) {
            CenterAlignedTopAppBar(
                colors = TopAppBarColorScheme.default(),
                title = {
                    Text(
                        stringResource(R.string.screenattrib_title),
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
            )

            /* The tab row underneath the top bar. */
            TabRow(
                selectedTabIndex = selectedTabIndex.intValue
            ) {
                sectionTabName.forEachIndexed { index, tabTitleId ->
                    val tabFontWeight = if (selectedTabIndex.intValue == index) FontWeight.Bold else FontWeight.Normal
                    val tabIcon = if (selectedTabIndex.intValue == index) iconsSelected[index] else icons[index]

                    Tab(
                        modifier = Modifier.height(75.dp),
                        selected = selectedTabIndex.intValue == index,
                        icon = { Icon(tabIcon, "") },
                        text = { Text(stringResource(tabTitleId), fontWeight = tabFontWeight, fontSize = 18.sp) },
                        onClick = {
                            selectedTabIndex.intValue = index
                            current.scope.launch {
                                horizontalPagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        }
    }

}

class ScreenAttributionCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedHardCodedScrollState: ScrollState? = null
        var rememberedLiteratureScrollState: ScrollState? = null
        var rememberedWebViewScrollState: ScrollState? = null

        /* For storing tab state. */
        var attributionPagerState: PagerState? = null
        val currentHorizontalPagerValue = mutableIntStateOf(0)
    }
}