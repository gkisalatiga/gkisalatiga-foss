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
import android.app.Application
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.StringFormatter

class ScreenAgenda (private val current : ActivityData) : ComponentActivity() {

    @Composable
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
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
        BackHandler { AppNavigation.popBack() }

    }

    @Composable
    @SuppressLint("ComposableNaming")
    private fun getMainContent() {

        // The agenda node.
        val agendaJSONNode = MainCompanion.jsonRoot!!.getJSONObject("agenda")

        // Enlist the list of title, corresponding to name of days.
        val dayTitleList = agendaJSONNode.keys()

        // The column's saved scroll state.
        val scrollState = ScreenAgendaCompanion.rememberedScrollState!!
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

            /* Enlisting the day selector chips. */
            val selected = ScreenAgendaCompanion.mutableCurrentDay
            var isFirst = true
            Row (modifier = Modifier.fillMaxWidth()) {
                for (key in dayTitleList) {
                    val dayInLocale = StringFormatter.dayLocaleShortInIndonesian[key]!!
                    val startPadding = if (isFirst) 0.dp else 5.dp; isFirst = false
                    Button(
                        onClick = { ScreenAgendaCompanion.mutableCurrentDay.value = key },
                        modifier = Modifier.weight(1.0f).padding(start = startPadding).width(intrinsicSize = IntrinsicSize.Max),
                        colors = ButtonDefaults.buttonColors(containerColor = if (key == selected.value) current.colors.screenAgendaChipSelectedBackgroundColor else current.colors.screenAgendaChipUnselectedBackgroundColor),
                        border =  if (key == selected.value) null else BorderStroke(1.dp, Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        Text(
                            dayInLocale.uppercase(),
                            fontSize = 12.sp,
                            color = if (key == selected.value) current.colors.screenAgendaChipTextSelectedBackgroundColor else current.colors.screenAgendaChipTextUnselectedBackgroundColor,
                            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).padding(2.dp),
                            textAlign = TextAlign.Center
                        )  // --- end of Text.
                    }  // --- end of Button.
                }
            }

            Spacer(Modifier.height(15.dp))

            // Get the day's name in current locale; then display the day title.
            val dayInLocale = StringFormatter.dayLocaleInIndonesian[selected.value]!!
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(dayInLocale, modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 32.sp, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }

            // Obtain this day's list of events.
            val todayNode = agendaJSONNode.getJSONArray(selected.value)

            // Iterating through every event agenda on this day.
            /* Displays all agendas of the selected agenda day. */
            for (index in 0 until todayNode.length()) {

                // Draw the list item for the current event.
                Surface(shape = RoundedCornerShape(15.dp), modifier = Modifier.padding(top = 10.dp)) {
                    Column (Modifier.fillMaxSize().background(current.colors.screenAgendaItemBackgroundColor)) {
                        Column (modifier = Modifier.fillMaxSize().padding(15.dp)) {
                            // The item title.
                            Text(todayNode.getJSONObject(index).getString("name"), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(Modifier.height(5.dp))

                            Row (verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Place, "", tint = current.colors.screenAgendaContentTintColor)
                                Spacer(Modifier.width(10.dp))
                                Text( todayNode.getJSONObject(index).getString("place"), fontSize = 14.sp, color = current.colors.screenAgendaContentTintColor)
                            }
                            Row (verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Groups, "", tint = current.colors.screenAgendaContentTintColor)
                                Spacer(Modifier.width(10.dp))
                                Text( todayNode.getJSONObject(index).getString("representative"), fontSize = 14.sp, color = current.colors.screenAgendaContentTintColor)
                            }

                            // Display additional note.
                            val note = todayNode.getJSONObject(index).getString("note")
                            if (note.isNotEmpty())
                                Row (verticalAlignment = Alignment.Top) {
                                    Icon(Icons.AutoMirrored.Default.Article, "", tint = current.colors.screenAgendaContentTintColor)
                                    Spacer(Modifier.width(10.dp))
                                    Text( note, fontSize = 14.sp, color = current.colors.screenAgendaContentTintColor )
                                }
                        }

                        // Display time info.
                        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxSize()) {
                            Surface(shape = RoundedCornerShape(topStart = 15.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp)) {
                                Column(modifier = Modifier.background(current.colors.screenAgendaItemTimeBackgroundColor)) {
                                    Text( todayNode.getJSONObject(index).getString("time"), modifier = Modifier.padding(horizontal = 10.dp).padding(vertical = 5.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = current.colors.screenAgendaItemTimeTextColor)
                                }
                            }
                        }
                    }
                }  // --- end of surface.

            }  // --- end of for loop.
        }  // --- end of column.

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        CenterAlignedTopAppBar(
            colors = TopAppBarColorScheme.default(),
            title = {
                Text(
                    stringResource(R.string.screenagenda_title),
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

class ScreenAgendaCompanion : Application() {
    companion object {
        const val DEFAULT_DAY_CHIP_INDEX = "sun"

        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* The currently selected day chip. */
        var mutableCurrentDay = mutableStateOf(DEFAULT_DAY_CHIP_INDEX)
    }
}