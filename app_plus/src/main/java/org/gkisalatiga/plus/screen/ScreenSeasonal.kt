/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Display seasonal items.
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.fragment.FragmentSeasonalAgenda
import org.gkisalatiga.plus.fragment.FragmentSeasonalBook
import org.gkisalatiga.plus.fragment.FragmentSeasonalGaleri
import org.gkisalatiga.plus.fragment.FragmentSeasonalMain
import org.gkisalatiga.plus.fragment.FragmentSeasonalTwibbon
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONObject

/**
 * TODO: Remove all references to [ScreenForms]. (Use Ctrl+F).
 */
class ScreenSeasonal (private val current : ActivityData) : ComponentActivity() {

    private lateinit var appFlags: JSONObject
    private lateinit var seasonalData: JSONObject

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {

        // Declaring toggles (flags) and seasonal data.
        /* Show or hide feature menus based on flag settings. */
        appFlags = MainCompanion.jsonRoot!!.getJSONObject("backend").getJSONObject("flags")
        seasonalData = ModulesCompanion.jsonRoot!!.getJSONObject("seasonal")

        Scaffold (
            topBar = { getTopBar() }
                ) {
            Box ( Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }

        // Ensure that when we are at the first screen upon clicking "back",
        // the app is exited instead of continuing to navigate back to the previous screens.
        // SOURCE: https://stackoverflow.com/a/69151539
        BackHandler { handleBackPress() }
    }
    
    @Composable
    private fun getMainContent() {

        // Setting the layout to center both vertically and horizontally,
        // and then make it scrollable vertically.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = ScreenSeasonalCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {
            /* Display the church's building image. */
            val imgSource = seasonalData.getString("banner-inside")
            val imgDescription = "Menu banner"
            Surface (
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.aspectRatio(1.77778f)
            ) {
                AsyncImage(
                    imgSource,
                    contentDescription = imgDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            /* Add a visually dividing divider :D */
            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            // Displaying the fragments.
            Box (Modifier.fillMaxSize()) {
                ScreenSeasonalCompanion.mutableLastPage.value.let {
                    androidx.compose.animation.AnimatedVisibility(it == NavigationRoutes.FRAG_SEASONAL_AGENDA, enter = slideInHorizontally() + fadeIn(), exit = slideOutHorizontally() + fadeOut()) {
                        FragmentSeasonalAgenda(current).getComposable()
                    }
                    androidx.compose.animation.AnimatedVisibility(it == NavigationRoutes.FRAG_SEASONAL_BOOK, enter = slideInHorizontally() + fadeIn(), exit = slideOutHorizontally() + fadeOut()) {
                        FragmentSeasonalBook(current).getComposable()
                    }
                    androidx.compose.animation.AnimatedVisibility(it == NavigationRoutes.FRAG_SEASONAL_GALLERY, enter = slideInHorizontally() + fadeIn(), exit = slideOutHorizontally() + fadeOut()) {
                        FragmentSeasonalGaleri(current).getComposable()
                    }
                    androidx.compose.animation.AnimatedVisibility(it == NavigationRoutes.FRAG_SEASONAL_MAIN, enter = slideInHorizontally() + fadeIn(), exit = slideOutHorizontally() + fadeOut()) {
                        FragmentSeasonalMain(current).getComposable()
                    }
                    androidx.compose.animation.AnimatedVisibility(it == NavigationRoutes.FRAG_SEASONAL_TWIBBON, enter = slideInHorizontally() + fadeIn(), exit = slideOutHorizontally() + fadeOut()) {
                        FragmentSeasonalTwibbon(current).getComposable()
                    }
                }
            }

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
                    seasonalData.getString("title"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                IconButton(onClick = { handleBackPress() }) {
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

    private fun handleBackPress() {
        if (ScreenSeasonalCompanion.mutableLastPage.value == ScreenSeasonalCompanion.DEFAULT_MAIN_FRAGMENT) AppNavigation.popBack()
        else ScreenSeasonalCompanion.mutableLastPage.value = ScreenSeasonalCompanion.DEFAULT_MAIN_FRAGMENT
    }
}

class ScreenSeasonalCompanion : Application() {
    companion object {
        val DEFAULT_MAIN_FRAGMENT = NavigationRoutes.FRAG_SEASONAL_MAIN

        /* The last visited pager page (representing fragment) in ScreenMain. */
        var mutableLastPage = mutableStateOf(DEFAULT_MAIN_FRAGMENT)

        /* The screen's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}