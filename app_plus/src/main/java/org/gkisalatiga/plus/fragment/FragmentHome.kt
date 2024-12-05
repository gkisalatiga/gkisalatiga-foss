/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.app.Application
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
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
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.StringFormatter
import org.gkisalatiga.plus.screen.ScreenPosterViewerCompanion
import org.gkisalatiga.plus.screen.ScreenWebViewCompanion
import kotlin.math.ceil

class FragmentHome (private val current : ActivityData) : ComponentActivity() {

    private val ctx = current.ctx

    // The following defines the visible menu buttons shown in the main menu,
    // as well as their corresponding navigation targets.
    private val btnRoutes = listOf(
        NavigationRoutes.SCREEN_WARTA,
        NavigationRoutes.SCREEN_LITURGI,
        NavigationRoutes.SCREEN_AGENDA,
        NavigationRoutes.SCREEN_PERSEMBAHAN,
        NavigationRoutes.SCREEN_YKB,
        NavigationRoutes.SCREEN_FORMS,
        NavigationRoutes.SCREEN_GALERI,
        // TODO: Re-enable the bible and library menu once the functionalities are ready.
        // NavigationRoutes.SCREEN_BIBLE,
        NavigationRoutes.SCREEN_LIBRARY,
        NavigationRoutes.SCREEN_PUKAT_BERKAT,
    )

    // The following defines the label of each visible menu button.
    private lateinit var btnLabels: List<String>

    // The following defines each visible menu button's icon description.
    private lateinit var btnDescriptions: List<String>

    // The following defines the icons used for the visible menu buttons.
    private val btnIcons = listOf(
        R.drawable.ph__seal_question_bold,  // --- we don't need icon for this entry.
        R.drawable.ph__seal_question_bold,  // --- we don't need icon for this entry.
        R.drawable.ph__calendar_dots_bold,
        R.drawable.ph__hand_coins_bold,
        R.drawable.ph__book_open_text_bold,
        R.drawable.ph__paper_plane_tilt_bold,
        R.drawable.ph__images_square_bold,
        // TODO: Re-enable the bible and library menu once the functionalities are ready.
        // R.drawable.ph__cross_bold,
        R.drawable.ph__books_bold,
        R.drawable.ph__shopping_cart_bold,
    )

    // Toggle menu button enabled state.
    private val btnEnabledState = listOf(
        true,  // --- won't affect actual state.
        true,  // --- won't affect actual state.
        true,
        true,
        true,
        true,
        true,
        // TODO: Re-enable the bible and library menu once the functionalities are ready.
        // false,
        true,
        true,
    )

    @Composable
    fun getComposable() {

        /* Initialized the "lateinit" variables. */
        btnLabels = listOf(
            ctx.resources.getString(R.string.btn_mainmenu_wj),
            ctx.resources.getString(R.string.btn_mainmenu_liturgi),
            ctx.resources.getString(R.string.btn_mainmenu_agenda),
            ctx.resources.getString(R.string.btn_mainmenu_offertory),
            ctx.resources.getString(R.string.btn_mainmenu_ykb),
            ctx.resources.getString(R.string.btn_mainmenu_form),
            ctx.resources.getString(R.string.btn_mainmenu_gallery),
            // TODO: Re-enable the bible and library menu once the functionalities are ready.
            // ctx.resources.getString(R.string.btn_mainmenu_bible),
            ctx.resources.getString(R.string.btn_mainmenu_library),
            ctx.resources.getString(R.string.btn_mainmenu_pukatberkat),
        )
        btnDescriptions = listOf(
            ctx.resources.getString(R.string.btn_desc_mainmenu_wj),
            ctx.resources.getString(R.string.btn_desc_mainmenu_liturgi),
            ctx.resources.getString(R.string.btn_desc_mainmenu_agenda),
            ctx.resources.getString(R.string.btn_desc_mainmenu_offertory),
            ctx.resources.getString(R.string.btn_desc_mainmenu_ykb),
            ctx.resources.getString(R.string.btn_desc_mainmenu_form),
            ctx.resources.getString(R.string.btn_desc_mainmenu_gallery),
            ctx.resources.getString(R.string.btn_desc_mainmenu_media),
            // TODO: Re-enable the bible and library menu once the functionalities are ready.
            // ctx.resources.getString(R.string.btn_desc_mainmenu_bible),
            ctx.resources.getString(R.string.btn_desc_mainmenu_library),
            ctx.resources.getString(R.string.btn_desc_mainmenu_pukatberkat),
        )

        // The following defines each individual featured cover image of the menu.
        // (Only the top two menus are considered.)
        val btnFeaturedCover = listOf(
            R.drawable.menu_cover_wj,
            R.drawable.menu_cover_liturgi
        )

        // Get the number of carousel banners.
        val actualPageCount = MainCompanion.jsonRoot!!.getJSONArray("carousel").length()

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
                    beyondViewportPageCount = 5,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Navigate to the current iteration's JSON node.
                    // val currentNode = GlobalCompanion.carouselJSONObject[it % actualPageCount]
                    val currentNode = MainCompanion.jsonRoot!!.getJSONArray("carousel").getJSONObject(it % actualPageCount)

                    /* Display the carousel banner image. */
                    Surface (
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.padding(ctx.resources.getDimension(R.dimen.banner_inner_padding).dp).fillMaxWidth().aspectRatio(1.77778f),
                        onClick = {
                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(ctx, "You are clicking carousel banner no. ${it % actualPageCount}!", Toast.LENGTH_SHORT).show()

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
                                    ScreenPosterViewerCompanion.posterViewerTitle = currentNode.getString("title")
                                    ScreenPosterViewerCompanion.posterViewerCaption = currentNode.getString("poster-caption")
                                    ScreenPosterViewerCompanion.posterViewerImageSource = currentNode.getString("poster-image")
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
                            }
                        }
                    ) {
                        AsyncImage(
                            model = currentNode.getString("banner"),
                            contentDescription = "Carousel Image ${it % actualPageCount}",
                            error = painterResource(R.drawable.thumbnail_error_notext),
                            placeholder = painterResource(R.drawable.thumbnail_placeholder),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Create the pager indicator.
                // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
                Row(
                    modifier = Modifier.height(45.dp).fillMaxWidth().align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(actualPageCount) { iteration ->
                        val color = if (carouselPagerState.currentPage % actualPageCount == iteration) Color.White else Color.White.copy(alpha = 0.5f)

                        // The individual dot for indicating carousel page.
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(10.dp)
                        )
                    }
                }

            }

            Spacer(Modifier.fillMaxWidth().height(10.dp))

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
        }  // --- end of scrollable column.

    }  // --- end of getComposable().

}

class FragmentHomeCompanion : Application() {
    companion object {
        /* The fragment's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null

        /* The carousel's remembered pager state. */
        var rememberedCarouselPagerState: PagerState? = null
    }
}