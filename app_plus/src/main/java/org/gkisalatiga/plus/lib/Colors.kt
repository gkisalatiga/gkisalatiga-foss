/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the color palette of GKI Salatiga.
 */

package org.gkisalatiga.plus.lib

class Colors {
    companion object {
        const val MAIN_DARK_BROWN = 0xff482505
        const val AGENDA_ITEM_BACKGROUND = 0xffFFE8BB
        const val AGENDA_ITEM_TIME_BACKGROUND = 0xff482505
        const val AGENDA_ITEM_CHIP_SELECTED_BACKGROUND = 0xff482505
        const val LIGHT_THEME_WHITE = 0xffffffff
        const val MAIN_TOP_BAR_COLOR = 0xff715446
        const val MAIN_TOP_BAR_CONTENT_COLOR = 0xffffffff
        const val YKB_ARCHIVE_BUTTON_COLOR = 0xff482505

        const val SPLASHSCREEN_SUB_TEXT_COLOR = 0xffffffff

        /* Used in FragmentServices */
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