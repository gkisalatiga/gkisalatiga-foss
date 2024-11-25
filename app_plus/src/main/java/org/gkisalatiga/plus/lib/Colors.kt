/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the color palette of GKI Salatiga.
 */

package org.gkisalatiga.plus.lib

import androidx.compose.ui.graphics.Color

class Colors {
    companion object {
        /* Used in defining color schemes at the app's launch. */
        val COLOR_SCHEME_PRIMARY = Color(0xff715446)
        val COLOR_SCHEME_SECONDARY = Color(0xff97705d)
        val COLOR_SCHEME_TERTIARY = Color(0xffbd8c75)

        /* Main (default) colors of the app. */
        val MAIN_BOTTOM_BAR_COLOR = Color(0xffEBC1B3)
        val MAIN_CONTAINER_COLOR = Color(0xff715446)
        val MAIN_DARK_BROWN = Color(0xff482505)
        val MAIN_LIGHT_THEME_WHITE = Color(0xffffffff)
        val MAIN_PDF_DOWNLOADED_BADGE_COLOR = Color(0xff40c057)
        val MAIN_SCREEN_BACKGROUND_COLOR = Color(0xffffffff)
        val MAIN_SPLASHSCREEN_SUB_TEXT_COLOR = Color(0xffffffff)
        val MAIN_SURFACE_COLOR = Color(0xffffffff)
        val MAIN_TOP_BAR_COLOR = Color(0xff715446)
        val MAIN_TOP_BAR_CONTENT_COLOR = Color(0xffffffff)

        /* Used in app's screens. */
        val SCREEN_AGENDA_ITEM_BACKGROUND = Color(0xffFFE8BB)
        val SCREEN_AGENDA_ITEM_TIME_BACKGROUND = Color(0xff482505)
        val SCREEN_AGENDA_ITEM_CHIP_SELECTED_BACKGROUND = Color(0xff482505)
        val SCREEN_YKB_ARCHIVE_BUTTON_COLOR = Color(0xff482505)

        /* Used in app's fragments. */
        val FRAGMENT_SERVICES_SHOWMORE_BACKGROUND = Color(0xff97705d)
        val FRAGMENT_SERVICES_SHOWMORE_CONTENT = Color(0xffffffff)

        /* This is how we can theoretically create a color-changing scheme based on theme: */
        /*
        // Cannot use "const" with "when"
        val ABC = when (Theme) {
            Theme1 -> Color1
            Theme2 -> Color2
            else -> ColorDefault
        }
        */
    }
}