/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the enumerated constant strings used in navigating between Composables.
 */

package org.gkisalatiga.plus.lib

public data class NavigationRoutes (
    // The individual Composable "screens" of the app.
    val SCREEN_MAIN: String = "nav_screen_main",
    val SCREEN_ABOUT: String = "nav_screen_about",
    val SCREEN_PROFILE: String = "nav_screen_profile",
    val SCREEN_WEBVIEW: String = "nav_screen_webview",
    val SCREEN_DEV: String = "nav_screen_developer",
    val SCREEN_INTERNAL_HTML: String = "nav_screen_internalhtml",
    val SCREEN_VIDEO: String = "nav_screen_video",
    val SCREEN_LIVE: String = "nav_screen_live",
    val SCREEN_PRERECORDED: String = "nav_screen_prerecorded",
    val SCREEN_FORMS: String = "nav_screen_forms",
    val SCREEN_YKB: String = "nav_screen_ykb",
    val SCREEN_WARTA: String = "nav_screen_wj",
    val SCREEN_LITURGI: String = "nav_screen_liturgi",
    val SCREEN_VIDEO_LIST: String = "nav_screen_saren",
    val SCREEN_POSTER_VIEWER: String = "nav_screen_poster",
    val SCREEN_AGENDA: String = "nav_screen_agenda",
    val SCREEN_ATTRIBUTION: String = "nav_screen_attrib",
    val SCREEN_PERSEMBAHAN: String = "nav_screen_offertory",
    val SCREEN_GALERI: String = "nav_screen_gallery",
    val SCREEN_GALERI_LIST: String = "nav_screen_gallerylist",
    val SCREEN_GALERI_VIEW: String = "nav_screen_galleryview",
    val SCREEN_GALERI_YEAR: String = "nav_screen_galleryyear",
    val SCREEN_MEDIA: String = "nav_screen_media",
    val SCREEN_STATIC_CONTENT_LIST: String = "nav_screen_static_content_list",
    val SCREEN_BLANK: String = "nav_screen_blank",

    // The individual Composable "fragments" of each screen.
    // Note that we do not actually implement fragments, since we use Jetpack Compose.
    // "Fragment" is just an easy way to phrase "a container within a screen".
    val FRAG_MAIN_HOME: String = "nav_frag_home",
    val FRAG_MAIN_SERVICES: String = "nav_frag_services",
    val FRAG_MAIN_NEWS: String = "nav_frag_news",
    val FRAG_MAIN_EVENTS: String = "nav_frag_events",
    val FRAG_MAIN_INFO: String = "nav_frag_info",
    val FRAG_GALLERY_LIST: String = "nav_gallery_list",
    val FRAG_GALLERY_STORY: String = "nav_gallery_story",
    val FRAG_PROFILE_CHURCH: String = "nav_frag_church",
    val FRAG_PROFILE_PASTOR: String = "nav_frag_pastorate",
    val FRAG_PROFILE_ASSEMBLY: String = "nav_frag_assembly",
    val FRAG_PROFILE_MINISTRY: String = "nav_frag_ministry",
    val FRAG_ABOUT: String = "nav_frag_about",
    val FRAG_BLANK: String = "nav_frag_blank",

    // The following definitions define "sub-menus" that are part of a given fragment.
    val SUB_KEBAKTIAN_UMUM: String = "nav_sub_umum",
    val SUB_KEBAKTIAN_ES: String = "nav_sub_es",
    val SUB_BLANK: String = "nav_sub_blank",
)