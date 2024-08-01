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
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.json.JSONObject

class GlobalSchema : Application() {

    // Initializing the data schema of the app that will be shared across composables
    // and that will course the navigation of screens.
    companion object {

        /* ------------------------------------------------------------------------------------ */
        /* The following parameter determines which JSON API source to look up to in order to update the application content.
         * It cannot and should not be changed arbitrarily within the app code. */
        val JSONSource = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/gkisplus.json"

        // This is the filename which will save the above JSON source.
        val JSONSavedFilename = "gkisplus.json"

        // Stores the absolute path of the downloaded (into internal app storage) JSON metadata
        var absolutePathToJSONMetaData = ""

        // The state of the initialization of the JSON metadata.
        var isJSONMetaDataInitialized = mutableStateOf(false)

        // The JSONObject that can be globally accessed by any function and class in the app.
        var globalJSONObject: JSONObject? = null

        /* ------------------------------------------------------------------------------------ */
        /* The following parameter determines which zipped static source to look up to in order to update the application's static data.
         * It cannot and should not be changed arbitrarily within the app code. */

        val staticDataSource = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/gkisplus-static.zip"

        // This is the filename which will save the above ZIP-compressed static source.
        val staticDataSavedFilename = "gkisplus-static.zip"

        // Stores the absolute path of the downloaded (into internal app storage) ZIP-compressed static data (folder).
        var absolutePathToStaticData = ""

        // The state of the initialization of the ZIP-compressed static data.
        var isStaticDataDownloaded = mutableStateOf(false)
        var isStaticDataExtracted = mutableStateOf(false)

        // The variable associated with the string values and resource paths.
        var staticDataTitleArray: ArrayList<String> = ArrayList<String>()
        var staticDataBannerArray: ArrayList<String> = ArrayList<String>()
        var staticDataJSONNodeArray: ArrayList<String> = ArrayList<String>()
        var staticDataIndexHTMLArray: ArrayList<String> = ArrayList<String>()

        /* ------------------------------------------------------------------------------------ */
        /* The following parameter determines which zipped carousel data should be loaded into the main screen.
         * It cannot and should not be changed arbitrarily within the app code. */

        val carouselBannerSource = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/gkisplus-carousel.zip"
        val carouselBannerSavedFilename = "gkisplus-carousel.zip"

        // Stores the absolute path of the downloaded (into internal app storage) ZIP-compressed static data.
        var absolutePathToCarouselBanner = ""

        // The state of the initialization of the ZIP-compressed carousel banner data (folder).
        var isCarouselBannerDownloaded = mutableStateOf(false)
        var isCarouselBannerExtracted = mutableStateOf(false)

        // The variable associated with the string values and resource paths.
        var carouselBannerJSONNodeArray: ArrayList<String> = ArrayList<String>()
        var carouselBannerBannerArray: ArrayList<String> = ArrayList<String>()
        var carouselBannerTypeArray: ArrayList<String> = ArrayList<String>()
        var carouselBannerBaseFolderArray: ArrayList<String> = ArrayList<String>()

        /* ------------------------------------------------------------------------------------ */
        /* Initializing values that are preloaded during ActivityLauncher initializations. */

        // The list of JSON nodes corresponding to a service section.
        var servicesNode: ArrayList<String> = ArrayList<String>()

        // The list of services to display, corresponding to the section title string ID.
        var servicesTitle: ArrayList<String> = ArrayList<String>()

        /* ------------------------------------------------------------------------------------ */
        /* Initializing the debugging toggles. */

        // Whether to enable the easter egg feature of the app and display it to the user.
        const val DEBUG_ENABLE_EASTER_EGG = true

        // Whether to display the debugger's toast.
        const val DEBUG_ENABLE_TOAST = true

        // Whether to display the debugger's logcat logging.
        const val DEBUG_ENABLE_LOG_CAT = true

        // Whether to hide the splash screen.
        const val DEBUG_DISABLE_SPLASH_SCREEN = false

        /* ------------------------------------------------------------------------------------ */
        /* These parameters are used to navigate across screens, fragments, and submenus in the composables.
         * These parameters must be individually a mutable state object.
         * Changing any of the following parameters would directly and immediately trigger recomposition. */

        // Determines where to go when pressing the "back" button after changing screens.
        var popBackScreen = mutableStateOf("")
        var popBackFragment = mutableStateOf("")
        var popBackSubmenu = mutableStateOf("")

        // Determine the next screen to open upon trigger.
        var pushScreen = mutableStateOf("")
        var pushFragment = mutableStateOf("")  // --- not used.
        var pushSubmenu = mutableStateOf("")  // --- not used.

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

        /* The global YouTubeViewer element. */
        var ytView: YouTubePlayerView? = null

        /* The YouTube player object. */
        var ytPlayer: YouTubePlayer? = null

        /* The global YouTube tracker. */
        val ytTracker: YouTubePlayerTracker = YouTubePlayerTracker()

        /* The YouTube video player states. */
        val ytIsFullscreen = mutableStateOf(false)
        val ytCurrentSecond = mutableFloatStateOf(0.0f)

        /* The remembered scroll states. */
        var fragmentHomeScrollState: ScrollState? = null
        var fragmentServicesScrollState: ScrollState? = null
        var fragmentInfoScrollState: ScrollState? = null

        /* The poster dialog state in FragmentHome. */
        val fragmentHomePosterDialogState = mutableStateOf(false)

        /* ------------------------------------------------------------------------------------ */
        /* The following variable determines the status of internet connection. */

        // The status of internet connection.
        var isConnectedToInternet: Boolean = false

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

        // Determines what playlist to display when switching to "ScreenVideoList".
        // This represents the JSON node under "data/yt-video".
        var videoListTargetNode: String = ""
        var videoListTitle: String = ""

        // Determines the "data/static" JSON schema node to display in the ScreenInternalHTML view,
        // as well as its content title.
        var targetIndexHTMLPath: String = ""
        var internalWebViewTitle: String = ""

        // Controls the state of the poster dialog.
        val posterDialogTitle = mutableStateOf("")
        val posterDialogCaption = mutableStateOf("")
        val posterDialogImageSource = mutableStateOf("")

        /* This parameter is required for  manipulating the composition and the app's view. */
        // TODO: Find a way to use the app's context across functions without memory leak.
        @SuppressLint("StaticFieldLeak")
        var context: Context = AppCompatActivity()

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