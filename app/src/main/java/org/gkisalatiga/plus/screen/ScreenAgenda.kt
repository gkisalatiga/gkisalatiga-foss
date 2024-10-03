/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display a composable "web view" and load a custom HTML body.
 * Only those HTML contents stored in the JSON schema's "data/static" node can be displayed.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.StringFormatter
import java.util.ArrayList

class ScreenAgenda() : ComponentActivity() {

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    public fun getComposable() {
        Scaffold (
            topBar = { this.getTopBar() }
                ) {

            // Display the necessary content.
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
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

        // The agenda node.
        val agendaJSONNode = GlobalSchema.globalJSONObject!!.getJSONObject("agenda")

        // Enlist the list of title, corresponding to name of days.
        val dayTitleList = agendaJSONNode.keys()

        // The column's saved scroll state.
        val scrollState = GlobalSchema.screenAgendaScrollState!!
        Column (
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.verticalScroll(scrollState).fillMaxSize().padding(20.dp)
        ) {
            /* Display the banner image. */
            val imgSource = R.drawable.banner_agenda
            val imgDescription = "Menu banner"
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.aspectRatio(1.77778f)
            ) {
                Image(
                    painter = painterResource(imgSource),
                    contentDescription = imgDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            // Iterate through every day list.
            var isFirstElement = true
            for (key in dayTitleList) {

                if (isFirstElement) isFirstElement = false else Spacer(Modifier.height(20.dp))

                // Get the day's name in current locale; then display the day title.
                val dayInLocale = StringFormatter().dayLocaleInIndonesian[key]!!
                Text(dayInLocale, modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 26.sp, overflow = TextOverflow.Ellipsis)

                // Obtain this day's list of events.
                val todayNode = agendaJSONNode.getJSONArray(key)

                // Iterating through every event agenda on this day.
                var isFirstRowElement = true
                for (index in 0 until todayNode.length()) {
                    if (isFirstRowElement) {
                        HorizontalDivider()
                        isFirstRowElement = false
                    }

                    // Draw the list item for the current event.
                    // SOURCE: https://www.composables.com/material/listitem
                    ListItem(
                        headlineContent = { Text( todayNode.getJSONObject(index).getString("name"), fontWeight = FontWeight.Bold ) },
                        overlineContent = { Text( todayNode.getJSONObject(index).getString("time") ) },
                        supportingContent = {
                            Column {
                                Row {
                                    Icon(Icons.Default.Place, "")
                                    Spacer(Modifier.width(5.dp))
                                    Text( todayNode.getJSONObject(index).getString("place") )
                                }
                                Row (verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Groups, "")
                                    Spacer(Modifier.width(5.dp))
                                    Text( todayNode.getJSONObject(index).getString("representative") )
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
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
                    stringResource(R.string.screenagenda_title),
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
            actions = { },
            scrollBehavior = scrollBehavior
        )
    }

}