/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.app.Application
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.YouTubeViewCompanion
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.data.MainCarouselItemObject
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.screen.ScreenPosterViewerLegacyCompanion
import org.gkisalatiga.plus.screen.ScreenWebViewCompanion
import org.json.JSONObject
import kotlin.math.ceil

class FragmentHome (private val current : ActivityData) : ComponentActivity() {

    // The following defines the visible menu buttons shown in the main menu,
    // as well as their corresponding navigation targets.
    private lateinit var btnRoutes: MutableList<NavigationRoutes>

    // The following defines the label of each visible menu button.
    private lateinit var btnLabels: MutableList<String>

    // The following defines each visible menu button's icon description.
    private lateinit var btnDescriptions: MutableList<String>

    // The following defines the icons used for the visible menu buttons.
    private lateinit var btnIcons: MutableList<Int>

    // Toggle menu button enabled state.
    private lateinit var btnEnabledState: MutableList<Boolean>

    @Composable
    fun getComposable() {

        /* Initialized the "late init" variables for the top two features: Warta Jemaat & Liturgi. */
        btnRoutes = mutableListOf(NavigationRoutes.SCREEN_WARTA, NavigationRoutes.SCREEN_LITURGI)
        btnLabels = mutableListOf(current.ctx.resources.getString(R.string.btn_mainmenu_wj), current.ctx.resources.getString(R.string.btn_mainmenu_liturgi))
        btnDescriptions = mutableListOf(current.ctx.resources.getString(R.string.btn_desc_mainmenu_wj), current.ctx.resources.getString(R.string.btn_desc_mainmenu_liturgi))
        btnIcons = mutableListOf(R.drawable.ph__seal_question_bold, R.drawable.ph__seal_question_bold)
        btnEnabledState = mutableListOf(true, true)

        /* Show or hide feature menus based on flag settings. */
        val appFlags = MainCompanion.api!!.backend.flags

        if (appFlags.isFeatureAgendaShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_AGENDA)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_agenda))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_agenda))
            btnIcons.add(R.drawable.ph__calendar_dots_bold)
            btnEnabledState.add(true)
        }

        if (appFlags.isFeaturePersembahanShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_PERSEMBAHAN)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_offertory))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_offertory))
            btnIcons.add(R.drawable.ph__hand_coins_bold)
            btnEnabledState.add(true)
        }

        if (appFlags.isFeatureYKBShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_YKB)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_ykb))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_ykb))
            btnIcons.add(R.drawable.ph__book_open_text_bold)
            btnEnabledState.add(true)
        }

        if (appFlags.isFeatureFormulirShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_FORMS)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_form))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_form))
            btnIcons.add(R.drawable.ph__paper_plane_tilt_bold)
            btnEnabledState.add(true)
        }

        if (appFlags.isFeatureGaleriShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_GALERI)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_gallery))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_gallery))
            btnIcons.add(R.drawable.ph__images_square_bold)
            btnEnabledState.add(true)
        }

        // TODO: Remove the "GlobalCompanion.isAppDebuggable.value" expression.
        // if (appFlags.isFeatureBibleShown == 1 || GlobalCompanion.isAppDebuggable.value) {
        if (appFlags.isFeatureBibleShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_BIBLE)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_bible))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_bible))
            btnIcons.add(R.drawable.ph__cross_bold)
            btnEnabledState.add(true)
        }

        if (appFlags.isFeatureLibraryShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_LIBRARY)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_library))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_library))
            btnIcons.add(R.drawable.ph__books_bold)
            btnEnabledState.add(true)
        }

        if (appFlags.isFeatureLapakShown == 1) {
            btnRoutes.add(NavigationRoutes.SCREEN_PUKAT_BERKAT)
            btnLabels.add(current.ctx.resources.getString(R.string.btn_mainmenu_pukatberkat))
            btnDescriptions.add(current.ctx.resources.getString(R.string.btn_desc_mainmenu_pukatberkat))
            btnIcons.add(R.drawable.ph__shopping_cart_bold)
            btnEnabledState.add(true)
        }

        /* The following defines each individual featured cover image of the menu. */
        // (Only the top two menus are considered.)
        val btnFeaturedCover = listOf(
            R.drawable.menu_cover_wj,
            R.drawable.menu_cover_liturgi
        )

        /* Enlist the carousels. */
        val allCarousels = mutableListOf<MainCarouselItemObject>()
        // MainCompanion.jsonRoot!!.getJSONArray("carousel").let { for (i in 0 until it.length()) allCarousels.add(it.getJSONObject(i)) }
        MainCompanion.api!!.carousel.let { for (i in 0 until it.size) allCarousels.add(it[i]) }
        FragmentHomeCompanion.filteredCarouselPostersList = allCarousels.filter { jO -> jO.type == "poster" }

        /* Filter out non-poster carousels. */
        val filteredCarousels = FragmentHomeCompanion.filteredCarouselPostersList!!

        // Get the number of carousel banners.
        val actualPageCount = filteredCarousels.size

        // Retrieving the global state.
        val carouselPagerState = FragmentHomeCompanion.rememberedCarouselPagerState!!

        /* Set-up the launched effect for auto-scrolling the horizontal carousel/pager. */
        // SOURCE: https://stackoverflow.com/a/67615616
        LaunchedEffect(carouselPagerState.settledPage) {
            launch {
                delay(2500)
                with(carouselPagerState) {
                    animateScrollToPage(
                        page = currentPage + 1,
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                }
            }
        }  // --- end of launched effect.

        /* --------------------------------------------------------------- */

        // Setting the layout to center both vertically and horizontally
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = FragmentHomeCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {

            /* Show the "infinite" horizontal carousel for CTA. */
            // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
            // SOURCE: https://stackoverflow.com/a/75469260
            // ---
            // Create the box boundary.
            Box (modifier = Modifier.fillMaxWidth().aspectRatio(1.77778f)) {

                /* Create the horizontal pager "carousel" */
                HorizontalPager(
                    state = carouselPagerState,
                    beyondViewportPageCount = 7,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Navigate to the current iteration's JSON node.
                    // val currentNode = GlobalCompanion.carouselJSONObject[it % actualPageCount]
                    // val currentNode = MainCompanion.jsonRoot!!.getJSONArray("carousel").getJSONObject(it % actualPageCount)
                    // ---
                    // Only show poster carousels.
                    val currentNode = filteredCarousels[it % actualPageCount]

                    /* Display the carousel banner image. */
                    Surface (
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.padding(current.ctx.resources.getDimension(R.dimen.banner_inner_padding).dp).fillMaxWidth().aspectRatio(1.77778f),
                        onClick = {
                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "You are clicking carousel banner no. ${it % actualPageCount}!", Toast.LENGTH_SHORT).show()
                            AppNavigation.navigate(NavigationRoutes.SCREEN_POSTER_VIEWER)
                            /* TODO: Remove this deprecated block after a major release.
                            // Get the type of the current carousel banner.
                            val currentType = currentNode.getString("type")

                            /* Switch to a different screen or run a certain action based on the carousel banner type. */
                            when (currentType) {
                                "article" -> {
                                    // Preparing the WebView arguments.
                                    val url = currentNode.getString("article-url")
                                    val title = currentNode.getString("title")

                                    // Navigate to the WebView viewer.
                                    ScreenWebViewCompanion.putArguments(url, title)
                                    AppNavigation.navigate(NavigationRoutes.SCREEN_WEBVIEW)
                                }
                                "poster" -> {
                                    ScreenPosterViewerLegacyCompanion.posterViewerTitle = currentNode.getString("title")
                                    ScreenPosterViewerLegacyCompanion.posterViewerCaption = currentNode.getString("poster-caption")
                                    ScreenPosterViewerLegacyCompanion.posterViewerImageSource = currentNode.getString("poster-image")
                                    AppNavigation.navigate(NavigationRoutes.SCREEN_POSTER_VIEWER)
                                }
                                "yt" -> {
                                    // Preparing the YouTube player arguments.
                                    val url = currentNode.getString("yt-link")
                                    val title = currentNode.getString("yt-title")
                                    val date = currentNode.getString("yt-date")
                                    val desc = currentNode.getString("yt-desc")

                                    // Trying to switch to the YouTube viewer and open the stream.
                                    Logger.log({}, "Opening the YouTube stream: $url.")
                                    YouTubeViewCompanion.seekToZero()
                                    YouTubeViewCompanion.putArguments(
                                        date = StringFormatter.convertDateFromJSON(date),
                                        desc = desc,
                                        thumbnail = StringFormatter.getYouTubeThumbnailFromUrl(url),
                                        title = title,
                                        yt_id = StringFormatter.getYouTubeIDFromUrl(url),
                                        yt_link = url
                                    )
                                    AppNavigation.navigate(NavigationRoutes.SCREEN_LIVE)
                                }
                            }*/
                        }
                    ) {
                        AsyncImage(
                            model = currentNode.banner,
                            contentDescription = "Carousel Image ${it % actualPageCount}",
                            error = painterResource(R.drawable.thumbnail_error_notext),
                            placeholder = painterResource(R.drawable.thumbnail_placeholder),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Create the pager indicator.
            // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
            Row(
                modifier = Modifier.height(10.dp).fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(actualPageCount) { iteration ->
                    val color = if (carouselPagerState.currentPage % actualPageCount == iteration)
                        current.colors.fragmentHomeCarouselPageIndicatorActiveColor else current.colors.fragmentHomeCarouselPageIndicatorInactiveColor
                    val lineWeight = animateFloatAsState(
                        targetValue = if (carouselPagerState.currentPage % actualPageCount == iteration) 1.5f else 0.5f,
                        label = "weight",
                        animationSpec = tween(300, easing = EaseInOut)
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(color)
                            .weight(lineWeight.value)
                            .height(4.dp)
                    )
                }
            }

            Spacer(Modifier.fillMaxWidth().height(10.dp))

            // The seasonal JSON node.
            // val seasonalData = ModulesCompanion.jsonRoot!!.getJSONObject("seasonal")
            val seasonalData = ModulesCompanion.api!!.seasonal

            // Displaying the seasonal menu button.
            if (appFlags.isFeatureSeasonalShown == 1) {
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.padding(horizontal = current.ctx.resources.getDimension(R.dimen.banner_inner_padding).dp).padding(top = current.ctx.resources.getDimension(R.dimen.banner_inner_padding).dp).fillMaxWidth().aspectRatio(4.0f),
                    onClick = { AppNavigation.navigate(NavigationRoutes.SCREEN_SEASONAL) }
                ) {
                    AsyncImage(
                        model = seasonalData.bannerFront,
                        contentDescription = "Seasonal main/front banner",
                        error = painterResource(R.drawable.thumbnail_error_notext),
                        placeholder = painterResource(R.drawable.thumbnail_placeholder),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.fillMaxWidth().height(5.dp))
            HorizontalDivider(Modifier.padding(vertical = 20.dp).padding(horizontal = current.ctx.resources.getDimension(R.dimen.banner_inner_padding).dp))

            /* Displaying the top two menus. */
            Row {
                btnRoutes.subList(0, 2).forEachIndexed { index, _ ->
                    // The individual card item.
                    Card (
                        modifier = Modifier.padding(10.dp).fillMaxWidth().weight(1f),
                        colors = CardColors(
                            containerColor = Colors.FRAGMENT_HOME_TOP_TWO_MENUS_CONTAINER_COLOR,
                            contentColor = Colors.FRAGMENT_HOME_TOP_MENU_TINT_COLOR,
                            disabledContainerColor = Color.Unspecified,
                            disabledContentColor = Color.Unspecified
                        ),
                        onClick = {
                            // This will be triggered when the main menu button is clicked.
                            if (btnRoutes[index] != NavigationRoutes.SCREEN_BLANK) {
                                AppNavigation.navigate(btnRoutes[index])
                            }
                        }
                    ) {
                        Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp), modifier = Modifier.fillMaxWidth().aspectRatio(0.75f)) {
                                Image(painter = painterResource(btnFeaturedCover[index]), "Top Menu Banner", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                            }
                            Text(btnLabels[index], textAlign = TextAlign.Center, modifier = Modifier.padding(5.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.fillMaxWidth().height(10.dp))

            // The modifier that applies to both the actual buttons and the spacers.
            // val buttonSpacerModifier = Modifier.weight(1f).padding(5.dp).height(125.dp)
            val buttonSpacerModifier = Modifier.weight(1f).padding(5.dp).aspectRatio(0.88888f)

            // The menu array after "popping" the first two elements.
            val subArray = btnRoutes.subList(2, btnRoutes.size)

            /* Displaying the main menu action buttons other than the first two. */
            // Assumes btnRoutes, btnLabels, and btnIcons all have the same size.
            val columns = 3
            val rows = ceil((subArray.size / columns).toDouble()).toInt()

            var index = 0
            for (j in 0 .. rows) {

                // Ensures that we don't draw an empty row when there is no data.
                if (index == subArray.size) break

                Row {
                    while (index < subArray.size) {
                        val offsetIndex = index + 2

                        // Displaying the menu button.
                        Button (
                            onClick = {
                                // This will be triggered when the main menu button is clicked.
                                if (btnRoutes[offsetIndex] != NavigationRoutes.SCREEN_BLANK) {
                                    AppNavigation.navigate(btnRoutes[offsetIndex])
                                }
                            },
                            modifier = buttonSpacerModifier,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(5.dp),
                            enabled = btnEnabledState[offsetIndex]
                        ) {
                            // The main menu element wrapper.
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()) {
                                // The main menu action button icon.
                                Image(
                                    painter = painterResource(btnIcons[offsetIndex]),
                                    contentDescription = btnDescriptions[offsetIndex],
                                    colorFilter = ColorFilter.tint(Colors.FRAGMENT_HOME_TOP_MENU_TINT_COLOR),
                                    modifier = Modifier.fillMaxHeight(0.50f).aspectRatio(1.0f)
                                )
                                // Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                // Spacer(Modifier.fillMaxHeight(0.10f))
                                // The text.
                                Text(
                                    btnLabels[offsetIndex],
                                    textAlign = TextAlign.Center,
                                    minLines = 1,
                                    maxLines = 2,
                                    overflow = TextOverflow.Visible,
                                    softWrap = true,
                                    fontSize = 12.sp,
                                    color = Colors.FRAGMENT_HOME_TOP_MENU_TINT_COLOR,
                                    modifier = Modifier
                                        // .fillMaxHeight(0.50f)
                                        .padding(3.dp)
                                )
                            }
                        }

                        // Ensures that we have the right amount of columns.
                        index += 1
                        if (index % columns == 0) break
                    }

                    // Add spacer for non-even button rows. (Visual improvement.)
                    // Only applies to the last row.
                    if (j == rows) {
                        repeat(columns - (subArray.size % columns)) {
                            Spacer(buttonSpacerModifier)
                        }
                    }

                }
            }  // --- end of for loop.

            HorizontalDivider(Modifier.padding(vertical = 20.dp))

            // App update prompt.
            val isAppUpdateAvailable = GlobalCompanion.isAppUpdateAvailable.value
            val titleCardUpdate = stringResource(R.string.titlecard_update)
            val subTextUpdate =
                if (isAppUpdateAvailable)
                    stringResource(R.string.subtext_update_available)
                        .replace("%%%VERSION%%%", GlobalCompanion.lastAppUpdateVersionName.value)
                else
                    stringResource(R.string.subtext_update_latest)
            Card(
                onClick = {
                    // val url = MainCompanion.jsonRoot!!.getJSONObject("backend").getJSONObject("strings").getString("about_google_play_listing_url")
                    val url = MainCompanion.api!!.backend.strings.aboutGooglePlayListingUrl
                    current.uriHandler.openUri(url)
                },
                enabled = isAppUpdateAvailable,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Row ( modifier = Modifier.fillMaxSize().padding(12.5.dp), verticalAlignment = Alignment.CenterVertically ) {
                    Icon(Icons.Default.Update, "Notification icon (decorative)", modifier = Modifier.weight(0.8f).fillMaxHeight(1.0f).aspectRatio(1.0f).padding(5.dp).padding(start = 5.dp))
                    Column(modifier = Modifier.weight(5.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Center) {
                        Text(titleCardUpdate, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(subTextUpdate, fontSize = 14.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Justify, modifier = Modifier.padding(top = 2.dp), lineHeight = 16.sp)
                    }
                }
            }
            
            // App notification permission prompt.
            val isNotificationGranted = GlobalCompanion.isNotificationGranted.value
            val titleCardNotification = stringResource(R.string.titlecard_notification)
            val subTextNotification = if (isNotificationGranted) stringResource(R.string.subtext_notification_granted) else stringResource(R.string.subtext_notification_denied)
            Card(
                onClick = {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, current.ctx.packageName)
                    current.ctx.startActivity(intent)
                },
                enabled = !isNotificationGranted,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Row ( modifier = Modifier.fillMaxSize().padding(12.5.dp), verticalAlignment = Alignment.CenterVertically ) {
                    Icon(Icons.Default.NotificationsActive, "Notification icon (decorative)", modifier = Modifier.weight(0.8f).fillMaxHeight(1.0f).aspectRatio(1.0f).padding(5.dp).padding(start = 5.dp))
                    Column(modifier = Modifier.weight(5.0f).padding(start = 10.dp), verticalArrangement = Arrangement.Center) {
                        Text(titleCardNotification, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(subTextNotification, fontSize = 14.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Justify, modifier = Modifier.padding(top = 2.dp), lineHeight = 16.sp)
                    }
                }
            }
            
        }  // --- end of scrollable column.

    }  // --- end of getComposable().

}

class FragmentHomeCompanion : Application() {
    companion object {
        /* The fragment's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* The carousel's remembered pager state. */
        var rememberedCarouselPagerState: PagerState? = null

        /* The list of filtered carousel images. */
        var filteredCarouselPostersList: List<MainCarouselItemObject>? = mutableListOf()
    }
}