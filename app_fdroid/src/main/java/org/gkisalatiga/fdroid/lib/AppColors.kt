/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Stores the color palette of GKI Salatiga.
 */

package org.gkisalatiga.fdroid.lib

class AppColors {
    companion object {
        const val MAIN_DARK_BROWN = 0xff482505
        const val AGENDA_ITEM_BACKGROUND = 0xffFFE8BB
        const val AGENDA_ITEM_TIME_BACKGROUND = 0xff482505
        const val AGENDA_ITEM_CHIP_SELECTED_BACKGROUND = 0xff482505
        const val LIGHT_THEME_WHITE = 0xffffffff
        const val MAIN_TOP_BAR_COLOR = 0xff825303

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