/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 * ---
 *
 * AsyncImage.
 * SOURCE: https://coil-kt.github.io/coil/compose/
 */

package org.gkisalatiga.plus.fragment

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenSeasonalCompanion

class FragmentSeasonalMain (private val current : ActivityData) : ComponentActivity() {

    private val itemTargetTitle = listOf(
        "extract. AGENDA",
        "extract. BOOK",
        "extract. GALERI",
        "extract. TWIB",
    )

    private val itemTargetBannerUrl = listOf(
        "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/refs/heads/main/images-carousel/carousel_warta_beasiswa_diakonia.webp",
        "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/refs/heads/main/images-carousel/carousel_warta_beasiswa_diakonia.webp",
        "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/refs/heads/main/images-carousel/carousel_warta_beasiswa_diakonia.webp",
        "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/refs/heads/main/images-carousel/carousel_warta_beasiswa_diakonia.webp",
    )

    private val itemTargetFragments = listOf(
        NavigationRoutes.FRAG_SEASONAL_AGENDA,
        NavigationRoutes.FRAG_SEASONAL_BOOK,
        NavigationRoutes.FRAG_SEASONAL_GALLERY,
        NavigationRoutes.FRAG_SEASONAL_TWIBBON,
    )

    @Composable
    fun getComposable() {

        /* Display the individual "church info" card. */
        Column ( modifier = Modifier.padding(top = 10.dp) ) {
            itemTargetTitle.forEachIndexed { idx, _ ->

                // The card title, thumbnail, etc.
                var bannerURL = itemTargetBannerUrl[idx]
                val title = itemTargetTitle[idx]

                // For some reason, coil cannot render non-HTTPS images.
                if (bannerURL.startsWith("http://")) bannerURL = bannerURL.replaceFirst("http://", "https://")

                Card(
                    onClick = { ScreenSeasonalCompanion.mutableLastPage.value = itemTargetFragments[idx] },
                    modifier = Modifier.padding(bottom = 10.dp).aspectRatio(2.4f).fillMaxWidth()
                ) {

                    // Displaying the text-overlaid image.
                    Box {
                        /* The background featured image. */
                        // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/customize
                        // ---
                        val contrast = 1.1f  // --- 0f..10f (1 should be default)
                        val brightness = 0.0f  // --- -255f..255f (0 should be default)
                        AsyncImage(
                            model = bannerURL,
                            contentDescription = "Profile page: $title",
                            error = painterResource(R.drawable.thumbnail_error_notext),
                            placeholder = painterResource(R.drawable.thumbnail_placeholder),
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.colorMatrix(ColorMatrix(
                                floatArrayOf(
                                    contrast, 0f, 0f, 0f, brightness,
                                    0f, contrast, 0f, 0f, brightness,
                                    0f, 0f, contrast, 0f, brightness,
                                    0f, 0f, 0f, 1f, 0f
                                )
                            ))
                        )

                        /* Add shadow-y overlay background so that the white text becomes more visible. */
                        // SOURCE: https://developer.android.com/develop/ui/compose/graphics/draw/brush
                        // SOURCE: https://stackoverflow.com/a/60479489
                        Box (
                            modifier = Modifier
                                // Color pattern: 0xAARRGGBB (where "AA" is the alpha value).
                                .background(Color(0x40fda308))
                                .matchParentSize()
                        )

                        /* The card description label. */
                        Column (horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = title,
                                fontSize = 22.sp,
                                color = Color.White,
                                modifier = Modifier.padding(start = 20.dp).padding(bottom = 20.dp),
                                style = TextStyle(
                                    shadow = Shadow(Color.Black, Offset(3.0f, 3.0f), 8.0f)
                                )
                            )
                        }
                    }  // --- end of box.

                }
            }
        }  // --- end of church info card/column.
    }

}
