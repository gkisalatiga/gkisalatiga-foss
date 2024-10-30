/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the enumerated constant strings used in navigating between Composables.
 */

package org.gkisalatiga.plus.lib

enum class NavigationRoutes {
    /* The individual Composable "screens" of the app, which occupy an entire screen. */
    SCREEN_ABOUT,
    SCREEN_AGENDA,
    SCREEN_ATTRIBUTION,
    SCREEN_BIBLE,
    SCREEN_BLANK,
    SCREEN_CONTRIB,
    SCREEN_DEV,
    SCREEN_FORMS,
    SCREEN_GALERI,
    SCREEN_GALERI_LIST,
    SCREEN_GALERI_VIEW,
    SCREEN_GALERI_YEAR,
    SCREEN_INTERNAL_HTML,
    SCREEN_LIBRARY,
    SCREEN_LICENSE,
    SCREEN_LITURGI,
    SCREEN_LIVE,
    SCREEN_MAIN,
    SCREEN_MEDIA,
    SCREEN_PDF_VIEWER,
    SCREEN_PERSEMBAHAN,
    SCREEN_POSTER_VIEWER,
    SCREEN_PRERECORDED,
    SCREEN_PRIVACY,
    SCREEN_PROFILE,
    SCREEN_PUKAT_BERKAT,
    SCREEN_SEARCH,
    SCREEN_SETTINGS,
    SCREEN_STATIC_CONTENT_LIST,
    SCREEN_VIDEO,
    SCREEN_VIDEO_LIST,
    SCREEN_WARTA,
    SCREEN_WEBVIEW,
    SCREEN_YKB,
    SCREEN_YKB_LIST,

    /* The individual Composable "fragments" of some screens.
     * Note that we do not actually implement fragments, since we use Jetpack Compose.
     * "Fragment" is just an easy way to phrase "a container within a screen".
     */
    FRAG_ABOUT,
    FRAG_BLANK,
    FRAG_GALLERY_LIST,
    FRAG_GALLERY_STORY,
    FRAG_MAIN_EVENTS,
    FRAG_MAIN_HOME,
    FRAG_MAIN_INFO,
    FRAG_MAIN_NEWS,
    FRAG_MAIN_SERVICES,
    FRAG_PROFILE_ASSEMBLY,
    FRAG_PROFILE_CHURCH,
    FRAG_PROFILE_MINISTRY,
    FRAG_PROFILE_PASTOR,

    /* The following definitions define "sub-menus" that are part of a given fragment. */
    SUB_BLANK,
    SUB_KEBAKTIAN_ES,
    SUB_KEBAKTIAN_UMUM,
}