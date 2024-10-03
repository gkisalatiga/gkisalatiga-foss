/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 *
 * REFERENCES USED:
 *
 * Scaffold tutorial
 * SOURCE: https://www.jetpackcompose.net/scaffold
 *
 * Navigation between screens in Jetpack Compose
 * SOURCE: https://medium.com/@KaushalVasava/navigation-in-jetpack-compose-full-guide-beginner-to-advanced-950c1133740
 * SOURCE: https://medium.com/@husayn.fakher/a-guide-to-navigation-in-jetpack-compose-questions-and-answers-d86b7e6a8523
 *
 * Navigation screen transition animation
 * SOURCE: https://stackoverflow.com/a/68749621
 *
 * You don't need a fragment nor separate activity in Jetpack Compose.
 * Each method can act as a separate container of an individual part.
 * SOURCE: https://stackoverflow.com/a/66378077
 *
 * On writing a clean code, it's pros and cons:
 * SOURCE: https://softwareengineering.stackexchange.com/a/29205
 */

package org.gkisalatiga.plus

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import kotlinx.coroutines.delay
import org.gkisalatiga.plus.composable.YouTubeView
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.AppGallery
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.GallerySaver

import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.AppStatic
import org.gkisalatiga.plus.screen.ScreenAbout
import org.gkisalatiga.plus.screen.ScreenAgenda
import org.gkisalatiga.plus.screen.ScreenAttribution
import org.gkisalatiga.plus.screen.ScreenDev
import org.gkisalatiga.plus.screen.ScreenForms
import org.gkisalatiga.plus.screen.ScreenGaleri
import org.gkisalatiga.plus.screen.ScreenGaleriList
import org.gkisalatiga.plus.screen.ScreenGaleriView
import org.gkisalatiga.plus.screen.ScreenGaleriYear
import org.gkisalatiga.plus.screen.ScreenInternalHTML
import org.gkisalatiga.plus.screen.ScreenLiturgi
import org.gkisalatiga.plus.screen.ScreenMain
import org.gkisalatiga.plus.screen.ScreenMedia
import org.gkisalatiga.plus.screen.ScreenPersembahan
import org.gkisalatiga.plus.screen.ScreenPosterViewer
import org.gkisalatiga.plus.screen.ScreenStaticContentList
import org.gkisalatiga.plus.screen.ScreenVideoList
import org.gkisalatiga.plus.screen.ScreenVideoLive
import org.gkisalatiga.plus.screen.ScreenWarta
import org.gkisalatiga.plus.screen.ScreenWebView
import org.gkisalatiga.plus.screen.ScreenYKB
import org.gkisalatiga.plus.services.ConnectionChecker
import org.gkisalatiga.plus.services.DataUpdater
import org.gkisalatiga.plus.services.DeepLinkHandler
import org.gkisalatiga.plus.services.NotificationService
import org.gkisalatiga.plus.services.WorkScheduler
import org.gkisalatiga.plus.ui.theme.GKISalatigaPlusTheme

@OptIn(ExperimentalMaterial3Api::class)
class ActivityLauncher : ComponentActivity() {

    override fun onPause() {
        super.onPause()
        GlobalSchema.isRunningInBackground.value = true
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ActivityLauncher.onPause] App is now in background.")
    }

    override fun onResume() {
        super.onResume()
        GlobalSchema.isRunningInBackground.value = false
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ActivityLauncher.onResume] App has been restored to foreground.")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        /* Handles deep-linking. */
        intent?.data?.let {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) Log.d("Groaker-Test", "[ActivityLauncher.onNewIntent] Received data: host: ${it.host}, path: ${it.path}, encodedPath: ${it.encodedPath}, pathSegments: ${it.pathSegments}")
            if (it.host == "gkisalatiga.org" || it.host == "www.gkisalatiga.org") {
                when (it.encodedPath) {
                    "/plus/deeplink/saren" -> DeepLinkHandler.handleSaRen()
                    "/plus/deeplink/ykb" -> DeepLinkHandler.handleYKB()
                    else -> if (it.encodedPath != null) DeepLinkHandler.openDomainURL("https://${it.host}${it.encodedPath}")
                }
            }
        }  // --- end of intent?.data?.let {}
    }  // --- end of onNewIntent.

    @SuppressLint("MissingSuperCall", "Recycle")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == GlobalSchema.GALLERY_SAVER_CODE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                // Decode the URI path.
                // SOURCE: https://www.perplexity.ai/search/kotlin-how-to-download-file-to-h.TAGPj2R5yOTTZ_d3ebKQ
                val contentResolver = applicationContext.contentResolver
                val outputStream = contentResolver.openOutputStream(uri)

                // Perform operations on the document using its URI.
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_DUMP) Log.d("Groaker-Dump", uri.path!!)
                GallerySaver().onSAFPathReceived(outputStream!!)
            }
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {

        // SOURCE: https://stackoverflow.com/a/53669865
        // ProcessLifecycleOwner.get().lifecycle.addObserver(this);

        // Initializes the app's internally saved preferences.
        initPreferences()

        // Start the connection (online/offline) checker.
        ConnectionChecker(this).execute()

        // Configure the behavior of the hidden system bars and configure the immersive mode (hide status bar and navigation bar).
        // SOURCE: https://developer.android.com/develop/ui/views/layout/immersive
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Enable transparent status bar.
        // SOURCE: https://youtu.be/Ruu44ZUhkBM?si=KTtR2GjZdqMa-rBs
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        // Lock the screen's orientation to portrait mode only.
        val targetOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.requestedOrientation = targetOrientation

        // Enable on-the-fly edit of drawable SVG vectors.
        // SOURCE: https://stackoverflow.com/a/38418049
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Preamble logging to the terminal.
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Starting app: ${this.resources.getString(R.string.app_name_alias)}")

        // Call the superclass. (The default behavior. DO NOT CHANGE!)
        super.onCreate(savedInstanceState)

        // Determine the default screen, fragment, and submenu to open upon first app launch,
        // as well as other pre-determined default values.
        val defaultScreenLaunch = NavigationRoutes().SCREEN_MAIN
        val defaultFragmentLaunch = NavigationRoutes().FRAG_MAIN_HOME
        val defaultServicesSubmenu = NavigationRoutes().SUB_BLANK
        val defaultNewTopBarBackground = R.drawable.topbar_greetings_background

        // Setting some of the most important default values of the global schema.
        // (i.e., the composable navigation direction.)
        GlobalSchema.pushScreen.value = defaultScreenLaunch
        GlobalSchema.lastMainScreenPagerPage.value = defaultFragmentLaunch
        GlobalSchema.lastServicesSubmenu.value = defaultServicesSubmenu

        // The top bar greeting background.
        GlobalSchema.lastNewTopBarBackground.value = defaultNewTopBarBackground

        // Setting the clipboard manager.
        // Should be performed within "onCreate" to avoid the following error:
        // java.lang.IllegalStateException: System services not available to Activities before onCreate()
        GlobalSchema.clipManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // Prepares the global YouTube composable and viewer.
        // Prevents NPE.
        GlobalSchema.ytComposable = YouTubeView()

        // Retrieving the latest JSON metadata.
        initData()

        // Block the app until all data is initialized.
        // Prevents "null pointer exception" when the JSON data in the multi-thread has not been prepared.
        while (true) {
            if (GlobalSchema.globalJSONObject != null && GlobalSchema.globalGalleryObject != null && GlobalSchema.globalStaticObject != null) {
                break
            } else {
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_SPAM) Log.w("Groaker-Spam", "[ActivityLauncher.onCreate] Still initializing data ...")
            }
        }

        // Creating the notification channels.
        initNotificationChannel()

        // Initializing the scheduled alarms.
        initWorkManager()

        // Initiate the Jetpack Compose composition.
        // This is the entry point of every composable, similar to "main()" function in Java.
        setContent {

            // Try to remember the state of the carousel.
            initCarouselState()

            // Initializes the scroll states.
            GlobalSchema.fragmentGalleryListScrollState = rememberLazyGridState()
            GlobalSchema.fragmentHomeScrollState = rememberScrollState()
            GlobalSchema.fragmentServicesScrollState = rememberScrollState()
            GlobalSchema.fragmentInfoScrollState = rememberScrollState()
            GlobalSchema.screenAboutScrollState = rememberScrollState()
            GlobalSchema.screenAttributionScrollState = rememberScrollState()
            GlobalSchema.screenAgendaScrollState = rememberScrollState()
            GlobalSchema.screenFormsScrollState = rememberScrollState()
            GlobalSchema.screenGaleriScrollState = rememberScrollState()
            GlobalSchema.screenMediaScrollState = rememberScrollState()
            GlobalSchema.screenPersembahanScrollState = rememberScrollState()

            // Prepare the pull-to-refresh (PTR) state globally.
            GlobalSchema.globalPTRState = rememberPullToRefreshState()

            // Listen to the request to hide the phone's bars.
            // SOURCE: https://developer.android.com/develop/ui/views/layout/immersive
            key (GlobalSchema.phoneBarsVisibility.value) {
                if (GlobalSchema.phoneBarsVisibility.value) {
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                } else {
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                }
            }

            GKISalatigaPlusTheme {
                if (!GlobalSchema.DEBUG_DISABLE_SPLASH_SCREEN) {
                    // Splash screen.
                    // SOURCE: https://medium.com/@fahadhabib01/animated-splash-screens-in-jetpack-compose-navigation-component-4e28f69ad559
                    Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
                        val splashNavController = rememberNavController()
                        NavHost(navController = splashNavController, startDestination = "init_screen") {
                            composable(
                                "init_screen",
                                deepLinks = listOf(
                                    navDeepLink { uriPattern = "https://gkisalatiga.org" },
                                    navDeepLink { uriPattern = "https://www.gkisalatiga.org" }
                                )
                            ) {
                                /* Handles deep-linking. */
                                if (intent?.data != null) {
                                    intent?.data?.let {
                                        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_TEST) Log.d("Groaker-Test", "[ActivityLauncher.onCreate] Received data: host: ${it.host}, path: ${it.path}, encodedPath: ${it.encodedPath}, pathSegments: ${it.pathSegments}")
                                        if (it.host == "gkisalatiga.org" || it.host == "www.gkisalatiga.org") {
                                            when (it.encodedPath) {
                                                "/plus/deeplink/saren" -> DeepLinkHandler.handleSaRen()
                                                "/plus/deeplink/ykb" -> DeepLinkHandler.handleYKB()
                                                else -> if (it.encodedPath != null) DeepLinkHandler.openDomainURL("https://${it.host}${it.encodedPath}")
                                            }
                                            // This activity was called from a URI call. Skip the splash screen.
                                            initMainGraphic()
                                        }
                                    }  // --- end of intent?.data?.let {}

                                /* Nothing matches, start the app from the beginning.*/
                                } else {
                                    // This isn't a URI action call. Open the app regularly.
                                    GlobalSchema.defaultScreen.value = NavigationRoutes().SCREEN_MAIN
                                    initSplashScreen(splashNavController)
                                }
                            }  // --- end of navigation composable.

                            composable ("main_screen") {
                                // Just display the main graphic directly.
                                GlobalSchema.defaultScreen.value = NavigationRoutes().SCREEN_MAIN
                                initMainGraphic()
                            }  // --- end of navigation composable.
                        }  // --- end of NavHost.
                    }
                } else {
                    // Just display the main graphic directly.
                    GlobalSchema.defaultScreen.value = NavigationRoutes().SCREEN_MAIN
                    initMainGraphic()
                }
            }  // --- end of GKISalatigaPlusTheme.
        }  // --- end of setContent().
    }  // --- end of onCreate().

    /**
     * This method reads the current saved preference associated with the app
     * and pass it to the GlobalSchema so that other functions can use them.
     */
    private fun initPreferences() {
        // Initializes the preferences.
        AppPreferences(this).readAllPreferences()

        // Increment the number of counts.
        val now = GlobalSchema.preferencesKeyValuePairs[GlobalSchema.PREF_KEY_LAUNCH_COUNTS] as Int
        AppPreferences(this).writePreference(GlobalSchema.PREF_KEY_LAUNCH_COUNTS, now + 1)
        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(this, "Launches since install: ${now + 1}", Toast.LENGTH_SHORT).show()
    }

    /**
     * This method determines what is shown during splash screen.
     * @param schema the app's custom configurable schema, shared across composables.
     */
    @Composable
    private fun initSplashScreen(splashNavController: NavHostController) {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "Loading splash screen of the app ...")

        val scale = remember { androidx.compose.animation.core.Animatable(1.6f) }
        LaunchedEffect(key1 = true) {
            scale.animateTo(targetValue = 0.5f, animationSpec = tween(durationMillis = 950, easing = { FastOutSlowInEasing.transform(it) /*OvershootInterpolator(2f).getInterpolation(it)*/ }))

            // Determines the duration of the splash screen.
            delay(100)
            splashNavController.navigate("main_screen")
        }

        // Displays the splash screen content.
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(Color(0xff071450))) {
            Image(painter = painterResource(id = R.drawable.splash_screen_foreground), contentDescription = "Splash screen logo", modifier = Modifier.scale(scale.value))
        }
    }

    /**
     * This method will become the navigation hub of screens across composables.
     * It also becomes the graphical base of all screens.
     */
    @Composable
    private fun initMainGraphic() {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) {
            Log.d("Groaker", "Initializing main graphic ...")
            Log.d("Groaker", "Obtained 'pushScreen' value: ${GlobalSchema.pushScreen.value}")
        }

        // We use nav. host because it has built-in support for transition effect/animation.
        // We also use nav. host so that we can handle URI deep-linking,
        // both from an external URL click and from a notification click.
        // SOURCE: https://composables.com/tutorials/deeplinks
        val mainNavController = rememberNavController()
        NavHost(navController = mainNavController, startDestination = GlobalSchema.defaultScreen.value) {
            composable(NavigationRoutes().SCREEN_MAIN) { ScreenMain().getComposable() }
            composable(NavigationRoutes().SCREEN_ABOUT) { ScreenAbout().getComposable() }
            composable(NavigationRoutes().SCREEN_ATTRIBUTION) { ScreenAttribution().getComposable() }
            composable(NavigationRoutes().SCREEN_LIVE) { ScreenVideoLive().getComposable() }
            composable(NavigationRoutes().SCREEN_FORMS) { ScreenForms().getComposable() }
            composable(NavigationRoutes().SCREEN_DEV) { ScreenDev().getComposable() }
            composable(NavigationRoutes().SCREEN_AGENDA) { ScreenAgenda().getComposable() }
            composable(NavigationRoutes().SCREEN_PERSEMBAHAN) { ScreenPersembahan().getComposable() }
            composable(NavigationRoutes().SCREEN_GALERI) { ScreenGaleri().getComposable() }
            composable(NavigationRoutes().SCREEN_GALERI_LIST) { ScreenGaleriList().getComposable() }
            composable(NavigationRoutes().SCREEN_GALERI_VIEW) { ScreenGaleriView().getComposable() }
            composable(NavigationRoutes().SCREEN_GALERI_YEAR) { ScreenGaleriYear().getComposable() }
            composable(NavigationRoutes().SCREEN_MEDIA) { ScreenMedia().getComposable() }
            composable(NavigationRoutes().SCREEN_YKB) {ScreenYKB().getComposable()}
            composable(NavigationRoutes().SCREEN_VIDEO_LIST) { ScreenVideoList().getComposable() }
            composable(NavigationRoutes().SCREEN_WARTA) { ScreenWarta().getComposable() }
            composable(NavigationRoutes().SCREEN_LITURGI) { ScreenLiturgi().getComposable() }
            composable(NavigationRoutes().SCREEN_WEBVIEW) { ScreenWebView().getComposable() }
            composable(NavigationRoutes().SCREEN_INTERNAL_HTML) { ScreenInternalHTML().getComposable() }
            composable(NavigationRoutes().SCREEN_POSTER_VIEWER) { ScreenPosterViewer().getComposable() }
            composable(NavigationRoutes().SCREEN_STATIC_CONTENT_LIST) { ScreenStaticContentList().getComposable() }
        }

        // Watch for the state change in the parameter "pushScreen".
        // SOURCE: https://stackoverflow.com/a/73129228
        key(GlobalSchema.pushScreen.value, GlobalSchema.reloadCurrentScreen.value) {
            mainNavController.navigate(GlobalSchema.pushScreen.value)
        }

    }

    /**
     * Initializing the app's notification channels.
     * This is only need on Android API 26+.
     */
    private fun initNotificationChannel() {
        NotificationService.initFallbackDebugChannel(this)
        NotificationService.initSarenNotificationChannel(this)
        NotificationService.initYKBHarianNotificationChannel(this)
    }

    /**
     * Initializing the WorkManager,
     * which will trigger notifications and stuffs.
     */
    private fun initWorkManager() {
        WorkScheduler.scheduleSarenReminder(this)
        WorkScheduler.scheduleYKBReminder(this)
    }

    /**
     * This app prepares the downloading of JSON metadata.
     * It should always be performed at the beginning of app to ensure updated content.
     * This function initializes the GZip-compressed Tarfile archive containing
     * the static data of GKI Salatiga Plus.
     * ---
     * This function does not need to become a composable function since it requires no UI.
     */
    private fun initData() {

        // Get the number of launches since install so that we can determine
        // whether to use the fallback data.
        val launches = GlobalSchema.preferencesKeyValuePairs[GlobalSchema.PREF_KEY_LAUNCH_COUNTS] as Int

        // Get fallback data only if first launch.
        if (launches == 0) {
            // Let's apply the fallback JSON data until the actual, updated JSON metadata is downloaded.
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_INIT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Loading the fallback JSON metadata ...")
            AppDatabase(this).initFallbackGalleryData()

            // Loading the fallback gallery data.
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_INIT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Loading the fallback gallery JSON file ...")
            AppGallery(this).initFallbackGalleryData()

            // Loading the fallback static data.
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_INIT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Loading the fallback static JSON file ...")
            AppStatic(this).initFallbackStaticData()

        } else {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_INIT) Log.d("Groaker-Init", "[ActivityLauncher.initData] This is not first launch.")
        }

        // At last, update the data to the latest whenever possible.
        DataUpdater(this).updateData()
    }

    @Composable
    private fun initCarouselState() {

        // Use multithread (coroutine scope) so that it won't block the app's execution.
        val scope = rememberCoroutineScope()
        scope.run {

            /* Read the carousel JSON object. */
            // First, we clear the array list.
            GlobalSchema.carouselJSONObject.clear()
            GlobalSchema.carouselJSONKey.clear()
            // Directly assign globally.
            val carouselParentNode = GlobalSchema.globalJSONObject!!.getJSONObject("carousel")
            for (l in carouselParentNode.keys()) {
                // Extract the static data's node names.
                GlobalSchema.carouselJSONObject.add(carouselParentNode.getJSONObject(l))

                // Get the carousel JSON object key strings.
                GlobalSchema.carouselJSONKey.add(l)
            }

            /**********************************************************************/

            // "Infinite" pager page scrolling.
            // Please fill the following integer-variable with a number of pages
            // that the user won't bother scrolling.
            // SOURCE: https://stackoverflow.com/a/75469260
            val baseInfiniteScrollingPages = 256  // --- i.e., 2^8.

            // Necessary variables for the infinite-page carousel.
            // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
            val actualPageCount = GlobalSchema.carouselJSONKey.size
            val carouselPageCount = actualPageCount * baseInfiniteScrollingPages
            GlobalSchema.fragmentHomeCarouselPagerState = rememberPagerState(
                initialPage = carouselPageCount / 2,
                pageCount = { carouselPageCount }
            )
        }
    }

}