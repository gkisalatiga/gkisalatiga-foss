/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.composable

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import net.engawapg.lib.zoomable.ScrollGesturePropagation
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable

/**
 * Renders each individual PDF page, taking bitmap image as an argument.
 * @param pageBitmap the [Bitmap] to be displayed in the current PDF page.
 */
@Composable
fun PdfPage(pageBitmap: Bitmap, backgroundColor: Color) {
    val zoomState = rememberZoomState()
    AsyncImage(
        pageBitmap,
        modifier = Modifier.zoomable(
            zoomState,
            onDoubleTap = { position -> zoomState.toggleScale(PdfPageCompanion.PAGE_ZOOM_TARGET_SCALE, position) },
            scrollGesturePropagation = ScrollGesturePropagation.NotZoomed
        ).wrapContentSize().background(backgroundColor),
        contentDescription = "PdfPage Bitmap Rendering Composable",
        contentScale = ContentScale.Fit
    )
}

class PdfPageCompanion {
    companion object {
        /* Determines the maximum zoom scale of the image. */
        const val PAGE_ZOOM_TARGET_SCALE = 5.0f
    }
}
