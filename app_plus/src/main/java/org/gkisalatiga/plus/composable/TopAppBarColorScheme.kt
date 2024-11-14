package org.gkisalatiga.plus.composable

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import org.gkisalatiga.plus.lib.Colors

class TopAppBarColorScheme {
    companion object {
        @Composable
        @OptIn(ExperimentalMaterial3Api::class)
        fun default() = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Colors.COLOR_SCHEME_PRIMARY,
            titleContentColor = Colors.MAIN_TOP_BAR_CONTENT_COLOR,
            navigationIconContentColor = Colors.MAIN_TOP_BAR_CONTENT_COLOR,
            actionIconContentColor = Colors.MAIN_TOP_BAR_CONTENT_COLOR,
        )
    }
}