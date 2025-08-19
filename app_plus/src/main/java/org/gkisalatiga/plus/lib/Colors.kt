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

// TODO: Migrate the Colors class to the sealed class [DynamicColorScheme].
class Colors {
    companion object {
        /* Main (default) colors of the app. */
        val MAIN_CONTAINER_COLOR = Color(0xff715446)
        val MAIN_DARK_BROWN_COLOR = Color(0xff482505)
        val MAIN_PDF_DOWNLOADED_BADGE_COLOR = Color(0xff40c057)
        val MAIN_PRIMARY_COLOR = Color(0xff715446)
        val MAIN_SCREEN_BACKGROUND_COLOR = Color(0xffffffff)
        val MAIN_SECONDARY_COLOR = Color(0xff97705d)
        val MAIN_SURFACE_COLOR = Color(0xffffffff)
        val MAIN_SURFACE_CONTAINER_COLOR = Color(0xfff3edf8)
        val MAIN_SURFACE_CONTAINER_HIGHEST_COLOR = Color(0xffe6e0e9)
        val MAIN_TERTIARY_COLOR = Color(0xffbd8c75)
        val MAIN_TOP_BAR_COLOR = Color(0xff715446)
        val MAIN_TOP_BAR_CONTENT_COLOR = Color(0xffffffff)

        /* Used in app's screens. */
        val SCREEN_MAIN_COLORIZE_COLOR = Color(0x77fdb308)
        val SCREEN_MAIN_OVERLAY_GRADIENT_MIDDLE_COLOR = Color.Transparent
        val SCREEN_MAIN_OVERLAY_GRADIENT_TOP_COLOR = Color(0xff825303)
        val SCREEN_YKB_ARCHIVE_BUTTON_COLOR = Color(0xff482505)

        /* Used in app's fragments. */
        val FRAGMENT_HOME_TOP_MENU_TINT_COLOR = Color(0xffFFFFFF)
        val FRAGMENT_HOME_TOP_TWO_MENUS_CONTAINER_COLOR = Color(0xff715446)
        val FRAGMENT_SERVICES_SHOW_MORE_BACKGROUND = Color(0xff97705d)
        val FRAGMENT_SERVICES_SHOW_MORE_CONTENT = Color(0xffffffff)
    }
}

sealed class DynamicColorScheme (
    val fragmentHomeCarouselPageIndicatorActiveColor : Color,
    val fragmentHomeCarouselPageIndicatorInactiveColor : Color,
    val fragmentInfoCopyrightTextColor : Color,
    val fragmentInfoIconTintColor : Color,
    val fragmentSeasonalMainTextColor : Color,
    val mainScreenBackgroundColor : Color,
    val mainSplashScreenBackgroundColor : Color,
    val mainSplashScreenForegroundColor : Color,
    val mainSplashScreenSubTextColor : Color,
    val mainZoomableBoxBackgroundColor : Color,
    val screenAgendaChipTodayBackgroundColor : Color,
    val screenAgendaChipSelectedBackgroundColor : Color,
    val screenAgendaChipUnselectedBackgroundColor : Color,
    val screenAgendaChipUnselectedBackgroundAlternativeColor : Color,
    val screenAgendaChipTextSelectedBackgroundColor : Color,
    val screenAgendaChipTextUnselectedBackgroundColor : Color,
    val screenAgendaContentTintColor : Color,
    val screenAgendaWeekdayTextColor : Color,
    val screenAgendaItemBackgroundColor : Color,
    val screenAgendaItemTimeBackgroundColor : Color,
    val screenAgendaItemTimeTextColor : Color,
    val screenAgendaItemProposalRutinBackgroundColor : Color,
    val screenAgendaItemProposalStatusTextColor : Color,
    val screenAgendaStatusApprovedColor : Color,
    val screenAgendaStatusCancelledColor : Color,
    val screenAgendaStatusDeniedColor : Color,
    val screenAgendaStatusWaitingColor : Color,
    val screenMainBottomNavSelectedIndicationColor : Color,
    val screenMainBottomNavSelectedIconColor : Color,
    val screenMainBottomNavSelectedTextColor : Color,
    val screenMainBottomNavUnselectedIconColor : Color,
    val screenMainBottomNavUnselectedTextColor : Color,
    val screenMainOverlayGradientBottomColor : Color,
    val screenSearchClickableTextColor : Color,
    val screenSearchHistoryItemTextColor : Color,
) {
    class DarkColorScheme : DynamicColorScheme(
        fragmentHomeCarouselPageIndicatorActiveColor = Color.White,
        fragmentHomeCarouselPageIndicatorInactiveColor = Color.White.copy(alpha = 0.5f),
        fragmentInfoCopyrightTextColor = Color(0xffa46443),
        fragmentInfoIconTintColor = Color(0xffe6ad84),
        fragmentSeasonalMainTextColor = Color.White,
        mainScreenBackgroundColor = Color(0xff141218),
        mainSplashScreenBackgroundColor = Color(0xff482505),
        mainSplashScreenForegroundColor = Color.White,
        mainSplashScreenSubTextColor = Color.White,
        mainZoomableBoxBackgroundColor = Color(0xff141218),
        screenAgendaChipTodayBackgroundColor = Color(0xfffdb308),
        screenAgendaChipSelectedBackgroundColor = Color(0xff482505),
        screenAgendaChipUnselectedBackgroundColor = Color.White,
        screenAgendaChipUnselectedBackgroundAlternativeColor = Color.White,
        screenAgendaChipTextSelectedBackgroundColor = Color.White,
        screenAgendaChipTextUnselectedBackgroundColor = Color.Black,
        screenAgendaContentTintColor = Color.White,
        screenAgendaItemBackgroundColor = Color(0xff482505),
        screenAgendaItemTimeBackgroundColor = Color(0xffFFE8BB),
        screenAgendaItemTimeTextColor = Color.Black,
        screenAgendaItemProposalRutinBackgroundColor = Color(0xfffdb308),
        screenAgendaItemProposalStatusTextColor = Color.Black,
        screenAgendaStatusApprovedColor = Color.Green,
        screenAgendaStatusCancelledColor = Color.Red,
        screenAgendaStatusDeniedColor = Color.Red,
        screenAgendaStatusWaitingColor = Color.LightGray,
        screenAgendaWeekdayTextColor = Color.White,
        screenMainBottomNavSelectedIndicationColor = Color(0xff715446),
        screenMainBottomNavSelectedIconColor = Color.White,
        screenMainBottomNavSelectedTextColor = Color.White,
        screenMainBottomNavUnselectedIconColor = Color.White,
        screenMainBottomNavUnselectedTextColor = Color.White,
        screenMainOverlayGradientBottomColor = Color.Black,
        screenSearchClickableTextColor = Color.White,
        screenSearchHistoryItemTextColor = Color.White,
    )
    class LightColorScheme : DynamicColorScheme(
        fragmentHomeCarouselPageIndicatorActiveColor = Color.DarkGray,
        fragmentHomeCarouselPageIndicatorInactiveColor = Color.LightGray.copy(alpha = 0.5f),
        fragmentInfoCopyrightTextColor = Color(0xffe6ad84),
        fragmentInfoIconTintColor = Color(0xffa46443),
        fragmentSeasonalMainTextColor = Color.Black,
        mainScreenBackgroundColor = Color.White,
        mainSplashScreenBackgroundColor = Color.White,
        mainSplashScreenForegroundColor = Color(0xff482505),
        mainSplashScreenSubTextColor = Color(0xff482505),
        mainZoomableBoxBackgroundColor = Color.White,
        screenAgendaChipTodayBackgroundColor = Color(0xfffdb308),
        screenAgendaChipSelectedBackgroundColor = Color(0xff482505),
        screenAgendaChipUnselectedBackgroundColor = Color.White,
        screenAgendaChipUnselectedBackgroundAlternativeColor = Color(0xffeeeeee),
        screenAgendaChipTextSelectedBackgroundColor = Color.White,
        screenAgendaChipTextUnselectedBackgroundColor = Color.Black,
        screenAgendaContentTintColor = Color.Black,
        screenAgendaItemBackgroundColor = Color(0xffFFE8BB),
        screenAgendaItemTimeBackgroundColor = Color(0xff482505),
        screenAgendaItemTimeTextColor = Color.White,
        screenAgendaItemProposalRutinBackgroundColor = Color(0xfffdb308),
        screenAgendaItemProposalStatusTextColor = Color.Black,
        screenAgendaStatusApprovedColor = Color(0xff40c057),
        screenAgendaStatusCancelledColor = Color.Red,
        screenAgendaStatusDeniedColor = Color.Red,
        screenAgendaStatusWaitingColor = Color.DarkGray,
        screenAgendaWeekdayTextColor = Color.Black,
        screenMainBottomNavSelectedIndicationColor = Color(0xffe6ad84),
        screenMainBottomNavSelectedIconColor = Color.Black,
        screenMainBottomNavSelectedTextColor = Color.Black,
        screenMainBottomNavUnselectedIconColor = Color.Black,
        screenMainBottomNavUnselectedTextColor = Color.Black,
        screenMainOverlayGradientBottomColor = Color.White,
        screenSearchClickableTextColor = Color.Black,
        screenSearchHistoryItemTextColor = Color(0xff715446)
    )
}
