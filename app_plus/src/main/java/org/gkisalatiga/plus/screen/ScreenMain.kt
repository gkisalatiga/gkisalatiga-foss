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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.composable.MainPTR
import org.gkisalatiga.plus.composable.MainPTRCompanion
import org.gkisalatiga.plus.composable.OfflineSnackbarHost
import org.gkisalatiga.plus.composable.OfflineSnackbarHostCompanion
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.fragment.FragmentHome
import org.gkisalatiga.plus.fragment.FragmentInfo
import org.gkisalatiga.plus.fragment.FragmentServices
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.services.PermissionChecker

class ScreenMain (private val current : ActivityData) : ComponentActivity() {

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

    // Determines the top banner title.
    private var topBannerTitle = ""

    // Controls the horizontal scrolling of the pager.
    private lateinit var horizontalPagerState: PagerState

    // Determines what background to show on the new top bar layout by user github.com/ujepx64.
    private var newTopBannerBackground = R.drawable.topbar_greetings_background

    // The snackbar host state.
    private val snackbarHostState = OfflineSnackbarHostCompanion.snackbarHostState

    @Composable
    @ExperimentalMaterial3Api
    @SuppressLint("ComposableNaming", "UnusedMaterial3ScaffoldPaddingParameter", "UseOfNonLambdaOffsetOverload")
    fun getComposable() {
        // Initializing the top banner title.
        topBannerTitle = current.ctx.resources.getString(R.string.app_name_alias)

        // Initializing the horizontal pager.
        horizontalPagerState = rememberPagerState ( pageCount = {fragRoutes.size}, initialPage = fragRoutes.indexOf(ScreenMainCompanion.mutableLastPagerPage.value) )

        // Connects the horizontal pager with the bottom bar.
        LaunchedEffect(horizontalPagerState.targetPage) {
            ScreenMainCompanion.mutableLastPagerPage.value = fragRoutes[horizontalPagerState.targetPage]
        }

        // Check whether notification permission is already granted.
        LaunchedEffect(Unit) {
            PermissionChecker(current.ctx).checkNotificationPermission()
        }

        // The pull-to-refresh indicator states.
        val isRefreshing = remember { MainPTRCompanion.isPTRRefreshing }
        val pullToRefreshState = MainPTRCompanion.mainPTRState

        Scaffold (
            bottomBar = { getBottomBar() },
            topBar = {
                getTopBar()
            },
            snackbarHost = { OfflineSnackbarHost() },
            floatingActionButton =  { },
            floatingActionButtonPosition = FabPosition.Center,
            modifier = Modifier.pullToRefresh(isRefreshing.value, pullToRefreshState!!, onRefresh = {
                MainPTRCompanion.launchOnRefresh(current.ctx)
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
                                0 -> FragmentHome(current).getComposable()
                                1 -> FragmentServices(current).getComposable()
                                2 -> FragmentInfo(current).getComposable()
                            }
                        }
                    }
                }  // --- end of box 2.
            }  // --- end of box 1.

            // Check whether we are connected to the internet.
            // Then notify user about this.
            val snackbarMessageString = stringResource(R.string.not_connected_to_internet)
            LaunchedEffect(GlobalCompanion.isConnectedToInternet.value) {
                if (!GlobalCompanion.isConnectedToInternet.value) current.scope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarMessageString,
                        duration = SnackbarDuration.Short
                    )
                }
            }

            // Add pull-to-refresh mechanism for updating the content data.
            MainPTR(it.calculateTopPadding())

            // Ensure that when we are at the first screen upon clicking "back",
            // the app is exited instead of continuing to navigate back to the previous screens.
            // SOURCE: https://stackoverflow.com/a/69151539
            val exitConfirm = stringResource(R.string.exit_confirmation_toast_string)
            BackHandler {
                when (val curRoute = ScreenMainCompanion.mutableLastPagerPage.value) {
                    NavigationRoutes.FRAG_MAIN_HOME -> {

                        // Ensure "double tap the back button to exit".
                        if (backPressedTime + 2000 > System.currentTimeMillis()) {
                            // Exit the application.
                            // SOURCE: https://stackoverflow.com/a/67402808
                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "You just clicked $curRoute and exited the app!", Toast.LENGTH_SHORT).show()
                            (current.ctx as ComponentActivity).finish()
                        } else {
                            Toast.makeText(current.ctx, exitConfirm, Toast.LENGTH_LONG).show()
                        }

                        backPressedTime = System.currentTimeMillis()

                    }
                    NavigationRoutes.FRAG_MAIN_INFO, NavigationRoutes.FRAG_MAIN_SERVICES -> {
                        // Since we are in the main screen but not at fragment one,
                        // navigate the app to fragment one.
                        current.scope.launch { horizontalPagerState.animateScrollToPage(0) }
                    }
                    else -> {
                        // Do nothing.
                    }
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
                                Icon(bottomNavItemIconsSelected[index], contentDescription = "Nav bar selected: $item")
                            } else {
                                Icon(bottomNavItemIconsInactive[index], contentDescription = "Nav bar inactive: $item")
                            }
                        },
                        label = { Text(item) },
                        selected = fragRoutes.indexOf(ScreenMainCompanion.mutableLastPagerPage.value) == index,
                        colors = NavigationBarItemColors(
                            selectedIconColor = current.colors.screenMainBottomNavSelectedIconColor,
                            selectedTextColor = current.colors.screenMainBottomNavSelectedTextColor,
                            selectedIndicatorColor = current.colors.screenMainBottomNavSelectedIndicationColor,
                            unselectedIconColor = current.colors.screenMainBottomNavUnselectedIconColor,
                            unselectedTextColor = current.colors.screenMainBottomNavUnselectedTextColor,
                            disabledIconColor = Color.Unspecified,
                            disabledTextColor = Color.Unspecified
                        ),
                        onClick = {
                            current.scope.launch { horizontalPagerState.animateScrollToPage(index) }
                        },
                        modifier = Modifier.semantics {
                            this.contentDescription = "Bottom navigation menu: $item"
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
            Image (
                painter = painterResource(newTopBannerBackground),
                contentDescription = "FragmentHome Top Bar Greetings Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            /* To colorize the image. */
            Box (Modifier.background(Colors.SCREEN_MAIN_COLORIZE_COLOR).fillMaxSize()) {}

            /* Drawing the overlapping top bar transparent gradient. */
            val overlayGradient = ScreenMainCompanion.renderedOverlayGradient!!
            Box (
                modifier = Modifier
                    .background(overlayGradient)
                    .fillMaxSize()
                    .padding(current.ctx.resources.getDimension(R.dimen.new_topbar_canvas_padding).dp)
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
                    val strings = MainCompanion.jsonRoot!!.getJSONObject("backend").getJSONObject("strings")
                    val greetingsTop = strings.getString("greetings_top")
                    Text(greetingsTop, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White, style = shadowTextStyle)
                    val greetingsBottom = strings.getString("greetings_bottom")
                    Text(greetingsBottom, fontSize = 21.sp, fontWeight = FontWeight.SemiBold, color = Color.White, style = shadowTextStyle)
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
                    Image(
                        painter = painterResource(R.drawable.app_typography),
                        contentDescription = stringResource(R.string.app_name_alias),
                        modifier = Modifier.fillMaxWidth().weight(2.5f).padding(vertical = 10.dp),//Modifier.aspectRatio(5.68817f).weight(2.5f),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart
                    )
                    Spacer(Modifier.weight(0.75f))
                }
            },
            navigationIcon = {},
            actions = {
                IconButton(
                    onClick = { AppNavigation.navigate(NavigationRoutes.SCREEN_SEARCH) },
                    modifier = Modifier.semantics { this.contentDescription = "GKI Salatiga+ search button" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.screensearch_title),
                        tint = ScreenMainCompanion.topBarTitleContentColor
                    )
                }
                IconButton(
                    onClick = { AppNavigation.navigate(NavigationRoutes.SCREEN_SETTINGS) },
                    modifier = Modifier.semantics { this.contentDescription = "GKI Salatiga+ settings button" }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.screensettings_title),
                        tint = ScreenMainCompanion.topBarTitleContentColor
                    )
                }
                IconButton(
                    onClick = { AppNavigation.navigate(NavigationRoutes.SCREEN_ABOUT) },
                    modifier = Modifier.semantics { this.contentDescription = "GKI Salatiga+ about button" }
                ) {
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
        var renderedOverlayGradient: Brush? = null
        val topBarContainerColor = Colors.MAIN_TOP_BAR_COLOR
        val topBarTitleContentColor = Colors.MAIN_TOP_BAR_CONTENT_COLOR

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
