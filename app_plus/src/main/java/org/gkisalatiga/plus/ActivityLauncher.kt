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
 *
 * Use "result.postValue()" instead of "result.value = ..." to post a LiveData value,
 * in order to enforce asynchronous coroutine and prevents "Cannot invoke setValue on a background thread" error.
 * SOURCE: https://stackoverflow.com/a/60126585
 */

@file:SuppressLint("ComposableNaming")

package org.gkisalatiga.plus

import android.R.attr.name
import android.R.attr.text
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.delay
import org.gkisalatiga.plus.composable.GKISalatigaAppTheme
import org.gkisalatiga.plus.composable.MainPTRCompanion
import org.gkisalatiga.plus.composable.YouTubeView
import org.gkisalatiga.plus.composable.YouTubeViewCompanion
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.Gallery
import org.gkisalatiga.plus.db.GalleryCompanion
import org.gkisalatiga.plus.db.Main
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.Modules
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.db.Static
import org.gkisalatiga.plus.db.StaticCompanion
import org.gkisalatiga.plus.fragment.FragmentHomeCompanion
import org.gkisalatiga.plus.fragment.FragmentInfoCompanion
import org.gkisalatiga.plus.fragment.FragmentServicesCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.AppPreferences
import org.gkisalatiga.plus.lib.Beacon
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.DynamicColorScheme
import org.gkisalatiga.plus.lib.GallerySaver
import org.gkisalatiga.plus.lib.LocalStorage
import org.gkisalatiga.plus.lib.LocalStorageDataTypes
import org.gkisalatiga.plus.lib.LocalStorageKeys
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.lib.PersistentLogger
import org.gkisalatiga.plus.lib.PreferenceKeys
import org.gkisalatiga.plus.screen.ScreenAbout
import org.gkisalatiga.plus.screen.ScreenAboutCompanion
import org.gkisalatiga.plus.screen.ScreenAgenda
import org.gkisalatiga.plus.screen.ScreenAgendaCompanion
import org.gkisalatiga.plus.screen.ScreenAttribution
import org.gkisalatiga.plus.screen.ScreenAttributionCompanion
import org.gkisalatiga.plus.screen.ScreenBible
import org.gkisalatiga.plus.screen.ScreenBibleCompanion
import org.gkisalatiga.plus.screen.ScreenBibleViewer
import org.gkisalatiga.plus.screen.ScreenBibleViewerCompanion
import org.gkisalatiga.plus.screen.ScreenBlank
import org.gkisalatiga.plus.screen.ScreenContrib
import org.gkisalatiga.plus.screen.ScreenContribCompanion
import org.gkisalatiga.plus.screen.ScreenDev
import org.gkisalatiga.plus.screen.ScreenDevCompanion
import org.gkisalatiga.plus.screen.ScreenForms
import org.gkisalatiga.plus.screen.ScreenFormsCompanion
import org.gkisalatiga.plus.screen.ScreenGaleri
import org.gkisalatiga.plus.screen.ScreenGaleriCompanion
import org.gkisalatiga.plus.screen.ScreenGaleriList
import org.gkisalatiga.plus.screen.ScreenGaleriListCompanion
import org.gkisalatiga.plus.screen.ScreenGaleriView
import org.gkisalatiga.plus.screen.ScreenGaleriYear
import org.gkisalatiga.plus.screen.ScreenGaleriYearCompanion
import org.gkisalatiga.plus.screen.ScreenInspiration
import org.gkisalatiga.plus.screen.ScreenInspirationCompanion
import org.gkisalatiga.plus.screen.ScreenInspirationSpinwheelCompanion
import org.gkisalatiga.plus.screen.ScreenInspirationSpinwheel
import org.gkisalatiga.plus.screen.ScreenInternalHTML
import org.gkisalatiga.plus.screen.ScreenLibrary
import org.gkisalatiga.plus.screen.ScreenLibraryCompanion
import org.gkisalatiga.plus.screen.ScreenLicense
import org.gkisalatiga.plus.screen.ScreenLicenseCompanion
import org.gkisalatiga.plus.screen.ScreenLiturgi
import org.gkisalatiga.plus.screen.ScreenLiturgiCompanion
import org.gkisalatiga.plus.screen.ScreenMain
import org.gkisalatiga.plus.screen.ScreenMainCompanion
import org.gkisalatiga.plus.screen.ScreenMedia
import org.gkisalatiga.plus.screen.ScreenMediaCompanion
import org.gkisalatiga.plus.screen.ScreenPDFViewer
import org.gkisalatiga.plus.screen.ScreenPDFViewerCompanion
import org.gkisalatiga.plus.screen.ScreenPersembahan
import org.gkisalatiga.plus.screen.ScreenPersembahanCompanion
import org.gkisalatiga.plus.screen.ScreenPosterViewer
import org.gkisalatiga.plus.screen.ScreenPosterViewerLegacy
import org.gkisalatiga.plus.screen.ScreenPrivacy
import org.gkisalatiga.plus.screen.ScreenPrivacyCompanion
import org.gkisalatiga.plus.screen.ScreenPukatBerkat
import org.gkisalatiga.plus.screen.ScreenPukatBerkatCompanion
import org.gkisalatiga.plus.screen.ScreenSearch
import org.gkisalatiga.plus.screen.ScreenSearchCompanion
import org.gkisalatiga.plus.screen.ScreenSeasonal
import org.gkisalatiga.plus.screen.ScreenSeasonalCompanion
import org.gkisalatiga.plus.screen.ScreenSettings
import org.gkisalatiga.plus.screen.ScreenSettingsCompanion
import org.gkisalatiga.plus.screen.ScreenStaticContentList
import org.gkisalatiga.plus.screen.ScreenStaticContentListCompanion
import org.gkisalatiga.plus.screen.ScreenVideoList
import org.gkisalatiga.plus.screen.ScreenVideoListCompanion
import org.gkisalatiga.plus.screen.ScreenVideoLive
import org.gkisalatiga.plus.screen.ScreenWarta
import org.gkisalatiga.plus.screen.ScreenWartaCompanion
import org.gkisalatiga.plus.screen.ScreenWebView
import org.gkisalatiga.plus.screen.ScreenYKB
import org.gkisalatiga.plus.screen.ScreenYKBCompanion
import org.gkisalatiga.plus.screen.ScreenYKBList
import org.gkisalatiga.plus.screen.ScreenYKBListCompanion
import org.gkisalatiga.plus.services.ClipManager
import org.gkisalatiga.plus.services.ConnectionChecker
import org.gkisalatiga.plus.services.DataUpdater
import org.gkisalatiga.plus.services.DeepLinkHandler
import org.gkisalatiga.plus.services.EnableDevMode
import org.gkisalatiga.plus.services.InternalFileManager
import org.gkisalatiga.plus.services.NotificationService
import org.gkisalatiga.plus.services.PermissionChecker
import org.gkisalatiga.plus.services.WorkScheduler
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
class ActivityLauncher : ComponentActivity() {

    // Declaring the Firebase analytics service.
    // SOURCE: https://firebase.google.com/docs/analytics/get-started?platform=android
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onPause() {
        super.onPause()
        GlobalCompanion.isRunningInBackground.value = true
        Logger.log({}, "App is now in background.")
    }

    override fun onResume() {
        super.onResume()
        GlobalCompanion.isRunningInBackground.value = false
        Logger.log({}, "App has been restored to foreground.")

        PermissionChecker(this@ActivityLauncher).checkNotificationPermission()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent, consumeAfterHandling = false)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    @SuppressLint("MissingSuperCall", "Recycle")
    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == GallerySaver.GALLERY_SAVER_CODE && resultCode == Activity.RESULT_OK) {
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

        // Preamble logging to the terminal.
        Logger.log({}, "Starting app: ${this.resources.getString(R.string.app_name_alias)}")

        // Enable edge-to-edge.
        // ---
        // WARNING!
        // No matter wScreenBibleCompanionhat you think, do not call enableEdgeToEdge() outside the scope
        // of Jetpack Compose's setContent () -> Unit.
        // The Google Play Console edge-to-edge warning won't disappear anyway,
        // and a pesky default top bar would show up on higher Android devices instead.
        // (github.com/samarlyka, 2025-10-11)
        // enableEdgeToEdge()

        // Handle the splash screen transition.
        // SOURCE: https://developer.android.com/develop/ui/views/launch/splash-screen/migrate
        // val splashScreen = installSplashScreen()
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) installSplashScreen()
        val splashScreen = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) installSplashScreen() else null
        // if (false) installSplashScreen()

        // Handle notification permission.
        PermissionChecker(this@ActivityLauncher).checkNotificationPermission()

        // Call the superclass. (The default behavior. DO NOT CHANGE!)
        super.onCreate(savedInstanceState)

        // Initializing the Firebase analytics object.
        firebaseAnalytics = Firebase.analytics

        // Keep the splash screen visible for this Activity.
        // splashScreen.setKeepOnScreenCondition { true }

        // Initializes the app's internally saved preferences.
        initPreferencesAndLocalStorage()

        // Unlocks the dev menu if the app is debuggable.
        val isDevModeUnlocked = LocalStorage(this@ActivityLauncher).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_DEVELOPER_MENU_UNLOCKED, LocalStorageDataTypes.BOOLEAN) as Boolean
        val currentClassName = "org.gkisalatiga.plus.ActivityLauncher"
        fun x(y: () -> Unit): String { return "${y.javaClass.enclosingClass?.name}".trim() }
        if (this@ActivityLauncher.packageName.endsWith(".debug") && !isDevModeUnlocked) {
            // Unlocks the dev menu.
            PersistentLogger(this@ActivityLauncher).write({}, "The developer menu automatically unlocks by debug package suffix!")
            LocalStorage(this@ActivityLauncher).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_DEVELOPER_MENU_UNLOCKED, true, LocalStorageDataTypes.BOOLEAN)
            EnableDevMode.activateDebugToggles()
        } else if (x {} == currentClassName && !isDevModeUnlocked) {
            PersistentLogger(this@ActivityLauncher).write({}, "The developer menu automatically unlocks by unobfuscated R8 app build detection!")
            LocalStorage(this@ActivityLauncher).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_DEVELOPER_MENU_UNLOCKED, true, LocalStorageDataTypes.BOOLEAN)
            EnableDevMode.activateDebugToggles()
        }

        // Updating the debug status.
        GlobalCompanion.isAppDebuggable.value = x {} == currentClassName

        // Start the connection (online/offline) checker.
        ConnectionChecker(this).execute()

        // Configure the behavior of the hidden system bars and configure the immersive mode (hide status bar and navigation bar).
        // SOURCE: https://developer.android.com/develop/ui/views/layout/immersive
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Lock the screen's orientation to portrait mode only.
        val targetOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        this.requestedOrientation = targetOrientation

        // Enable on-the-fly edit of drawable SVG vectors.
        // SOURCE: https://stackoverflow.com/a/38418049
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        // Setting the clipboard manager.
        // Should be performed within "onCreate" to avoid the following error:
        // java.lang.IllegalStateException: System services not available to Activities before onCreate()
        ClipManager.clipManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // Prepares the global YouTube composable and viewer.
        // Prevents NPE.
        YouTubeViewCompanion.composable = YouTubeView()

        // Retrieving the latest JSON metadata.
        initData()

        // Retrieving app info.
        val pInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        val vName = pInfo.versionName
        val vCode = pInfo.versionCode

        // Update the last app version code used.
        // Must be done after data initialization in initData().
        LocalStorage(this).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAST_APP_VERSION_CODE, vCode, LocalStorageDataTypes.INT)

        // Creating the notification channels.
        initNotificationChannel()

        // Initializing the scheduled alarms.
        initWorkManager()

        // Cleaning up stuffs.
        initCleanUp()

        // Initiate the Jetpack Compose composition.
        // This is the entry point of every composable, similar to "main()" function in Java.
        setContent {

            // Check for dark mode UI state.
            val prefDarkMode = AppPreferences(this).getPreferenceValue(PreferenceKeys.PREF_KEY_THEME_UI) as String
            val isDarkMode = (isSystemInDarkTheme() && prefDarkMode == "system") || prefDarkMode == "dark"
            GlobalCompanion.isDarkModeUi.value = isDarkMode

            // Enable transparent status bar.
            // ---
            // DO NOT MOVE OUTSIDE setContent () -> Unit.
            // It will get superseded by Android's default top bar,
            // overlapping the Jetpack Compose's top bar.
            enableEdgeToEdge(
                statusBarStyle = if (isDarkMode)
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                else
                    SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
            )

            // Try to remember the state of the carousel.
            initCarouselState()

            // Initializes the scroll states and lazy scroll states.
            FragmentHomeCompanion.rememberedScrollState = rememberScrollState()
            FragmentInfoCompanion.rememberedScrollState = rememberScrollState()
            FragmentServicesCompanion.rememberedScrollState = rememberScrollState()
            ScreenAboutCompanion.rememberedScrollState = rememberScrollState()
            ScreenAgendaCompanion.rememberedScrollState = rememberScrollState()
            ScreenAttributionCompanion.rememberedHardCodedScrollState = rememberScrollState()
            ScreenAttributionCompanion.rememberedLiteratureScrollState = rememberScrollState()
            ScreenAttributionCompanion.rememberedWebViewScrollState = rememberScrollState()
            ScreenBibleCompanion.rememberedScrollState = rememberScrollState()
            ScreenContribCompanion.rememberedScrollState = rememberScrollState()
            ScreenDevCompanion.rememberedScrollState = rememberScrollState()
            ScreenFormsCompanion.rememberedScrollState = rememberScrollState()
            ScreenGaleriCompanion.rememberedScrollState = rememberScrollState()
            ScreenGaleriYearCompanion.rememberedScrollState = rememberScrollState()
            ScreenGaleriListCompanion.rememberedLazyGridState = rememberLazyGridState()
            ScreenInspirationCompanion.rememberedScrollState = rememberScrollState()
            ScreenInspirationSpinwheelCompanion.rememberedScrollState = rememberScrollState()
            ScreenLibraryCompanion.rememberedScrollState = rememberScrollState()
            ScreenLicenseCompanion.rememberedScrollState = rememberScrollState()
            ScreenLiturgiCompanion.rememberedScrollState = rememberScrollState()
            ScreenMediaCompanion.rememberedScrollState = rememberScrollState()
            ScreenBibleViewerCompanion.navigatorLazyListState = rememberLazyListState()
            ScreenPDFViewerCompanion.navigatorLazyListState = rememberLazyListState()
            ScreenPersembahanCompanion.rememberedScrollState = rememberScrollState()
            ScreenPrivacyCompanion.rememberedScrollState = rememberScrollState()
            ScreenPukatBerkatCompanion.rememberedScrollStateFood = rememberScrollState()
            ScreenPukatBerkatCompanion.rememberedScrollStateGoods = rememberScrollState()
            ScreenPukatBerkatCompanion.rememberedScrollStateService = rememberScrollState()
            ScreenSearchCompanion.rememberedLazyListState = rememberLazyListState()
            ScreenSeasonalCompanion.rememberedScrollState = rememberScrollState()
            ScreenSettingsCompanion.rememberedScrollState = rememberScrollState()
            ScreenStaticContentListCompanion.rememberedScrollState = rememberScrollState()
            ScreenVideoListCompanion.rememberedScrollState = rememberScrollState()
            ScreenWartaCompanion.rememberedScrollState = rememberScrollState()
            ScreenYKBCompanion.rememberedScrollState = rememberScrollState()
            ScreenYKBListCompanion.rememberedScrollState = rememberScrollState()

            // Pre-assign the overlay gradient.
            val gradientColorSet = if (isDarkMode) DynamicColorScheme.DarkColorScheme() else DynamicColorScheme.LightColorScheme()
            ScreenMainCompanion.renderedOverlayGradient = Brush.verticalGradient(colorStops = arrayOf (
                0.0f to Colors.SCREEN_MAIN_OVERLAY_GRADIENT_TOP_COLOR,
                0.5f to Colors.SCREEN_MAIN_OVERLAY_GRADIENT_MIDDLE_COLOR,
                1.0f to gradientColorSet.screenMainOverlayGradientBottomColor
            ))

            // Pre-assign pager states of non-main menus.
            ScreenAttributionCompanion.attributionPagerState = rememberPagerState ( pageCount = {3}, initialPage = 0 )
            ScreenPukatBerkatCompanion.pukatBerkatPagerState = rememberPagerState ( pageCount = {3}, initialPage = 0 )

            // Prepare the pull-to-refresh (PTR) state globally.
            MainPTRCompanion.mainPTRState = rememberPullToRefreshState()

            // Mutable state for spin wheels.
            ScreenInspirationSpinwheelCompanion.mutableShuffledIconData = mutableStateOf(ScreenInspirationSpinwheel.defaultIconPools)
            ScreenInspirationSpinwheelCompanion.mutableShuffledSpinWheelData = mutableStateOf(mutableListOf())

            // Listen to the request to hide the phone's bars.
            // SOURCE: https://developer.android.com/develop/ui/views/layout/immersive
            key (GlobalCompanion.isPhoneBarsVisible.value) {
                if (GlobalCompanion.isPhoneBarsVisible.value) {
                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                } else {
                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                }
            }

            GKISalatigaAppTheme (darkTheme = isDarkMode) {
                // Display splash screen.
                Surface(color = if (isDarkMode) Color.Black else Color.White, modifier = Modifier.fillMaxSize()) {
                    val splashNavController = rememberNavController()
                    NavHost(navController = splashNavController, startDestination = "init_screen") {
                        composable("init_screen", deepLinks = listOf(navDeepLink { uriPattern = "https://gkisalatiga.org" }, navDeepLink { uriPattern = "https://www.gkisalatiga.org" })) {
                            if (intent?.data == null) {
                                /* Display the splash screen because no intent is passed for this session. */
                                // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) initSplashScreen()
                                // if (true) initSplashScreen()
                                // else initMainGraphic()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) splashScreen!!.setKeepOnScreenCondition { false }
                                initSplashScreen()
                            }
                            else {
                                handleDeepLink(intent, consumeAfterHandling = true); initMainGraphic()
                            }
                        }

                        composable ("main_screen") { initMainGraphic() }
                    }  // --- end of NavHost.
                }
            }  // --- end of GKISalatigaPlusTheme.
        }  // --- end of setContent().
    }  // --- end of onCreate().

    /**
     * This function handles how new deep links (e.g., upon clicking a notification)
     * are treated in the app. This function also alters the intent data to prevent bugs
     * similar to issue #72.
     * @param intent the intent that manages the deep link information and carries information about the deep link routings.
     * @param consumeAfterHandling whether the intent data should be consumed after deep link handling. Defaults to "true".
     */
    private fun handleDeepLink(intent: Intent?, consumeAfterHandling: Boolean = true) {
        Logger.logDeepLink({}, "Handling deep link -> intent: $intent, consumeAfterHandling: $consumeAfterHandling")
        if (intent?.data != null) {
            val uri = intent.data!!
            Logger.logDeepLink({}, "Received data: host: ${uri.host}, path: ${uri.path}, encodedPath: ${uri.encodedPath}, pathSegments: ${uri.pathSegments}")

            if (uri.host == "gkisalatiga.org" || uri.host == "www.gkisalatiga.org") {
                when (uri.encodedPath) {
                    "/app/deeplink/consumption" -> Unit
                    "/app/deeplink/contributors" -> DeepLinkHandler.handleContributors()
                    "/app/deeplink/main_graphics" -> DeepLinkHandler.handleMainGraphics()
                    "/app/deeplink/saren" -> DeepLinkHandler.handleSaRen()
                    "/app/deeplink/ykb" -> DeepLinkHandler.handleYKB()
                    else -> if (uri.encodedPath != null) DeepLinkHandler.openDomainURL("https://${uri.host}${uri.encodedPath}")
                }

                // After deeplink handling, we must consume the intent data and replace it with other values.
                // This solves issue: https://github.com/gkisalatiga/gkisalatiga-foss/issues/72.
                if (consumeAfterHandling) intent.setData(Uri.parse("https://gkisalatiga.org/app/deeplink/consumption"))
            }

        }  // --- end if.
    }  // --- end of handleDeepLink().

    /**
     * This function carries out the cleaning up of stuffs.
     */
    private fun initCleanUp() {
        Logger.logInit({}, "Cleaning up old PDF files ...")
        InternalFileManager(this@ActivityLauncher).doPdfCleanUp()

        Logger.logInit({}, "Cleaning up old persistent logger entries ...")
        PersistentLogger(this@ActivityLauncher).cleanOldEntries()
    }

    /**
     * This method reads the current saved preference associated with the app
     * and pass it to the GlobalCompanion so that other functions can use them.
     */
    private fun initPreferencesAndLocalStorage() {
        // Initializes the default/fallback preferences and launch value if this is a first launch.
        if ((LocalStorage(this).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS, LocalStorageDataTypes.INT) as Int) <= 0) {
            AppPreferences(this).initDefaultPreferences()
            LocalStorage(this).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS, 0, LocalStorageDataTypes.INT)
            PersistentLogger(this).write({}, "This is first app launch.")
        }

        // Increment the number of launch counts.
        val now = LocalStorage(this).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS, LocalStorageDataTypes.INT) as Int
        LocalStorage(this).setLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS, now + 1, LocalStorageDataTypes.INT)
        if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(this, "Launches since install: ${now + 1}", Toast.LENGTH_SHORT).show()

        // Reporting the launch to Firebase.
        Beacon(firebaseAnalytics).logAppLaunch()

        // Developer mode.
        val isDevMode = LocalStorage(this).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_IS_DEVELOPER_MENU_UNLOCKED, LocalStorageDataTypes.BOOLEAN) as Boolean
        if (isDevMode) EnableDevMode.activateDebugToggles() else EnableDevMode.disableDebugToggles()
    }

    /**
     * This method determines what is shown during splash screen.
     */
    @Composable
    private fun initSplashScreen() {
        Surface(color = Color.White, modifier = Modifier.fillMaxSize()) {
            val splashNavController = rememberNavController()
            NavHost(navController = splashNavController, startDestination = "init_screen") {
                composable("init_screen", deepLinks = listOf(navDeepLink { uriPattern = "https://gkisalatiga.org" }, navDeepLink { uriPattern = "https://www.gkisalatiga.org" })) {
                    Logger.logInit({}, "Loading splash screen (legacy Android API) of the app ...")
                    LaunchedEffect(key1 = true) {
                        // Determines the duration of the splash screen.
                        delay(1500)
                        splashNavController.navigate("main_screen")
                    }

                    // Displays the splash screen content.
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize().background(DynamicColorScheme.DarkColorScheme().mainSplashScreenBackgroundColor)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.splash_screen_icon_82),
                            contentDescription = "Splash screen logo",
                            colorFilter = ColorFilter.tint(DynamicColorScheme.DarkColorScheme().mainSplashScreenForegroundColor),
                            modifier = Modifier.scale(0.65f)
                        )
                    }
                }

                composable ("main_screen") { initMainGraphic() }
            }
        }
    }

    /**
     * This method will become the navigation hub of screens across composables.
     * It also becomes the graphical base of all screens.
     */
    @Composable
    private fun initMainGraphic() {
        Logger.logInit({}, "Initializing main graphic with the current screen route: ${AppNavigation.mutableCurrentNavigationRoute.value.name} ...")

        // Prepare the activity-wide context variables.
        val current = ActivityData(
            ctx = this@ActivityLauncher,
            firebaseAnalytics = this@ActivityLauncher.firebaseAnalytics,
            scope = rememberCoroutineScope(),
            lifecycleOwner = LocalLifecycleOwner.current,
            lifecycleScope = this@ActivityLauncher.lifecycleScope,
            keyboardController = LocalSoftwareKeyboardController.current,
            colors = if (GlobalCompanion.isDarkModeUi.value) DynamicColorScheme.DarkColorScheme() else DynamicColorScheme.LightColorScheme(),
            uriHandler = LocalUriHandler.current,
        )

        // We use nav. host because it has built-in support for transition effect/animation.
        // We also use nav. host so that we can handle URI deep-linking, both from an external URL click and from a notification click.
        // SOURCE: https://composables.com/tutorials/deeplinks
        val mainNavController = rememberNavController()
        NavHost(navController = mainNavController, startDestination = AppNavigation.startingScreenRoute.name) {
            composable(NavigationRoutes.SCREEN_ABOUT.name) { ScreenAbout(current).getComposable() }
            composable(NavigationRoutes.SCREEN_AGENDA.name) { ScreenAgenda(current).getComposable() }
            composable(NavigationRoutes.SCREEN_ATTRIBUTION.name) { ScreenAttribution(current).getComposable() }
            composable(NavigationRoutes.SCREEN_BIBLE.name) { ScreenBible(current).getComposable() }
            composable(NavigationRoutes.SCREEN_BIBLE_VIEWER.name) { ScreenBibleViewer(current).getComposable() }
            composable(NavigationRoutes.SCREEN_BLANK.name) { ScreenBlank(current).getComposable() }
            composable(NavigationRoutes.SCREEN_CONTRIB.name) { ScreenContrib(current).getComposable() }
            composable(NavigationRoutes.SCREEN_DEV.name) { ScreenDev(current).getComposable(); /* You got no Easter egg, just some Christmas gift. */ LaunchedEffect(Unit) { Beacon(current).logScreenOpen(it.destination.route) } }
            composable(NavigationRoutes.SCREEN_FORMS.name) { ScreenForms(current).getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI.name) { ScreenGaleri(current).getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI_LIST.name) { ScreenGaleriList(current).getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI_VIEW.name) { ScreenGaleriView(current).getComposable() }
            composable(NavigationRoutes.SCREEN_GALERI_YEAR.name) { ScreenGaleriYear(current).getComposable() }
            composable(NavigationRoutes.SCREEN_INSPIRATION.name) { ScreenInspiration(current).getComposable() }
            composable(NavigationRoutes.SCREEN_INSPIRATION_SPINWHEEL.name) { ScreenInspirationSpinwheel(current).getComposable() }
            composable(NavigationRoutes.SCREEN_INTERNAL_HTML.name) { ScreenInternalHTML(current).getComposable() }
            composable(NavigationRoutes.SCREEN_LIBRARY.name) {ScreenLibrary(current).getComposable()}
            composable(NavigationRoutes.SCREEN_LICENSE.name) { ScreenLicense(current).getComposable() }
            composable(NavigationRoutes.SCREEN_LITURGI.name) { ScreenLiturgi(current).getComposable() }
            composable(NavigationRoutes.SCREEN_LIVE.name) { ScreenVideoLive(current).getComposable() }
            composable(NavigationRoutes.SCREEN_MAIN.name) { ScreenMain(current).getComposable() }
            composable(NavigationRoutes.SCREEN_MEDIA.name) { ScreenMedia(current).getComposable() }
            composable(NavigationRoutes.SCREEN_PDF_VIEWER.name) { ScreenPDFViewer(current).getComposable() }
            composable(NavigationRoutes.SCREEN_PERSEMBAHAN.name) { ScreenPersembahan(current).getComposable() }
            composable(NavigationRoutes.SCREEN_POSTER_VIEWER.name) { ScreenPosterViewer(current).getComposable() }
            composable(NavigationRoutes.SCREEN_POSTER_VIEWER_LEGACY.name) { ScreenPosterViewerLegacy(current).getComposable() }
            composable(NavigationRoutes.SCREEN_PRIVACY.name) { ScreenPrivacy(current).getComposable() }
            composable(NavigationRoutes.SCREEN_PUKAT_BERKAT.name) {ScreenPukatBerkat(current).getComposable()}
            composable(NavigationRoutes.SCREEN_SEARCH.name) {ScreenSearch(current).getComposable()}
            composable(NavigationRoutes.SCREEN_SEASONAL.name) {ScreenSeasonal(current).getComposable()}
            composable(NavigationRoutes.SCREEN_SETTINGS.name) {ScreenSettings(current).getComposable()}
            composable(NavigationRoutes.SCREEN_STATIC_CONTENT_LIST.name) { ScreenStaticContentList(current).getComposable() }
            composable(NavigationRoutes.SCREEN_VIDEO_LIST.name) { ScreenVideoList(current).getComposable() }
            composable(NavigationRoutes.SCREEN_WARTA.name) { ScreenWarta(current).getComposable() }
            composable(NavigationRoutes.SCREEN_WEBVIEW.name) { ScreenWebView(current).getComposable() }
            composable(NavigationRoutes.SCREEN_YKB.name) {ScreenYKB(current).getComposable()}
            composable(NavigationRoutes.SCREEN_YKB_LIST.name) {ScreenYKBList(current).getComposable()}
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
        NotificationService.initDebuggerChannel(this@ActivityLauncher)
        NotificationService.initFCMChannel(this@ActivityLauncher)
        NotificationService.initSarenNotificationChannel(this@ActivityLauncher)
        NotificationService.initYKBHarianNotificationChannel(this@ActivityLauncher)
    }

    /**
     * Initializing the WorkManager,
     * which will trigger notifications and stuffs.
     */
    private fun initWorkManager() {
        WorkScheduler.scheduleBackgroundDataUpdater(this@ActivityLauncher)
        WorkScheduler.scheduleSarenReminder(this@ActivityLauncher)
        WorkScheduler.scheduleYKBReminder(this@ActivityLauncher)
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
        // Preparing the file creator that provides shareable files.
        val contentProviderFileCreator = InternalFileManager(this).CONTENT_PROVIDER_FILE_CREATOR
        if (!contentProviderFileCreator.exists()) contentProviderFileCreator.mkdir()

        // The file creator to create the private file.
        val fileCreator = InternalFileManager(this).DATA_DIR_FILE_CREATOR

        // Setting up the downloaded JSON's absolute paths.
        Logger.logInit({}, "Initializing the downloaded JSON paths ...")
        MainCompanion.absolutePathToJSONFile = File(fileCreator, MainCompanion.savedFilename).absolutePath
        ModulesCompanion.absolutePathToJSONFile = File(fileCreator, ModulesCompanion.savedFilename).absolutePath
        GalleryCompanion.absolutePathToJSONFile = File(fileCreator, GalleryCompanion.savedFilename).absolutePath
        StaticCompanion.absolutePathToJSONFile = File(fileCreator, StaticCompanion.savedFilename).absolutePath

        // The QRIS location.
        ScreenPersembahanCompanion.absolutePathToQrisFile = File(contentProviderFileCreator, ScreenPersembahanCompanion.savedFilename).absolutePath

        // Get the number of launches since install so that we can determine
        // whether to use the fallback data.
        val launches = LocalStorage(this).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAUNCH_COUNTS, LocalStorageDataTypes.INT) as Int
        val lastAppVersion = LocalStorage(this).getLocalStorageValue(LocalStorageKeys.LOCAL_KEY_LAST_APP_VERSION_CODE, LocalStorageDataTypes.INT) as Int

        // Get fallback data only if first launch.
        if (launches == 1) {
            // Let's apply the fallback JSON data until the actual, updated JSON metadata is downloaded.
            Logger.logInit({}, "Loading the fallback JSON metadata ...")
            Main(this).initFallbackMainData()

            // Loading the fallback gallery data.
            Logger.logInit({}, "Loading the fallback gallery JSON file ...")
            Gallery(this).initFallbackGalleryData()

            // Loading the fallback static data.
            Logger.logInit({}, "Loading the fallback static JSON file ...")
            Static(this).initFallbackStaticData()

            // Loading the fallback static data.
            Logger.logInit({}, "Loading the fallback modules JSON file ...")
            Modules(this).initFallbackModulesData()
        } else {
            // Migration to v0.7.0 (41).
            if (lastAppVersion <= 40) {
                // The word "Davey" is an Android developer team's internal joke referring to "Dave Burke" (Android vice president?).
                // It is not intended for general use, but I (github.com/groaking) saw this as a joke.
                // I'll use the word "Davey" here anyway.
                // SOURCE: https://stackoverflow.com/a/60675966
                Logger.logInit({}, "Did not expect to have JSON files v2.1 to be handled by app version 0.6.4 or lower, Davey! Falling back to bundled JSON files ...")
                Main(this).initFallbackMainData()
                Gallery(this).initFallbackGalleryData()
                Static(this).initFallbackStaticData()
                Modules(this).initFallbackModulesData()
            } else {
                Logger.logInit({}, "This is not first launch. Loading the locally downloaded JSON files ...")
                Main(this).initLocalMainData()
                Gallery(this).initLocalGalleryData()
                Static(this).initLocalStaticData()
                Modules(this).initLocalModulesData()
            }
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
            val actualPageCount = MainCompanion.api!!.carousel.size
            val carouselPageCount = actualPageCount * baseInfiniteScrollingPages
            FragmentHomeCompanion.rememberedCarouselPagerState = rememberPagerState(
                initialPage = carouselPageCount / 2,
                pageCount = { carouselPageCount }
            )
        }
    }

}