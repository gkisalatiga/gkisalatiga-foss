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
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.rajat.pdfviewer.PdfRendererView
import kotlinx.coroutines.delay
import org.gkisalatiga.plus.composable.YouTubeView
import org.gkisalatiga.plus.composable.YouTubeViewCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.db.Gallery
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.GallerySaver

import org.gkisalatiga.plus.db.Static
import org.gkisalatiga.plus.db.StaticCompanion
import org.gkisalatiga.plus.fragment.FragmentGalleryListCompanion
import org.gkisalatiga.plus.fragment.FragmentHomeCompanion
import org.gkisalatiga.plus.fragment.FragmentInfoCompanion
import org.gkisalatiga.plus.fragment.FragmentServicesCompanion
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.PreferenceKeys
import org.gkisalatiga.plus.screen.ScreenAbout
import org.gkisalatiga.plus.screen.ScreenAboutCompanion
import org.gkisalatiga.plus.screen.ScreenAgenda
import org.gkisalatiga.plus.screen.ScreenAgendaCompanion
import org.gkisalatiga.plus.screen.ScreenAttribution
import org.gkisalatiga.plus.screen.ScreenAttributionCompanion
import org.gkisalatiga.plus.screen.ScreenBible
import org.gkisalatiga.plus.screen.ScreenContrib
import org.gkisalatiga.plus.screen.ScreenContribCompanion
import org.gkisalatiga.plus.screen.ScreenDev
import org.gkisalatiga.plus.screen.ScreenDevCompanion
import org.gkisalatiga.plus.screen.ScreenForms
import org.gkisalatiga.plus.screen.ScreenFormsCompanion
import org.gkisalatiga.plus.screen.ScreenGaleri
import org.gkisalatiga.plus.screen.ScreenGaleriCompanion
import org.gkisalatiga.plus.screen.ScreenGaleriList
import org.gkisalatiga.plus.screen.ScreenGaleriView
import org.gkisalatiga.plus.screen.ScreenGaleriYear
import org.gkisalatiga.plus.screen.ScreenInternalHTML
import org.gkisalatiga.plus.screen.ScreenLibrary
import org.gkisalatiga.plus.screen.ScreenLibraryCompanion
import org.gkisalatiga.plus.screen.ScreenLicense
import org.gkisalatiga.plus.screen.ScreenLicenseCompanion
import org.gkisalatiga.plus.screen.ScreenLiturgi
import org.gkisalatiga.plus.screen.ScreenMain
import org.gkisalatiga.plus.screen.ScreenMedia
import org.gkisalatiga.plus.screen.ScreenMediaCompanion
import org.gkisalatiga.plus.screen.ScreenPDFViewer
import org.gkisalatiga.plus.screen.ScreenPersembahan
import org.gkisalatiga.plus.screen.ScreenPersembahanCompanion
import org.gkisalatiga.plus.screen.ScreenPosterViewer
import org.gkisalatiga.plus.screen.ScreenPrivacy
import org.gkisalatiga.plus.screen.ScreenPrivacyCompanion
import org.gkisalatiga.plus.screen.ScreenPukatBerkat
import org.gkisalatiga.plus.screen.ScreenPukatBerkatCompanion
import org.gkisalatiga.plus.screen.ScreenSearch
import org.gkisalatiga.plus.screen.ScreenSettings
import org.gkisalatiga.plus.screen.ScreenStaticContentList
import org.gkisalatiga.plus.screen.ScreenStaticContentListCompanion
import org.gkisalatiga.plus.screen.ScreenVideoList
import org.gkisalatiga.plus.screen.ScreenVideoLive
import org.gkisalatiga.plus.screen.ScreenWarta
import org.gkisalatiga.plus.screen.ScreenWebView
import org.gkisalatiga.plus.screen.ScreenYKB
import org.gkisalatiga.plus.screen.ScreenYKBCompanion
import org.gkisalatiga.plus.screen.ScreenYKBList
import org.gkisalatiga.plus.screen.ScreenYKBListCompanion
import org.gkisalatiga.plus.services.ClipManager
import org.gkisalatiga.plus.services.ConnectionChecker
import org.gkisalatiga.plus.services.DataUpdater
import org.gkisalatiga.plus.services.DeepLinkHandler
import org.gkisalatiga.plus.services.NotificationService
import org.gkisalatiga.plus.services.WorkScheduler
import org.gkisalatiga.plus.ui.theme.GKISalatigaPlusTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
class ActivityLauncher : ComponentActivity() {

    override fun onPause() {
        super.onPause()
        GlobalCompanion.isRunningInBackground.value = true
        Logger.log({}, "App is now in background.")
    }

    override fun onResume() {
        super.onResume()
        GlobalCompanion.isRunningInBackground.value = false
        Logger.log({}, "App has been restored to foreground.")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        /* Handles deep-linking. */
        intent?.data?.let {
            Logger.logTest({}, "Received data: host: ${it.host}, path: ${it.path}, encodedPath: ${it.encodedPath}, pathSegments: ${it.pathSegments}")
            if (it.host == "gkisalatiga.org" || it.host == "www.gkisalatiga.org") {
                when (it.encodedPath) {
                    "/app/deeplink/saren" -> DeepLinkHandler.handleSaRen()
                    "/app/deeplink/ykb" -> DeepLinkHandler.handleYKB()
                    else -> if (it.encodedPath != null) DeepLinkHandler.openDomainURL("https://${it.host}${it.encodedPath}")
                }
            }
        }  // --- end of intent?.data?.let {}
    }  // --- end of onNewIntent.

    @SuppressLint("MissingSuperCall", "Recycle")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == GlobalCompanion.GALLERY_SAVER_CODE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                // Decode the URI path.
                // SOURCE: https://www.perplexity.ai/search/kotlin-how-to-download-file-to-h.TAGPj2R5yOTTZ_d3ebKQ
                val contentResolver = applicationContext.contentResolver
                val outputStream = contentResolver.openOutputStream(uri)

                // Perform operations on the document using its URI.
                Logger.logDump({}, uri.path!!)
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
        Logger.log({}, "Starting app: ${this.resources.getString(R.string.app_name_alias)}")

        // Call the superclass. (The default behavior. DO NOT CHANGE!)
        super.onCreate(savedInstanceState)

        // Setting the clipboard manager.
        // Should be performed within "onCreate" to avoid the following error:
        // java.lang.IllegalStateException: System services not available to Activities before onCreate()
        ClipManager.clipManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // Prepares the global YouTube composable and viewer.
        // Prevents NPE.
        YouTubeViewCompanion.composable = YouTubeView()

        // Retrieving the latest JSON metadata.
        initData()

        // Block the app until all data is initialized.
        // Prevents "null pointer exception" when the JSON data in the multi-thread has not been prepared.
        while (true) {
            if (MainCompanion.jsonRoot != null && ModulesCompanion.jsonRoot != null && GalleryCompanion.jsonRoot != null && StaticCompanion.jsonRoot != null) {
                break
            } else {
                Logger.logRapidTest({}, "Still initializing data ...", LoggerType.WARNING)
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

            // TODO: DEBUG: Remove this code because it is debug.
            GlobalCompanion.pdfRendererViewInstance = PdfRendererView(this)

            // Initializes the scroll states and lazy scroll states.
            FragmentGalleryListCompanion.rememberedLazyGridState = rememberLazyGridState()
            FragmentHomeCompanion.rememberedScrollState = rememberScrollState()
            FragmentInfoCompanion.rememberedScrollState = rememberScrollState()
            FragmentServicesCompanion.rememberedScrollState = rememberScrollState()
            ScreenAboutCompanion.rememberedScrollState = rememberScrollState()
            ScreenAgendaCompanion.rememberedDayListScrollState = rememberScrollState()
            ScreenAgendaCompanion.rememberedScrollState = rememberScrollState()
            ScreenAttributionCompanion.rememberedScrollState = rememberScrollState()
            ScreenContribCompanion.rememberedScrollState = rememberScrollState()
            ScreenDevCompanion.rememberedScrollState = rememberScrollState()
            ScreenFormsCompanion.rememberedScrollState = rememberScrollState()
            ScreenGaleriCompanion.rememberedScrollState = rememberScrollState()
            ScreenLibraryCompanion.rememberedScrollState = rememberScrollState()
            ScreenLicenseCompanion.rememberedScrollState = rememberScrollState()
            ScreenMediaCompanion.rememberedScrollState = rememberScrollState()
            ScreenPersembahanCompanion.rememberedScrollState = rememberScrollState()
            ScreenPrivacyCompanion.rememberedScrollState = rememberScrollState()
            ScreenPukatBerkatCompanion.rememberedScrollStateFood = rememberScrollState()
            ScreenPukatBerkatCompanion.rememberedScrollStateGoods = rememberScrollState()
            ScreenPukatBerkatCompanion.rememberedScrollStateService = rememberScrollState()
            ScreenStaticContentListCompanion.rememberedScrollState = rememberScrollState()
            ScreenYKBCompanion.rememberedScrollState = rememberScrollState()
            ScreenYKBListCompanion.rememberedScrollState = rememberScrollState()

            // Pre-assign pager states of non-main menus.
            ScreenPukatBerkatCompanion.pukatBerkatPagerState = rememberPagerState ( pageCount = {3}, initialPage = 0 )

            // Prepare the pull-to-refresh (PTR) state globally.
            GlobalCompanion.globalPTRState = rememberPullToRefreshState()

            // Listen to the request to hide the phone's bars.
            // SOURCE: https://developer.android.com/develop/ui/views/layout/immersive
            key (GlobalCompanion.isPhoneBarsVisible.value) {
                if (GlobalCompanion.isPhoneBarsVisible.value) {
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                } else {
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                }
            }

            GKISalatigaPlusTheme {
                if (!GlobalCompanion.DEBUG_DISABLE_SPLASH_SCREEN) {
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
                                        Logger.logTest({}, "Received data: host: ${it.host}, path: ${it.path}, encodedPath: ${it.encodedPath}, pathSegments: ${it.pathSegments}")
                                        if (it.host == "gkisalatiga.org" || it.host == "www.gkisalatiga.org") {
                                            when (it.encodedPath) {
                                                "/app/deeplink/saren" -> DeepLinkHandler.handleSaRen()
                                                "/app/deeplink/ykb" -> DeepLinkHandler.handleYKB()
                                                else -> if (it.encodedPath != null) DeepLinkHandler.openDomainURL("https://${it.host}${it.encodedPath}")
                                            }
                                            // This activity was called from a URI call. Skip the splash screen.
                                            initMainGraphic()
                                        }
                                    }  // --- end of intent?.data?.let {}

                                /* Nothing matches, start the app from the beginning.*/
                                } else {
                                    // This isn't a URI action call. Open the app regularly.
                                    initSplashScreen(splashNavController)
                                }
                            }  // --- end of navigation composable.

                            composable ("main_screen") {
                                // Just display the main graphic directly.
                                initMainGraphic()
                            }  // --- end of navigation composable.
                        }  // --- end of NavHost.
                    }
                } else {
                    // Just display the main graphic directly.
                    initMainGraphic()
                }
            }  // --- end of GKISalatigaPlusTheme.
        }  // --- end of setContent().
    }  // --- end of onCreate().

    /**
     * This method reads the current saved preference associated with the app
     * and pass it to the GlobalCompanion so that other functions can use them.
     */
    private fun initPreferences() {
        // Initializes the preferences.
        val appLocalStorage = LocalStorage(this)

        // Initializes the default/fallback preferences if this is a first launch.
        if ((appLocalStorage.getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS) as Int) < 0) appLocalStorage.initDefaultLocalStorage()

        // Increment the number of counts.
        val now = appLocalStorage.getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS) as Int
        appLocalStorage.setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS, now + 1)
        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(this, "Launches since install: ${now + 1}", Toast.LENGTH_SHORT).show()
    }

    /**
     * This method determines what is shown during splash screen.
     * @param schema the app's custom configurable schema, shared across composables.
     */
    @Composable
    private fun initSplashScreen(splashNavController: NavHostController) {
        Logger.log({}, "Loading splash screen of the app ...")

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
    @SuppressLint("ComposableNaming")
    private fun initMainGraphic() {
        Logger.logInit({}, "Initializing main graphic with the current screen route: ${AppNavigation.mutableCurrentNavigationRoute.value.name} ...")

        // We use nav. host because it has built-in support for transition effect/animation.
        // We also use nav. host so that we can handle URI deep-linking, both from an external URL click and from a notification click.
        // SOURCE: https://composables.com/tutorials/deeplinks
        val mainNavController = rememberNavController()
        NavHost(navController = mainNavController, startDestination = AppNavigation.startingScreenRoute.name) {
            composable(NavigationRoutes.SCREEN_ABOUT.name) { ScreenAbout().getComposable() }
            composable(NavigationRoutes.SCREEN_AGENDA.name) { ScreenAgenda().getComposable() }
            composable(NavigationRoutes.SCREEN_ATTRIBUTION.name) { ScreenAttribution().getComposable() }
            composable(NavigationRoutes.SCREEN_BIBLE.name) {ScreenBible().getComposable()}
            composable(NavigationRoutes.SCREEN_CONTRIB.name) { ScreenContrib().getComposable() }
            composable(NavigationRoutes.SCREEN_DEV.name) { ScreenDev().getComposable() }
            composable(NavigationRoutes.SCREEN_FORMS.name) { ScreenForms().getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI.name) { ScreenGaleri().getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI_LIST.name) { ScreenGaleriList().getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI_VIEW.name) { ScreenGaleriView().getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI_YEAR.name) { ScreenGaleriYear().getComposable() }
            composable(NavigationRoutes.SCREEN_INTERNAL_HTML.name) { ScreenInternalHTML().getComposable() }
            composable(NavigationRoutes.SCREEN_LIBRARY.name) {ScreenLibrary().getComposable()}
            composable(NavigationRoutes.SCREEN_LICENSE.name) { ScreenLicense().getComposable() }
            composable(NavigationRoutes.SCREEN_LITURGI.name) { ScreenLiturgi().getComposable() }
            composable(NavigationRoutes.SCREEN_LIVE.name) { ScreenVideoLive().getComposable() }
            composable(NavigationRoutes.SCREEN_MAIN.name) { ScreenMain().getComposable() }
            composable(NavigationRoutes.SCREEN_MEDIA.name) { ScreenMedia().getComposable() }
            composable(NavigationRoutes.SCREEN_PDF_VIEWER.name) { ScreenPDFViewer().getComposable() }
            composable(NavigationRoutes.SCREEN_PERSEMBAHAN.name) { ScreenPersembahan().getComposable() }
            composable(NavigationRoutes.SCREEN_POSTER_VIEWER.name) { ScreenPosterViewer().getComposable() }
            composable(NavigationRoutes.SCREEN_PRIVACY.name) { ScreenPrivacy().getComposable() }
            composable(NavigationRoutes.SCREEN_PUKAT_BERKAT.name) {ScreenPukatBerkat().getComposable()}
            composable(NavigationRoutes.SCREEN_SEARCH.name) {ScreenSearch().getComposable()}
            composable(NavigationRoutes.SCREEN_SETTINGS.name) {ScreenSettings().getComposable()}
            composable(NavigationRoutes.SCREEN_STATIC_CONTENT_LIST.name) { ScreenStaticContentList().getComposable() }
            composable(NavigationRoutes.SCREEN_VIDEO_LIST.name) { ScreenVideoList().getComposable() }
            composable(NavigationRoutes.SCREEN_WARTA.name) { ScreenWarta().getComposable() }
            composable(NavigationRoutes.SCREEN_WEBVIEW.name) { ScreenWebView().getComposable() }
            composable(NavigationRoutes.SCREEN_YKB.name) {ScreenYKB().getComposable()}
            composable(NavigationRoutes.SCREEN_YKB_LIST.name) {ScreenYKBList().getComposable()}
        }

        // Watch for the state change in the parameter "currentNavigationRoute".
        // SOURCE: https://stackoverflow.com/a/73129228
        key(AppNavigation.mutableCurrentNavigationRoute.value, AppNavigation.mutableRecomposeCurrentScreen.value) {
            mainNavController.navigate(AppNavigation.mutableCurrentNavigationRoute.value.name)
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
     * This function initializes the zip archive files containing
     * the app data of GKI Salatiga Plus.
     * ---
     * This function does not need to become a composable function since it requires no UI.
     */
    private fun initData() {

        // The file creator to create the private file.
        val fileCreator = this.getDir(GlobalCompanion.FILE_CREATOR_TARGET_DOWNLOAD_DIR, Context.MODE_PRIVATE)

        // Setting up the downloaded JSON's absolute paths.
        Logger.logInit({}, "Initializing the downloaded JSON paths ...")
        MainCompanion.absolutePathToJSONFile = File(fileCreator, MainCompanion.savedFilename).absolutePath
        ModulesCompanion.absolutePathToJSONFile = File(fileCreator, ModulesCompanion.savedFilename).absolutePath
        GalleryCompanion.absolutePathToJSONFile = File(fileCreator, GalleryCompanion.savedFilename).absolutePath
        StaticCompanion.absolutePathToJSONFile = File(fileCreator, StaticCompanion.savedFilename).absolutePath

        // Get the number of launches since install so that we can determine
        // whether to use the fallback data.
        val launches = LocalStorage(this).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS) as Int

        // Get fallback data only if first launch.
        if (launches == 0) {
            // Let's apply the fallback JSON data until the actual, updated JSON metadata is downloaded.
            Logger.logInit({}, "Loading the fallback JSON metadata ...")
            Main(this).initFallbackGalleryData()

            // Loading the fallback gallery data.
            Logger.logInit({}, "Loading the fallback gallery JSON file ...")
            Gallery(this).initFallbackGalleryData()

            // Loading the fallback static data.
            Logger.logInit({}, "Loading the fallback static JSON file ...")
            Static(this).initFallbackStaticData()

        } else {
            Logger.logInit({}, "This is not first launch.")
        }

        // At last, update the data to the latest whenever possible.
        DataUpdater(this).updateData()
    }

    @Composable
    private fun initCarouselState() {

        // Use multithread (coroutine scope) so that it won't block the app's execution.
        val scope = rememberCoroutineScope()
        scope.run {
            // "Infinite" pager page scrolling.
            // Please fill the following integer-variable with a number of pages
            // that the user won't bother scrolling.
            // SOURCE: https://stackoverflow.com/a/75469260
            val baseInfiniteScrollingPages = 256  // --- i.e., 2^8.

            // Necessary variables for the infinite-page carousel.
            // SOURCE: https://medium.com/androiddevelopers/customizing-compose-pager-with-fun-indicators-and-transitions-12b3b69af2cc
            val actualPageCount = MainCompanion.jsonRoot!!.getJSONArray("carousel").length()
            val carouselPageCount = actualPageCount * baseInfiniteScrollingPages
            FragmentHomeCompanion.rememberedCarouselPagerState = rememberPagerState(
                initialPage = carouselPageCount / 2,
                pageCount = { carouselPageCount }
            )
        }
    }

}