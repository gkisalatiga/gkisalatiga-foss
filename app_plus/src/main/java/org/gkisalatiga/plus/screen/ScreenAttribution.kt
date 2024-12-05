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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.lib.AppNavigation
import org.json.JSONArray
import org.json.JSONObject


class ScreenAttribution (private val current : ActivityData) : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {

        Scaffold (
            topBar = { getTopBar() }
                ) {

            val scrollState = ScreenAttributionCompanion.rememberedScrollState!!
            Column (
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())
                    .fillMaxSize()
                    .verticalScroll(scrollState)) {
                // Display the main "attribution" contents.
                getMainContent()
            }

        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    private fun getMainContent() {

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

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
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
            scrollBehavior = scrollBehavior
        )
    }

}

class ScreenAttributionCompanion : Application() {
    companion object {
        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}