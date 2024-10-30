/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * The main menu of the GKI Salatiga Plus app.
 * This sets up the Scaffolding of both top and bottom navigations,
 * as well as the menu feature select screen.
 *
 * ---
 *
 * REFERENCES USED:
 *
 * Using strings.xml in a Composable function
 * SOURCE: https://stackoverflow.com/a/65889036
 */

package org.gkisalatiga.plus.screen

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.fragment.FragmentHome
import org.gkisalatiga.plus.fragment.FragmentInfo
import org.gkisalatiga.plus.fragment.FragmentServices
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.services.DataUpdater
import org.gkisalatiga.plus.ui.theme.Brown1
import java.util.concurrent.TimeUnit

class ScreenMain : ComponentActivity() {

    // For "double-press back" to exit.
    private var backPressedTime: Long = 0

    // Enlists all of the fragments that will be displayed in this particular screen.
    private val fragRoutes = listOf(
        NavigationRoutes.FRAG_MAIN_HOME,
        NavigationRoutes.FRAG_MAIN_SERVICES,
        NavigationRoutes.FRAG_MAIN_INFO,
    )

    // The calculated status bar's height, for determining the "top bar"'s top padding. (Also the bottom nav bar.)
    private var calculatedTopPadding = 0.dp
    private var calculatedBottomPadding = 0.dp

    // Used by the bottom nav to command the scrolling of the horizontal pager.
    private var bottomNavPagerScrollTo = mutableIntStateOf(fragRoutes.indexOf(ScreenMainCompanion.mutableLastPagerPage.value))

    // Determines the top banner title.
    private var topBannerTitle = ""

    // Controls the horizontal scrolling of the pager.
    private lateinit var horizontalPagerState: PagerState

    // Determines what background to show on the new top bar layout by user github.com/ujepx64.
    private var newTopBannerBackground = R.drawable.topbar_greetings_background

    // The coroutine scope.
    private lateinit var scope: CoroutineScope

    // The snackbar host state.
    private val snackbarHostState = GlobalCompanion.snackbarHostState

    @Composable
    @ExperimentalMaterial3Api
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UseOfNonLambdaOffsetOverload")
    fun getComposable() {
        val ctx = LocalContext.current
        val uriHandler = LocalUriHandler.current
        scope = rememberCoroutineScope()

        // Initializing the top banner title.
        topBannerTitle = ctx.resources.getString(R.string.app_name_alias)

        // Initializing the horizontal pager.
        horizontalPagerState = rememberPagerState ( pageCount = {fragRoutes.size}, initialPage = fragRoutes.indexOf(ScreenMainCompanion.mutableLastPagerPage.value) )

        // Connects the horizontal pager with the bottom bar.
        LaunchedEffect(horizontalPagerState.targetPage) {
            ScreenMainCompanion.mutableLastPagerPage.value = fragRoutes[horizontalPagerState.targetPage]
        }

        // The pull-to-refresh indicator states.
        val isRefreshing = remember { GlobalCompanion.isPTRRefreshing }
        val pullToRefreshState = GlobalCompanion.globalPTRState
        val refreshExecutor = GlobalCompanion.PTRExecutor

        Scaffold (
            bottomBar = { getBottomBar() },
            topBar = {
                getTopBar()
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton =  { },
            floatingActionButtonPosition = FabPosition.Center,
            modifier = Modifier.pullToRefresh(isRefreshing.value, pullToRefreshState!!, onRefresh = {
                refreshExecutor.execute {
                    // Assumes there is an internet connection.
                    // (If there isn't, the boolean state change will trigger the snack bar.)
                    GlobalCompanion.isConnectedToInternet.value = true

                    // Attempts to update the data.
                    isRefreshing.value = true
                    DataUpdater(ctx).updateData()
                    TimeUnit.SECONDS.sleep(5)
                    isRefreshing.value = false

                    // Update/recompose the UI.
                    AppNavigation.mutableRecomposeCurrentScreen.value = !AppNavigation.mutableRecomposeCurrentScreen.value
                }
            })
        ) {
            calculatedTopPadding = it.calculateTopPadding()
            calculatedBottomPadding = it.calculateBottomPadding()

            // Expose the value.
            ScreenMainCompanion.calculatedTopBarPadding = calculatedTopPadding

            // Setting up the layout of all of the fragments.
            // Then wrap each fragment in AnimatedVisibility so that we can manually control their visibility.
            Box (Modifier.padding(bottom = calculatedBottomPadding)) {

                // Using nested scroll to handle mutliple scrolling surfaces.
                // SOURCE: https://medium.com/androiddevelopers/understanding-nested-scrolling-in-jetpack-compose-eb57c1ea0af0
                // SOURCE: https://developer.android.com/develop/ui/compose/touch-input/pointer-input/scroll#nested-scrolling
                val minContentOffset = ScreenMainCompanion.MIN_SCREEN_MAIN_TOP_OFFSET + ScreenMainCompanion.calculatedTopBarPadding.value
                val maxContentOffset = ScreenMainCompanion.MAX_SCREEN_MAIN_TOP_OFFSET
                val minImageOffset = ScreenMainCompanion.MIN_SCREEN_MAIN_WELCOME_IMAGE_TOP_OFFSET + (ScreenMainCompanion.calculatedTopBarPadding.value/2)
                val maxImageOffset = ScreenMainCompanion.MAX_SCREEN_MAIN_WELCOME_IMAGE_TOP_OFFSET
                val nestedScrollConnection = remember {
                    object : NestedScrollConnection {
                        override fun onPreScroll(
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            val delta = available.y

                            // Determines if the delta is positive (scroll up, delta direction is downward) or negative (scroll down, delta direction is upward).
                            val isDeltaNegative = if (delta < 0) true else false

                            // Calculating the top offset of the main content.
                            val currentContentOffset = ScreenMainCompanion.mutableScreenMainContentTopOffset.floatValue
                            val targetContentOffset = (currentContentOffset + delta / 2).coerceIn(minContentOffset, maxContentOffset)

                            // Applying the main content's top offset.
                            if (isDeltaNegative) ScreenMainCompanion.mutableScreenMainContentTopOffset.floatValue = targetContentOffset

                            // Calculating the top offset of the welcome image.
                            val currentImageOffset = ScreenMainCompanion.mutableScreenMainWelcomeImageTopOffset.floatValue
                            val targetImageOffset = (currentImageOffset + delta / 4).coerceIn(minImageOffset, maxImageOffset)

                            // Applying the welcome image's top offset.
                            if (isDeltaNegative) ScreenMainCompanion.mutableScreenMainWelcomeImageTopOffset.floatValue = targetImageOffset

                            // Determining how much delta should be spared to be consumed by the fragment's scrollable.
                            val returnDelta =
                                if (isDeltaNegative && currentContentOffset == minContentOffset) 0.0f
                                else if (!isDeltaNegative) 0.0f
                                else delta

                            // Debugging the output values.
                            Logger.logRapidTest({}, "[PreScroll] delta: $delta, currentOffset: $currentContentOffset, minOffset: $minContentOffset, maxOffset: $maxContentOffset, targetOffset: $targetContentOffset, returnDelta: $returnDelta", LoggerType.VERBOSE)

                            // Give out the delta to the fragment's scrollable.
                            return Offset(0.0f, returnDelta)
                        }

                        override fun onPostScroll(
                            consumed: Offset,
                            available: Offset,
                            source: NestedScrollSource
                        ): Offset {
                            val delta = available.y

                            // Determines if the delta is positive (scroll up, delta direction is downward) or negative (scroll down, delta direction is upward).
                            val isDeltaNegative = if (delta < 0) true else false

                            // Calculating the top offset of the main content.
                            val currentContentOffset = ScreenMainCompanion.mutableScreenMainContentTopOffset.floatValue
                            val targetContentOffset = (currentContentOffset + delta / 2).coerceIn(minContentOffset, maxContentOffset)

                            // Applying the main content's top offset.
                            if (!isDeltaNegative) ScreenMainCompanion.mutableScreenMainContentTopOffset.floatValue = targetContentOffset

                            // Calculating the top offset of the welcome image.
                            val currentImageOffset = ScreenMainCompanion.mutableScreenMainWelcomeImageTopOffset.floatValue
                            val targetImageOffset = (currentImageOffset + delta / 4).coerceIn(minImageOffset, maxImageOffset)

                            // Applying the welcome image's top offset.
                            if (!isDeltaNegative) ScreenMainCompanion.mutableScreenMainWelcomeImageTopOffset.floatValue = targetImageOffset

                            val returnDelta =
                                if (!isDeltaNegative && currentContentOffset == maxContentOffset) 0.0f
                                else if (isDeltaNegative) 0.0f
                                else delta

                            // Changing the top bar's transparency.
                            val targetTopBarTransparency = 1 - ((currentContentOffset - ScreenMainCompanion.calculatedTopBarPadding.value) / ( ScreenMainCompanion.MAX_SCREEN_MAIN_TOP_OFFSET - ScreenMainCompanion.calculatedTopBarPadding.value))
                            ScreenMainCompanion.mutableTopBarContainerTransparency.floatValue = targetTopBarTransparency

                            // Debugging the output values.
                            Logger.logRapidTest({}, "[Post-Scroll] y-consumed: ${consumed.y}, y-available: ${available.y}", LoggerType.VERBOSE)

                            // Give out the delta to the fragment's scrollable.
                            return Offset(0.0f, returnDelta)
                        }
                    }
                }

                // This box handles mouse input so that the fragment can be scrolled down,
                // covering the new "top bar" when the user swipes up.
                // SOURCE: https://developer.android.com/develop/ui/compose/touch-input/pointer-input/drag-swipe-fling
                Box (
                    modifier = Modifier
                        // Using nested scroll to handle mutliple scrolling surfaces.
                        // SOURCE: https://medium.com/androiddevelopers/understanding-nested-scrolling-in-jetpack-compose-eb57c1ea0af0
                        .nestedScroll(nestedScrollConnection)
                ) {
                    // Shows the new top bar.
                    // Wrap in a LazyColumn so that scrolling events in this element will get caught by the nestedScrollConnection.
                    LazyColumn (userScrollEnabled = true) {
                        item { getTopBanner() }
                    }

                    // Shows the main content.
                    Surface (
                        modifier = Modifier.offset(y = (ScreenMainCompanion.mutableScreenMainContentTopOffset.floatValue).dp).fillMaxSize().zIndex(10f),
                        // modifier = Modifier.padding(top = LocalContext.current.resources.getDimension(R.dimen.new_topbar_content_top_y_offset).dp).fillMaxSize().zIndex(10f),
                        shape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp)
                    ) {
                        // Enabling pager for managing and layouting multiple fragments in a given screen.
                        // SOURCE: https://www.composables.com/foundation/horizontalpager
                        HorizontalPager(
                            state = horizontalPagerState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 0.dp)
                                // This needs to be set as a "side effect" of setting a non-zero value to "minScreenMainTopOffset"
                                .padding(bottom = ScreenMainCompanion.MIN_SCREEN_MAIN_TOP_OFFSET.dp + ScreenMainCompanion.calculatedTopBarPadding),
                            // Without this property, the left-right page scrolling would be insanely laggy!
                            beyondViewportPageCount = 2
                        ) { page ->
                            when (page) {
                                0 -> FragmentHome().getComposable()
                                1 -> FragmentServices().getComposable()
                                2 -> FragmentInfo().getComposable()
                            }
                        }
                    }
                }  // --- end of box 2.
            }  // --- end of box 1.

            // Check whether we are connected to the internet.
            // Then notify user about this.
            val snackbarMessageString = stringResource(R.string.not_connected_to_internet)
            LaunchedEffect(GlobalCompanion.isConnectedToInternet.value) {
                if (!GlobalCompanion.isConnectedToInternet.value) scope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarMessageString,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            // Add pull-to-refresh mechanism for updating the content data.
            PullToRefreshBox(
                modifier = Modifier.fillMaxWidth().padding(top = it.calculateTopPadding()),
                isRefreshing = isRefreshing.value,
                state = pullToRefreshState,
                onRefresh = {},
                content = {},
                indicator = {
                    Indicator(
                        modifier = Modifier.align(Alignment.TopCenter),
                        isRefreshing = isRefreshing.value,
                        state = pullToRefreshState
                    )
                },
            )

            // Ensure that when we are at the first screen upon clicking "back",
            // the app is exited instead of continuing to navigate back to the previous screens.
            // SOURCE: https://stackoverflow.com/a/69151539
            val exitConfirm = stringResource(R.string.exit_confirmation_toast_string)
            val localContext = LocalContext.current
            BackHandler {
                val curRoute = ScreenMainCompanion.mutableLastPagerPage.value
                if (curRoute == NavigationRoutes.FRAG_MAIN_HOME) {

                    // Ensure "double tap the back button to exit".
                    if (backPressedTime + 2000 > System.currentTimeMillis()) {
                        // Exit the application.
                        // SOURCE: https://stackoverflow.com/a/67402808
                        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(localContext, "You just clicked $curRoute and exited the app!", Toast.LENGTH_SHORT).show()
                        (localContext as ComponentActivity).finish()
                    } else {
                        Toast.makeText(localContext, exitConfirm, Toast.LENGTH_LONG).show()
                    }

                    backPressedTime = System.currentTimeMillis()

                } else if (
                    curRoute == NavigationRoutes.FRAG_MAIN_INFO ||
                    curRoute == NavigationRoutes.FRAG_MAIN_SERVICES
                ) {
                    // Since we are in the main screen but not at fragment one,
                    // navigate the app to fragment one.
                    scope.launch { horizontalPagerState.animateScrollToPage(0) }
                } else {
                    // Do nothing.
                }
            }
        }

    }

    @Composable
    private fun getBottomBar() {

        // Defines the bottom nav tab names.
        val navItems = listOf(
            stringResource(R.string.bottomnav_menu_home),
            stringResource(R.string.bottomnav_menu_services),
            stringResource(R.string.bottomnav_menu_info),
        )

        // Enlists the bottom nav bar item icons. (On selected.)
        val bottomNavItemIconsSelected = listOf(
            Icons.Filled.Home,
            Icons.Filled.Subscriptions,
            Icons.Filled.Church
        )

        // Enlists the bottom nav bar item icons. (When inactive.)
        val bottomNavItemIconsInactive = listOf(
            Icons.Outlined.Home,
            Icons.Outlined.Subscriptions,
            Icons.Outlined.Church
        )

        BottomAppBar {
            // Ensures that the nav bar stays at the bottom
            // SOURCE: https://stackoverflow.com/q/70904979
            Row(modifier = Modifier.weight(1f, false)) {

                navItems.forEachIndexed { index, item ->

                    NavigationBarItem(
                        icon = {
                            if (fragRoutes.indexOf(ScreenMainCompanion.mutableLastPagerPage.value) == index) {
                                Icon(bottomNavItemIconsSelected[index], contentDescription = item)
                            } else {
                                Icon(bottomNavItemIconsInactive[index], contentDescription = item)
                            }
                        },
                        label = { Text(item) },
                        selected = fragRoutes.indexOf(ScreenMainCompanion.mutableLastPagerPage.value) == index,
                        colors = NavigationBarItemColors(
                            selectedIconColor = Color.Unspecified,
                            selectedTextColor = Color.Unspecified,
                            selectedIndicatorColor = Brown1,
                            unselectedIconColor = Color.Unspecified,
                            unselectedTextColor = Color.Unspecified,
                            disabledIconColor = Color.Unspecified,
                            disabledTextColor = Color.Unspecified
                        ),
                        onClick = {
                            scope.launch { horizontalPagerState.animateScrollToPage(index) }
                        }
                    )  // --- end of nav bar item.

                }

            }  // --- end of row.
        }  // --- end of BottomAppBar.
    }

    @Composable
    @SuppressLint("UseOfNonLambdaOffsetOverload")
    private fun getTopBanner() {

        /* Drawing canvas for the new top bar layout. */
        Box ( modifier = Modifier
            .fillMaxWidth()
            .height((ScreenMainCompanion.MAX_SCREEN_MAIN_TOP_OFFSET + 100).dp)
            .offset(y = ScreenMainCompanion.mutableScreenMainWelcomeImageTopOffset.floatValue.dp)
        ) {

            /* Drawing the top bar greetings banner background. */
            // SOURCE: https://stackoverflow.com/a/70965281
            Image (
                painter = painterResource(newTopBannerBackground),
                contentDescription = "",
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.Crop
            )

            /* To colorize the image. */
            Box (Modifier.background(Color(0x77fdb308)).fillMaxSize()) {}

            /* Drawing the overlapping top bar transparent gradient. */
            // SOURCE: https://developer.android.com/develop/ui/compose/graphics/draw/brush
            // SOURCE: https://stackoverflow.com/a/60479489
            val overlayGradient = Brush.verticalGradient(colorStops = arrayOf (
                // HSL (hue, saturation, lightness) must be in range (0..360, 0..1, 0..1)
                0.0f to Color.hsl(38f, 0.98f, 0.51f),
                0.5f to Color.Transparent,
                1.0f to Color.White
            ))
            Box (
                modifier = Modifier
                    .background(overlayGradient)
                    .fillMaxSize()
                    .padding(LocalContext.current.resources.getDimension(R.dimen.new_topbar_canvas_padding).dp)
                    .padding(top = calculatedTopPadding)
            ) {
                Column {

                    // Shadow.
                    // SOURCE: https://codingwithrashid.com/how-to-add-shadows-to-text-in-android-jetpack-compose/
                    val shadowTextStyle = TextStyle(
                        shadow = Shadow(
                            color = Color.Gray,
                            offset = Offset(2.0f, 2.0f),
                            blurRadius = 5.0f
                        )
                    )

                    // The overlaying greetings text.
                    Text(stringResource(R.string.new_topbar_greetings), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White, style = shadowTextStyle)
                    Text(stringResource(R.string.new_topbar_person_name), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White, style = shadowTextStyle)
                }
            }

        }

    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun getTopBar() {
        TopAppBar (
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = ScreenMainCompanion.topBarContainerColor
                    .copy(ScreenMainCompanion.mutableTopBarContainerTransparency.floatValue),
                titleContentColor = ScreenMainCompanion.topBarTitleContentColor
            ),
            title = {
                Row (horizontalArrangement = Arrangement.Start) {
                    Image(painterResource(R.drawable.app_typography), stringResource(R.string.app_name_alias),
                        modifier = Modifier.aspectRatio(5.68817f).weight(2.0f),
                        contentScale = ContentScale.Crop,
                        colorFilter = ColorFilter.tint(ScreenMainCompanion.topBarTitleContentColor)
                    )
                    Spacer(Modifier.weight(3.0f))
                }
            },
            navigationIcon = {},
            actions = {
                /* TODO: Re-enable the search button once the functionality is ready.
                IconButton(onClick = {
                    GlobalCompanion.pushScreen.value = NavigationRoutes.SCREEN_SEARCH
                    GlobalCompanion.popBackScreen.value = NavigationRoutes.SCREEN_MAIN
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.screensearch_title),
                        tint = ScreenMainCompanion.topBarTitleContentColor
                    )
                }
                */
                /* TODO: Re-enable the settings button once the functionality is ready.
                IconButton(onClick = {
                    GlobalCompanion.pushScreen.value = NavigationRoutes.SCREEN_SETTINGS
                    GlobalCompanion.popBackScreen.value = NavigationRoutes.SCREEN_MAIN
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.screensettings_title),
                        tint = ScreenMainCompanion.topBarTitleContentColor
                    )
                }
                */
                IconButton(onClick = { AppNavigation.navigate(NavigationRoutes.SCREEN_ABOUT) }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.screenabout_title),
                        tint = ScreenMainCompanion.topBarTitleContentColor
                    )
                }
            },
        )
    }
}

/**
 * This companion class stores every static information related to this class file.
 * It is globally readable, exposing any values assigned to public variables stored in it.
 */
class ScreenMainCompanion : Application () {
    companion object {
        val DEFAULT_MAIN_FRAGMENT = NavigationRoutes.FRAG_MAIN_HOME

        /* The dynamic state of the top bar UI. */
        val mutableTopBarContainerTransparency = mutableFloatStateOf(0.0f)
        val topBarContainerColor = Color(Colors.MAIN_TOP_BAR_COLOR)
        val topBarTitleContentColor = Color(Colors.MAIN_TOP_BAR_CONTENT_COLOR)

        /* The calculated top bar padding of the scaffolding. */
        var calculatedTopBarPadding = 0.dp

        /* The last visited pager page (representing fragment) in ScreenMain. */
        var mutableLastPagerPage = mutableStateOf(DEFAULT_MAIN_FRAGMENT)

        /* The top offset of fragments in the ScreenMain. */
        const val MIN_SCREEN_MAIN_TOP_OFFSET = 0.0f
        const val MAX_SCREEN_MAIN_TOP_OFFSET = 325.0f
        val mutableScreenMainContentTopOffset = mutableFloatStateOf(MAX_SCREEN_MAIN_TOP_OFFSET)

        /* The top offset of the main menu's welcome image (in the top bar). */
        const val MIN_SCREEN_MAIN_WELCOME_IMAGE_TOP_OFFSET = -(MAX_SCREEN_MAIN_TOP_OFFSET - MIN_SCREEN_MAIN_TOP_OFFSET) / 2
        const val MAX_SCREEN_MAIN_WELCOME_IMAGE_TOP_OFFSET = 0.0f
        val mutableScreenMainWelcomeImageTopOffset = mutableFloatStateOf(MAX_SCREEN_MAIN_WELCOME_IMAGE_TOP_OFFSET)
    }
}
