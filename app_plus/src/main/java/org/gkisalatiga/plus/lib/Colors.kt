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
        const val MAIN_BOTTOM_BAR_COLOR = 0xffEBC1B3
        const val MAIN_CONTAINER_COLOR = 0xff715446
        const val MAIN_DARK_BROWN = 0xff482505
        const val MAIN_LIGHT_THEME_WHITE = 0xffffffff
        const val MAIN_PDF_DOWNLOADED_BADGE_COLOR = 0xff40c057
        const val MAIN_SPLASHSCREEN_SUB_TEXT_COLOR = 0xffffffff
        const val MAIN_TOP_BAR_COLOR = 0xff715446
        val MAIN_TOP_BAR_CONTENT_COLOR = Color(0xffffffff)

        /* Used in app's screens. */
        const val SCREEN_AGENDA_ITEM_BACKGROUND = 0xffFFE8BB
        const val SCREEN_AGENDA_ITEM_TIME_BACKGROUND = 0xff482505
        const val SCREEN_AGENDA_ITEM_CHIP_SELECTED_BACKGROUND = 0xff482505
        const val SCREEN_YKB_ARCHIVE_BUTTON_COLOR = 0xff482505

        /* Used in app's fragments. */
        const val FRAGMENT_SERVICES_SHOWMORE_BACKGROUND = 0xff97705d
        const val FRAGMENT_SERVICES_SHOWMORE_CONTENT = 0xffffffff

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