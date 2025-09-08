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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.ModulesCompanion
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenSeasonalCompanion

class FragmentSeasonalMain (private val current : ActivityData) : ComponentActivity() {

    // The root seasonal node.
    // private val seasonalNode = ModulesCompanion.jsonRoot!!.getJSONObject("seasonal")
    private val seasonalNode = ModulesCompanion.api!!.seasonal

    private val itemTargetTitle = listOf(
        seasonalNode.staticMenu.agenda.title,
        seasonalNode.staticMenu.books.title,
        seasonalNode.staticMenu.gallery.title,
        seasonalNode.staticMenu.twibbon.title,
    )
    /*private val itemTargetTitle = listOf(
        seasonalNode.getJSONObject("static-menu").getJSONObject("agenda").getString("title"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("books").getString("title"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("gallery").getString("title"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("twibbon").getString("title"),
    )*/

    private val itemTargetBannerUrl = listOf(
        seasonalNode.staticMenu.agenda.banner,
        seasonalNode.staticMenu.books.banner,
        seasonalNode.staticMenu.gallery.banner,
        seasonalNode.staticMenu.twibbon.banner,
    )
    /*private val itemTargetBannerUrl = listOf(
        seasonalNode.getJSONObject("static-menu").getJSONObject("agenda").getString("banner"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("books").getString("banner"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("gallery").getString("banner"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("twibbon").getString("banner"),
    )*/

    private val itemTargetFragments = listOf(
        NavigationRoutes.FRAG_SEASONAL_AGENDA,
        NavigationRoutes.FRAG_SEASONAL_BOOK,
        NavigationRoutes.FRAG_SEASONAL_GALLERY,
        NavigationRoutes.FRAG_SEASONAL_TWIBBON,
    )

    private val itemTargetIsShown = listOf(
        seasonalNode.staticMenu.agenda.isShown,
        seasonalNode.staticMenu.books.isShown,
        seasonalNode.staticMenu.gallery.isShown,
        seasonalNode.staticMenu.twibbon.isShown,
    )
    /*private val itemTargetIsShown = listOf(
        seasonalNode.getJSONObject("static-menu").getJSONObject("agenda").getInt("is_shown"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("books").getInt("is_shown"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("gallery").getInt("is_shown"),
        seasonalNode.getJSONObject("static-menu").getJSONObject("twibbon").getInt("is_shown"),
    )*/

    @Composable
    fun getComposable() {

        /* Display the individual "church info" card. */
        Column ( modifier = Modifier.fillMaxSize() ) {
            itemTargetTitle.forEachIndexed { idx, _ ->
                // Respect visibility flag state.
                if (itemTargetIsShown[idx] == 1) {
                    // The card title, thumbnail, etc.
                    var bannerURL = itemTargetBannerUrl[idx]
                    val title = itemTargetTitle[idx]

                    // For some reason, coil cannot render non-HTTPS images.
                    if (bannerURL.startsWith("http://")) bannerURL = bannerURL.replaceFirst("http://", "https://")

                    Card(
                        onClick = {
                            current.scope.launch {
                                ScreenSeasonalCompanion.rememberedScrollState!!.animateScrollTo(0)
                            }
                            current.scope.launch {
                                delay(100L)
                                ScreenSeasonalCompanion.mutableLastPage.value = itemTargetFragments[idx]
                            }
                        },
                        modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
                    ) {

                        Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start) {
                            // Displaying the text-overlaid image.
                            Surface(shape = RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp), modifier = Modifier.fillMaxWidth().aspectRatio(2.4f)) {
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
                                }  // --- end of box.
                            }

                            // The card description text.
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                color = current.colors.fragmentSeasonalMainTextColor,
                                modifier = Modifier.padding(vertical = 8.5.dp).padding(horizontal = 15.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Start
                            )

                        }

                    }  // --- end of card.
                }  // --- end if.
            }  // --- end of forEachIndexed {}.
        }  // --- end of church info card/column.
    }

}
