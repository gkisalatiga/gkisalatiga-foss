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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.TopAppBarColorScheme
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.json.JSONObject

class ScreenPukatBerkat : ComponentActivity() {

    // The pager state.
    private val horizontalPagerState = ScreenPukatBerkatCompanion.pukatBerkatPagerState!!

    // The currently selected tab index.
    private val selectedTabIndex = ScreenPukatBerkatCompanion.currentHorizontalPagerValue

    // The coroutine scope.
    private lateinit var scope: CoroutineScope

    // The horizontal pager tab selections.
    private val sectionTabName = listOf(
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
        "goods",
        "service"
    )

    @Composable
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    fun getComposable() {
        // Initializing pre-init variables.
        scope = rememberCoroutineScope()

        Scaffold (topBar = { getTopBar() }) {
            Box ( Modifier.fillMaxSize().padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding()) ) {
                getMainContent()
            }
        }  // --- end of Scaffold.

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
    private fun getMainContent() {

        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier.fillMaxSize().padding(top = 0.dp),
            // Without this property, the left-right page scrolling would be insanely laggy!
            beyondViewportPageCount = 2
        ) {
            getSectionUI(pukatBerkatDictIndices[it], it)
        }

    }

    @Composable
    private fun getSectionUI(dictIndex: String, intIndex: Int) {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current

        /* Converting JSONArray to regular list. */
        val pukatBerkatListAsJSONArray = MainCompanion.jsonRoot!!.getJSONArray("pukat-berkat")
        val enumeratedPukatBerkatList: MutableList<Map<String, Any>> =  mutableListOf(emptyMap<String, Any>())
        for (i in 0 until pukatBerkatListAsJSONArray.length()) {
            val curNode = pukatBerkatListAsJSONArray[i] as JSONObject
            enumeratedPukatBerkatList.add(mapOf(
                "title" to curNode.getString("title"),
                "desc" to curNode.getString("desc"),
                "price" to curNode.getString("price"),
                "contact" to curNode.getString("contact"),
                "vendor" to curNode.getString("vendor"),
                "type" to curNode.getString("type"),
                "image" to curNode.getString("image"),
            ))
        }

        // For some reason, we must pop the 0-th item in cardsList
        // because JSONArray iterates from 1, not 0.
        enumeratedPukatBerkatList.removeAt(0)

        /* Save the scroll state */
        val sectionScrollState = when (dictIndex) {
            "food" -> ScreenPukatBerkatCompanion.rememberedScrollStateFood!!
            "goods" -> ScreenPukatBerkatCompanion.rememberedScrollStateGoods!!
            "service" -> ScreenPukatBerkatCompanion.rememberedScrollStateService!!
            else -> rememberScrollState()
        }

        /* Draw the UI. */
        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(sectionScrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            /* Iterating through every Pukat Berkat item. */
            for (i in 0 until enumeratedPukatBerkatList.size) {
                val curList = enumeratedPukatBerkatList[i]
                val curListSize = enumeratedPukatBerkatList.size

                // Debug test.
                // Logger.logTest({}, "Testing of enumeratedPukatBerkatList -> [curListSize] $curListSize ::  [curList] $curList")

                // Preparing the arguments.
                val title = curList["title"] as String
                val desc = curList["desc"] as String
                val price = curList["price"] as String
                val type = curList["type"] as String
                val thumbnailImage = curList["image"] as String
                val vendor = curList["vendor"] as String
                val contactMessage = stringResource(R.string.pukatberkat_whatsapp_text_template)
                    .replace("%%%TYPE%%%", stringResource(sectionTabName[intIndex]).lowercase())
                    .replace("%%%NAME%%%", title)
                    .replace("%%%VENDOR%%%", vendor)
                val contactURL = StringFormatter.getWhatsAppPrivateChatLink(
                    curList["contact"] as String,
                    contactMessage
                )

                // Determine whether to display the card based on the filter criterion.
                if (type != pukatBerkatDictIndices[intIndex]) continue

                // Displaying the individual card.
                Card(
                    onClick = {
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $title!", Toast.LENGTH_SHORT).show()

                        // Set the PosterViewer parameters.
                        ScreenPosterViewerCompanion.posterViewerTitle = title
                        ScreenPosterViewerCompanion.posterViewerCaption = desc
                        ScreenPosterViewerCompanion.posterViewerImageSource = thumbnailImage

                        // Navigate to the screen.
                        AppNavigation.navigate(NavigationRoutes.SCREEN_POSTER_VIEWER)
                    },
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    Column ( modifier = Modifier.fillMaxWidth().padding(10.dp).padding(top = 5.dp), verticalArrangement = Arrangement.Center ) {
                        Row {
                            // The item thumbnail.
                            Surface (shape = RoundedCornerShape(10.dp), modifier = Modifier.weight(1.0f).fillMaxHeight().padding(start = 5.dp)) {
                                AsyncImage(
                                    thumbnailImage,
                                    contentDescription = "Pukat Berkat: $title",
                                    error = painterResource(R.drawable.thumbnail_error_stretched),
                                    placeholder = painterResource(R.drawable.thumbnail_placeholder),
                                    modifier = Modifier.aspectRatio(1f).width(12.5.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Column(modifier = Modifier.weight(5.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Center) {
                                // The post title.
                                Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                // The item price.
                                Text(price, fontSize = 18.sp, fontWeight = FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        // The item description.
                        Text(desc, fontSize = 18.sp, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Normal, maxLines = 15, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp))
                        // The WhatsApp action button.
                        TextButton(
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(Colors.SCREEN_YKB_ARCHIVE_BUTTON_COLOR)
                            ),
                            onClick = {
                                if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You just clicked: $contactURL!", Toast.LENGTH_SHORT).show()

                                // Opens in an external browser.
                                // SOURCE: https://stackoverflow.com/a/69103918
                                uriHandler.openUri(contactURL)
                            }
                        ) {
                            Row (verticalAlignment = Alignment.CenterVertically) {
                                Text(vendor.uppercase())
                                Icon(Icons.AutoMirrored.Default.ArrowRight, "", modifier = Modifier.padding(start = 5.dp))
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

        Column(modifier = Modifier.fillMaxWidth()) {
            /* The navigational topBar. */
            CenterAlignedTopAppBar(
                colors = TopAppBarColorScheme.default(),
                title = {
                    Text(
                        stringResource(R.string.screenpukatberkat_title),
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
                actions = { }
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
        /* Saves the scrolling state of each Pukat Berkat tab menu. */
        var rememberedScrollStateFood: ScrollState? = null
        var rememberedScrollStateGoods: ScrollState? = null
        var rememberedScrollStateService: ScrollState? = null

        /* Saves information about the currently/last selected Pukat Berkat tab. */
        val currentHorizontalPagerValue = mutableIntStateOf(0)
        var pukatBerkatPagerState: PagerState? = null
    }
}