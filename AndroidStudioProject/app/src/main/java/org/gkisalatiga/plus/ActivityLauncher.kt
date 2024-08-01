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
import android.content.ClipboardManager
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
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
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.Downloader
import org.gkisalatiga.plus.lib.Extractor

import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenAbout
import org.gkisalatiga.plus.screen.ScreenForms
import org.gkisalatiga.plus.screen.ScreenInternalHTML
import org.gkisalatiga.plus.screen.ScreenLiturgi
import org.gkisalatiga.plus.screen.ScreenMain
import org.gkisalatiga.plus.screen.ScreenPosterViewer
import org.gkisalatiga.plus.screen.ScreenVideoList
import org.gkisalatiga.plus.screen.ScreenVideoLive
import org.gkisalatiga.plus.screen.ScreenWarta
import org.gkisalatiga.plus.screen.ScreenWebView
import org.gkisalatiga.plus.screen.ScreenYKB
import org.gkisalatiga.plus.ui.theme.GKISalatigaPlusTheme
import org.json.JSONObject
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// import org.gkisalatiga.plus.screen.ScreenMain

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

    @SuppressLint("MutableCollectionMutableState")
    override fun onCreate(savedInstanceState: Bundle?) {

        // SOURCE: https://stackoverflow.com/a/53669865
        // ProcessLifecycleOwner.get().lifecycle.addObserver(this);

        // Initializes the app's internally saved preferences.
        initPreferences()

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

        // Setting the global context value.
        GlobalSchema.context = this

        // Setting the clipboard manager.
        // Should be performed within "onCreate" to avoid the following error:
        // java.lang.IllegalStateException: System services not available to Activities before onCreate()
        GlobalSchema.clipManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // Prepares the global YouTube viewer.
        // Prevents NPE.
        GlobalSchema.ytView = YouTubePlayerView(this)

        // Retrieving the latest JSON metadata.
        initData()

        // Initiate the Jetpack Compose composition.
        // This is the entry point of every composable, similar to "main()" function in Java.
        setContent {

            // Initializes the scroll states.
            GlobalSchema.fragmentHomeScrollState = rememberScrollState()
            GlobalSchema.fragmentServicesScrollState = rememberScrollState()
            GlobalSchema.fragmentInfoScrollState = rememberScrollState()

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
                        NavHost(navController = splashNavController, startDestination = "splash_screen") {
                            composable("splash_screen") {
                                initSplashScreen(splashNavController = splashNavController)
                            }
                            composable("main_screen") {
                                initMainGraphic()
                            }
                        }
                    }
                } else {
                    // Just display the main graphic directly.
                    initMainGraphic()
                }

            }
        }
    }

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
        if (GlobalSchema.DEBUG_ENABLE_TOAST) Toast.makeText(this, "Launches since install: $now", Toast.LENGTH_SHORT).show()
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
        val mainNavController = rememberNavController()
        NavHost(navController = mainNavController, startDestination = NavigationRoutes().SCREEN_MAIN) {
            composable(NavigationRoutes().SCREEN_MAIN) { ScreenMain().getComposable() }
            composable(NavigationRoutes().SCREEN_ABOUT) { ScreenAbout().getComposable() }
            composable(NavigationRoutes().SCREEN_LIVE) { ScreenVideoLive().getComposable() }
            composable(NavigationRoutes().SCREEN_FORMS) { ScreenForms().getComposable() }
            composable(NavigationRoutes().SCREEN_YKB) { ScreenYKB().getComposable() }
            composable(NavigationRoutes().SCREEN_VIDEO_LIST) { ScreenVideoList().getComposable() }
            composable(NavigationRoutes().SCREEN_WARTA) { ScreenWarta().getComposable() }
            composable(NavigationRoutes().SCREEN_LITURGI) { ScreenLiturgi().getComposable() }
            composable(NavigationRoutes().SCREEN_WEBVIEW) { ScreenWebView().getComposable() }
            composable(NavigationRoutes().SCREEN_INTERNAL_HTML) { ScreenInternalHTML().getComposable() }
            composable(NavigationRoutes().SCREEN_POSTER_VIEWER) { ScreenPosterViewer().getComposable() }
        }

        // Watch for the state change in the parameter "pushScreen".
        // SOURCE: https://stackoverflow.com/a/73129228
        key(GlobalSchema.pushScreen.value) {
            mainNavController.navigate(GlobalSchema.pushScreen.value)
        }

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

        val timeNowMillis = System.currentTimeMillis()

        // Determine should we re-download the static data archive file from the repository,
        // which could be huge in size. (We don't do it frequently.)
        var updateStaticData = false
        val lastStaticDataUpdate = GlobalSchema.preferencesKeyValuePairs[GlobalSchema.PREF_KEY_LAST_STATIC_DATA_UPDATE] as Long
        val staticDataUpdateFrequency = GlobalSchema.preferencesKeyValuePairs[GlobalSchema.PREF_KEY_STATIC_DATA_UPDATE_FREQUENCY] as Long
        if (timeNowMillis > lastStaticDataUpdate + staticDataUpdateFrequency) {
            updateStaticData = true
            AppPreferences(this).writePreference(GlobalSchema.PREF_KEY_LAST_STATIC_DATA_UPDATE, timeNowMillis)
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] The static data is too old. It will be updated soon.")
        } else {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] The static data is up-to-date.")
        }

        // Determine should we re-download the carousel banner archive file from the repository,
        // which could be huge in size. (We don't do it frequently.)
        var updateCarouselBanner = false
        val lastCarouselBannerUpdate = GlobalSchema.preferencesKeyValuePairs[GlobalSchema.PREF_KEY_LAST_CAROUSEL_BANNER_UPDATE] as Long
        val carouselBannerUpdateFrequency = GlobalSchema.preferencesKeyValuePairs[GlobalSchema.PREF_KEY_CAROUSEL_BANNER_UPDATE_FREQUENCY] as Long
        if (timeNowMillis > lastCarouselBannerUpdate + carouselBannerUpdateFrequency) {
            updateCarouselBanner = true
            AppPreferences(this).writePreference(GlobalSchema.PREF_KEY_LAST_CAROUSEL_BANNER_UPDATE, timeNowMillis)
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] The carousel banner archive is too old. It will be updated soon.")
        } else {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] The carousel banner archive is up-to-date.")
        }

        // Upon successful data download, we manage the app's internal variable storage
        // according to the downloaded JSON file's schema.
        // We also make any appropriate settings accordingly.
        // ---
        // This is all done in a multi-thread so that we do not interrupt the main GUI.
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            // Create the JSON manager object.
            val appDB = AppDatabase()

            // Get the number of launches since install so that we can determine
            // whether to use the fallback data.
            val launches = GlobalSchema.preferencesKeyValuePairs[GlobalSchema.PREF_KEY_LAUNCH_COUNTS] as Int

            // Get fallback data only if first launch.
            if (launches == 0) {
                // Let's apply the fallback JSON data until the actual, updated JSON metadata is downloaded.
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Loading the fallback JSON metadata ...")
                GlobalSchema.globalJSONObject = appDB.getFallbackMainData()

                // Obtain the fallback static zip data.
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Loading the fallback zipped static data ...")
                Extractor(this).initFallbackStaticData()

                // Obtain the fallback carousel banner data.
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Loading the fallback carousel banner data ...")
                Extractor(this).initFallbackCarouselBanner()
            } else {
                if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] This is not first launch.")
            }

            // Init the services sections, mitigating java.util.ConcurrentModificationException.
            initServicesSection()

            // Set the flag to "false" to signal that we need to have the new data now.
            GlobalSchema.isJSONMetaDataInitialized.value = false

            while (true) {

                // Make the attempt to download the JSON file.
                Downloader().initMetaData()

                if (GlobalSchema.isJSONMetaDataInitialized.value) {
                    // Since the JSON metadata has now been downloaded, let's assign the actual JSON globally.
                    GlobalSchema.globalJSONObject = appDB.getMainData()
                    if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Successfully refreshed the JSON data!")

                    // Make the attempt to fetch the online static data.
                    if (updateStaticData) {
                        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Fetching the latest static data zipfile ...")
                        Extractor(this).initStaticData()
                    } else {
                        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Initializing the cached static data files ...")
                        Extractor(this).initStaticExtractLocation()
                    }

                    // Make the attempt to fetch the online carousel banner data.
                    if (updateCarouselBanner) {
                        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Fetching the latest carousel banner zipfile ...")
                        Extractor(this).initCarouselData()
                    } else {
                        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Init", "[ActivityLauncher.initData] Initializing the cached carousel banner files ...")
                        Extractor(this).initCarouselExtractLocation()
                    }

                    // Init the services sections, mitigating java.util.ConcurrentModificationException.
                    initServicesSection()

                    // It is finally set-up. Let's break free from this loop.
                    break

                } else {
                    // Sleep for a couple of milliseconds before continuing.
                    // SOURCE: http://stackoverflow.com/questions/24104313/ddg#24104427
                    TimeUnit.SECONDS.sleep(1);
                    continue
                }

            }
        }
    }

    /**
     * This function initializes the sections inside the "Services" tab,
     * based on the retrieved (or fallback) JSON.
     *
     * This function is created in order to mitigate: java.util.ConcurrentModificationException,
     * which occurs when the services sections are initialized in the fragment instead of the main thread/composable.
     */
    private fun initServicesSection() {
        // Preamble logging.
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker", "[ActivityLauncher.initServicesSection] Launching services initialization ...")

        // Get the application's JSON object.
        val json: JSONObject = AppDatabase().getMainData()

        // Reset the ArrayLists.
        GlobalSchema.servicesNode = ArrayList<String>()
        GlobalSchema.servicesTitle = ArrayList<String>()

        // Retrieve the dict key of the list of services.
        val servicesDictionaryKey: JSONObject = json.getJSONObject("yt-video")
        for (l in servicesDictionaryKey.keys()) {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Test", "Current value of l in services dict.: $l")
            GlobalSchema.servicesNode.add(l!!)
        }

        // Retrieve the title helper for services.
        // This is the list of services to display, corresponding to the JSONSchema node name.
        val helperTitleArray: JSONObject = json.getJSONObject("helper-title").getJSONObject("yt-video")
        for (l in GlobalSchema.servicesNode) {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT) Log.d("Groaker-Test", "Current value of added string in the helper array: ${helperTitleArray.getString(l)}")
            GlobalSchema.servicesTitle.add(helperTitleArray.getString(l))
        }

    }

}