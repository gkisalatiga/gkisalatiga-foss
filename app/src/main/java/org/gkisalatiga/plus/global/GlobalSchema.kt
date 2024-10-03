/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 *
 * Implements a global declaration of variables, which can be accessed across classes.
 * SOURCE: https://tutorial.eyehunts.com/android/declare-android-global-variable-kotlin-example/
 * SOURCE: https://stackoverflow.com/a/52844621
 */

package org.gkisalatiga.plus.global

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipboardManager
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.gkisalatiga.plus.composable.YouTubeView
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors

class GlobalSchema : Application() {

    // Initializing the data schema of the app that will be shared across composables
    // and that will course the navigation of screens.
    companion object {

        /* ------------------------------------------------------------------------------------ */
        /* The following constants are used in the "ScreenAbout" composable. */
        const val aboutSourceCodeURL = "https://github.com/gkisalatiga/gki-salatiga-plus"
        const val aboutChangelogURL = "https://github.com/gkisalatiga/gki-salatiga-plus/blob/main/CHANGELOG.md"
        const val aboutContactMail = "dev.gkisalatiga@gmail.com"
        const val aboutLicenseFullTextURL = "https://github.com/gkisalatiga/gki-salatiga-plus/blob/main/LICENSE"

        /* ------------------------------------------------------------------------------------ */
        /* The following schemas are used in Google Drive Gallery viewer
         * and the SAF-based GDrive photo downloader. */

        // SAF create document code.
        val GALLERY_SAVER_CODE = 40

        // SAF GallerySaver -> GDrive URL to download.
        var targetGoogleDrivePhotoURL = ""

        // Whether to display the download progress indicator.
        val showScreenGaleriViewDownloadProgress = mutableStateOf(false)
        val showScreenGaleriViewAlertDialog = mutableStateOf(false)
        var targetSaveFilename = ""
        var txtScreenGaleriViewAlertDialogTitle = ""
        var txtScreenGaleriViewAlertDialogSubtitle = ""

        /* ------------------------------------------------------------------------------------ */
        /* The following parameter determines which JSON API source to look up to in order to update the application content.
         * It cannot and should not be changed arbitrarily within the app code. */
        val JSONSource = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/gkisplus.json"

        // This is the filename which will save the above JSON source.
        val JSONSavedFilename = "gkisplus.json"

        // Stores the absolute path of the downloaded (into internal app storage) JSON metadata
        var absolutePathToJSONMetaData = ""

        // The state of the initialization of the JSON metadata.
        var isJSONMainDataInitialized = mutableStateOf(false)

        // The JSONObject that can be globally accessed by any function and class in the app.
        var globalJSONObject: JSONObject? = null
        // var globalJSONObject: MutableState<JSONObject?> = mutableStateOf(null)

        /* ------------------------------------------------------------------------------------ */
        /* Determines the initialization of gallery JSON file. */

        val gallerySource = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/gkisplus-gallery.json"
        val gallerySavedFilename = "gkisplus-gallery.json"
        var absolutePathToGalleryData = ""
        var isGalleryDataInitialized = mutableStateOf(false)
        var globalGalleryObject: JSONObject? = null

        /* ------------------------------------------------------------------------------------ */
        /* Determines the initialization of static JSON file. */

        val staticSource = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/gkisplus-static.json"
        val staticSavedFilename = "gkisplus-static.json"
        var absolutePathToStaticData = ""
        var isStaticDataInitialized = mutableStateOf(false)
        var globalStaticObject: JSONArray? = null

        /* ------------------------------------------------------------------------------------ */
        /* The following parameter determines which zipped static source to look up to in order to update the application's static data.
         * It cannot and should not be changed arbitrarily within the app code. */

        // The target static data "folder" to display in the static content list.
        var targetStaticFolder: JSONObject? = null

        /* ------------------------------------------------------------------------------------ */
        /* The following parameter determines which zipped carousel data should be loaded into the main screen.
         * It cannot and should not be changed arbitrarily within the app code. */

        // The variable associated with the string values and resource paths.
        var carouselJSONObject: ArrayList<JSONObject> = ArrayList<JSONObject>()
        var carouselJSONKey: ArrayList<String> = ArrayList<String>()

        /* ------------------------------------------------------------------------------------ */
        /* Values and constants used in the "offertory" menu. */

        const val offertoryQRISImageSource = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/images/qris_gkis.png"

        /* ------------------------------------------------------------------------------------ */
        /* Initializing the debugging toggles. */

        // Whether to enable the easter egg feature of the app and display it to the user.
        const val DEBUG_ENABLE_EASTER_EGG = true

        // Whether to display the debugger's toast.
        const val DEBUG_ENABLE_TOAST = false

        // Whether to display the debugger's logcat logging.
        const val DEBUG_ENABLE_LOG_CAT = true
        const val DEBUG_ENABLE_LOG_CAT_BOOT = true
        const val DEBUG_ENABLE_LOG_CAT_CONN_TEST = true
        const val DEBUG_ENABLE_LOG_CAT_DUMP = true
        const val DEBUG_ENABLE_LOG_CAT_INIT = true
        const val DEBUG_ENABLE_LOG_CAT_SPAM = true
        const val DEBUG_ENABLE_LOG_CAT_TEST = true
        const val DEBUG_ENABLE_LOG_CAT_UPDATER = true
        const val DEBUG_ENABLE_LOG_CAT_WORKER = true

        // Whether to hide the splash screen.
        const val DEBUG_DISABLE_SPLASH_SCREEN = false

        // Whether to disable downloading the static data zip file from the GitHub repository.
        const val DEBUG_DISABLE_DOWNLOADING_STATIC_DATA = true

        // Whether to disable downloading the carousel data zip file from the GitHub repository.
        const val DEBUG_DISABLE_DOWNLOADING_CAROUSEL_DATA = false

        /* ------------------------------------------------------------------------------------ */
        /* These parameters are used to navigate across screens, fragments, and submenus in the composables.
         * These parameters must be individually a mutable state object.
         * Changing any of the following parameters would directly and immediately trigger recomposition. */

        // Determines where to go when pressing the "back" button after changing screens.
        val popBackScreen = mutableStateOf("")
        val popBackDoubleScreen = mutableStateOf("")
        val popBackFragment = mutableStateOf("")
        val popBackSubmenu = mutableStateOf("")

        // Determine the next screen to open upon trigger.
        val pushScreen = mutableStateOf("")
        val pushFragment = mutableStateOf("")  // --- not used.
        val pushSubmenu = mutableStateOf("")  // --- not used.

        // The default value of screens and fragments.
        val defaultScreen = mutableStateOf("")
        val defaultFragment = mutableStateOf("")  // --- not used.
        val defaultSubmenu = mutableStateOf("")  // --- not used.

        // Determine if we should reload the current screen.
        val reloadCurrentScreen = mutableStateOf(false)

        // Custom submenu global state for the tab "Services".
        var lastServicesSubmenu = mutableStateOf("")

        // Stores globally the state of the last opened main menu fragment.
        var lastMainScreenPagerPage = mutableStateOf("")

        // Stores globally the current background of the new top bar by user github.com/ujepx64.
        var lastNewTopBarBackground = mutableStateOf(0)

        /* The download status of the lib.Downloader's multithread. */
        var isPrivateDownloadComplete = mutableStateOf(false)

        /* Stores the path to the downloaded private file; used in lib.Downloader. */
        var pathToDownloadedPrivateFile = mutableStateOf("")

        /* The composable YouTube element. */
        var ytComposable: YouTubeView? = null

        /* The global YouTubeViewer element. */
        var ytView: YouTubePlayerView? = null

        /* The YouTube player object. */
        var ytPlayer: YouTubePlayer? = null

        /* The global YouTube tracker. */
        val ytTracker: YouTubePlayerTracker = YouTubePlayerTracker()

        /* The YouTube video player states. */
        val ytIsFullscreen = mutableStateOf(false)
        val ytCurrentSecond = mutableFloatStateOf(0.0f)

        /* Determines what screen triggered the launching of "ScreenVideoList". */
        var ytVideoListDispatcher: String = ""

        /* The remembered scroll states. */
        var fragmentGalleryListScrollState: LazyGridState? = null
        var fragmentHomeScrollState: ScrollState? = null
        var fragmentServicesScrollState: ScrollState? = null
        var fragmentInfoScrollState: ScrollState? = null
        var screenAboutScrollState: ScrollState? = null
        var screenAttributionScrollState: ScrollState? = null
        var screenAgendaScrollState: ScrollState? = null
        var screenFormsScrollState: ScrollState? = null
        var screenMediaScrollState: ScrollState? = null
        var screenPersembahanScrollState: ScrollState? = null
        var screenGaleriScrollState: ScrollState? = null

        /* The poster dialog state in FragmentHome. */
        val fragmentHomePosterDialogState = mutableStateOf(false)

        /* The horizontal pager state in FragmentHome */
        var fragmentHomeCarouselPagerState: PagerState? = null

        /* The top offset of fragments in the ScreenMain. */
        const val minScreenMainTopOffset = 0.0f
        const val maxScreenMainTopOffset = 325.0f
        val screenMainContentTopOffset = mutableFloatStateOf(maxScreenMainTopOffset)

        /* The top offset of the main menu's welcome image (in the top bar). */
        const val minScreenMainWelcomeImageTopOffset = -(maxScreenMainTopOffset - minScreenMainTopOffset) / 2
        const val maxScreenMainWelcomeImageTopOffset = 0.0f
        val screenMainWelcomeImageTopOffset = mutableFloatStateOf(maxScreenMainWelcomeImageTopOffset)

        /* ------------------------------------------------------------------------------------ */
        /* The following variable determines the status of internet connection. */

        // The status of internet connection.
        var isConnectedToInternet = mutableStateOf(false)

        // Used in the loading of cached data when the app is not connected to the internet.
        var isOfflineCachedDataLoaded: Boolean = false

        /* ------------------------------------------------------------------------------------ */
        /* Controls the pull-to-refresh (PTR) states and variables. */

        val isPTRRefreshing = mutableStateOf(false)
        val PTRExecutor = Executors.newSingleThreadExecutor()

        @OptIn(ExperimentalMaterial3Api::class)
        var globalPTRState: PullToRefreshState? = null

        /* ------------------------------------------------------------------------------------ */
        /* Controls the scaffolding snack bar. */

        val snackbarHostState = SnackbarHostState()

        /* ------------------------------------------------------------------------------------ */
        /* The following variables are related to the app's activity and back-end functionalities. */

        // The status of internet connection.
        val isRunningInBackground = mutableStateOf(false)

        // Current app's screen orientation.
        val isPortraitMode = mutableStateOf(true)

        // Current app's bars (both status bar and navigation bar) state of visibility.
        val phoneBarsVisibility = mutableStateOf(true)

        /* ------------------------------------------------------------------------------------ */
        /* Initializing the global schema that does not directly trigger recomposition. */

        @SuppressLint("MutableCollectionMutableState")
        val ytViewerParameters = mutableMapOf<String, String>(
            /* These parameters are required for displaying the right content in the YouTube viewer. */
            "date" to "",
            "title" to "",
            "desc" to "",
            "thumbnail" to "",
            "yt-id" to "",
            "yt-link" to ""
        )

        // Determines what link to show in ScreenWebView, and its title.
        var webViewTargetURL: String = ""
        var webViewTitle: String = ""

        // Determines what YouTube playlist to display when switching to "ScreenVideoList".
        var videoListContentArray: MutableList<JSONObject> = mutableListOf()
        var videoListTitle: String = ""

        // Determines the "data/static" JSON schema node to display in the ScreenInternalHTML view,
        // as well as its content title.
        var targetIndexHTMLPath: String = ""
        var targetHTMLContent: String = ""
        var internalWebViewTitle: String = ""

        // Determines which gallery folder year to display in the "gallery" menu.
        var targetGalleryYear: String = ""

        // These variables apply to "ScreenGaleriList".
        var displayedAlbumTitle: String = ""
        var displayedAlbumStory: String = ""
        var displayedFeaturedImageID: String = ""
        var targetAlbumContent: JSONArray? = null

        // These variables apply to ScreenGaleriView.
        var galleryViewerStartPage: Int = 0

        // Controls the state of the poster dialog.
        val posterDialogTitle = mutableStateOf("")
        val posterDialogCaption = mutableStateOf("")
        val posterDialogImageSource = mutableStateOf("")

        /* This is the clipboard manager. */
        var clipManager: ClipboardManager? = null

        /* ------------------------------------------------------------------------------------ */
        /* The following is the app-wide, private preferences stored across launches.
         * All variables below must start with "pref" prefix.
         * The companion constant name for SharedPreferences key should also be supplied. */

        // This constants determines the shared preferences name.
        const val NAME_SHARED_PREFERENCES: String = "gkisplus"

        // In millisecond. So, divide by 1000 to get second, then 86400 to get days.
        const val PREF_KEY_STATIC_DATA_UPDATE_FREQUENCY = "static_data_freq"

        // In millisecond. So, divide by 1000 to get second, then 86400 to get days.
        const val PREF_KEY_CAROUSEL_BANNER_UPDATE_FREQUENCY = "carousel_banner_freq"

        // In millisecond. So, divide by 1000 to get second, then 86400 to get days.
        const val PREF_KEY_LAST_STATIC_DATA_UPDATE = "static_data_last_update"

        // In millisecond. So, divide by 1000 to get second, then 86400 to get days.
        const val PREF_KEY_LAST_CAROUSEL_BANNER_UPDATE = "carousel_banner_last_update"

        // Number of launches since last install/storage data clear.
        const val PREF_KEY_LAUNCH_COUNTS = "launch_counts"

        // The default pairing of saved preferences.
        var preferencesKeyValuePairs: MutableMap<String, Any> = mutableMapOf(
            PREF_KEY_STATIC_DATA_UPDATE_FREQUENCY to 604800000.toLong(),  // --- 604800000 means "once every 7 days" in millisecond
            PREF_KEY_CAROUSEL_BANNER_UPDATE_FREQUENCY to 86400000.toLong(),  // --- 86400000 means "once every 1 day" in millisecond
            PREF_KEY_LAST_STATIC_DATA_UPDATE to Long.MIN_VALUE,
            PREF_KEY_LAST_CAROUSEL_BANNER_UPDATE to Long.MIN_VALUE,
            PREF_KEY_LAUNCH_COUNTS to -1
        )

    }
}