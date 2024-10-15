/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the enumerated constant strings used in navigating between Composables.
 */

package org.gkisalatiga.fdroid.lib

// TODO: Make this class enum.
class NavigationRoutes {
    companion object {
        // The individual Composable "screens" of the app.
        const val SCREEN_MAIN: String = "nav_screen_main"
        const val SCREEN_ABOUT: String = "nav_screen_about"
        const val SCREEN_PROFILE: String = "nav_screen_profile"
        const val SCREEN_WEBVIEW: String = "nav_screen_webview"
        const val SCREEN_DEV: String = "nav_screen_developer"
        const val SCREEN_INTERNAL_HTML: String = "nav_screen_internalhtml"
        const val SCREEN_VIDEO: String = "nav_screen_video"
        const val SCREEN_LIVE: String = "nav_screen_live"
        const val SCREEN_PRERECORDED: String = "nav_screen_prerecorded"
        const val SCREEN_BIBLE: String = "nav_screen_bible"
        const val SCREEN_LIBRARY: String = "nav_screen_library"
        const val SCREEN_PUKAT_BERKAT: String = "nav_screen_store"
        const val SCREEN_SETTINGS: String = "nav_screen_prefs"
        const val SCREEN_SEARCH: String = "nav_screen_search"
        const val SCREEN_FORMS: String = "nav_screen_forms"
        const val SCREEN_YKB: String = "nav_screen_ykb"
        const val SCREEN_WARTA: String = "nav_screen_wj"
        const val SCREEN_LITURGI: String = "nav_screen_liturgi"
        const val SCREEN_VIDEO_LIST: String = "nav_screen_saren"
        const val SCREEN_POSTER_VIEWER: String = "nav_screen_poster"
        const val SCREEN_AGENDA: String = "nav_screen_agenda"
        const val SCREEN_ATTRIBUTION: String = "nav_screen_attrib"
        const val SCREEN_PRIVACY: String = "nav_screen_privacy"
        const val SCREEN_LICENSE: String = "nav_screen_license"
        const val SCREEN_CONTRIB: String = "nav_screen_contributors"
        const val SCREEN_PERSEMBAHAN: String = "nav_screen_offertory"
        const val SCREEN_GALERI: String = "nav_screen_gallery"
        const val SCREEN_GALERI_LIST: String = "nav_screen_gallerylist"
        const val SCREEN_GALERI_VIEW: String = "nav_screen_galleryview"
        const val SCREEN_GALERI_YEAR: String = "nav_screen_galleryyear"
        const val SCREEN_MEDIA: String = "nav_screen_media"
        const val SCREEN_STATIC_CONTENT_LIST: String = "nav_screen_static_content_list"
        const val SCREEN_BLANK: String = "nav_screen_blank"

        // The individual Composable "fragments" of each screen.
        // Note that we do not actually implement fragments, since we use Jetpack Compose.
        // "Fragment" is just an easy way to phrase "a container within a screen".
        const val FRAG_MAIN_HOME: String = "nav_frag_home"
        const val FRAG_MAIN_SERVICES: String = "nav_frag_services"
        const val FRAG_MAIN_NEWS: String = "nav_frag_news"
        const val FRAG_MAIN_EVENTS: String = "nav_frag_events"
        const val FRAG_MAIN_INFO: String = "nav_frag_info"
        const val FRAG_GALLERY_LIST: String = "nav_gallery_list"
        const val FRAG_GALLERY_STORY: String = "nav_gallery_story"
        const val FRAG_PROFILE_CHURCH: String = "nav_frag_church"
        const val FRAG_PROFILE_PASTOR: String = "nav_frag_pastorate"
        const val FRAG_PROFILE_ASSEMBLY: String = "nav_frag_assembly"
        const val FRAG_PROFILE_MINISTRY: String = "nav_frag_ministry"
        const val FRAG_ABOUT: String = "nav_frag_about"
        const val FRAG_BLANK: String = "nav_frag_blank"

        // The following definitions define "sub-menus" that are part of a given fragment.
        const val SUB_KEBAKTIAN_UMUM: String = "nav_sub_umum"
        const val SUB_KEBAKTIAN_ES: String = "nav_sub_es"
        const val SUB_BLANK: String = "nav_sub_blank"
    }
}
