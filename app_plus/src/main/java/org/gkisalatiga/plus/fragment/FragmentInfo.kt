/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.db.StaticCompanion
import org.gkisalatiga.plus.global.GlobalCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.Colors
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenStaticContentListCompanion
import org.json.JSONObject

class FragmentInfo (private val current: ActivityData) : ComponentActivity() {

    // The trigger to open an URL in an external browser.
    private var doTriggerBrowserOpen = mutableStateOf(false)

    // The link to open in an external browser or app.
    private var externalLinkURL = mutableStateOf("https://www.example.com")

    // The JSON node of the social media CTA.
    private val socialMediaJSONNode = MainCompanion.jsonRoot!!.getJSONObject("url-profile")

    // The list of node title.
    // This must be manually specified in the app.
    // private val socialMediaNodeTitles: MutableList<String> = mutableListOf()
    private val socialMediaNodeTitles = listOf(
        "web",
        "fb",
        "insta",
        "youtube",
        "whatsapp",
        "email"
    )

    // The list of social media icons.
    private val socialMediaIcons = listOf(
        R.drawable.remixicon_wordpress_fill_48,
        R.drawable.remixicon_facebook_box_fill_48,
        R.drawable.remixicon_instagram_fill_48,
        R.drawable.remixicon_youtube_fill_48,
        R.drawable.remixicon_whatsapp_fill_48,
        R.drawable.remixicon_at_fill_48
    )

    // The list of social media CTA targets.
    private val socialMediaCTATargets: MutableList<String> = mutableListOf()

    @Composable
    fun getComposable() {

        // Converting JSONArray to regular lists.
        val staticDataList: MutableList<JSONObject> = mutableListOf()
        for (i in 0 until StaticCompanion.jsonRoot!!.length()) {
            staticDataList.add(StaticCompanion.jsonRoot!![i] as JSONObject)
        }

        // Setting the layout to center both vertically and horizontally,
        // and then make it scrollable vertically.
        // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
        val scrollState = FragmentInfoCompanion.rememberedScrollState!!
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .padding(20.dp)
        ) {

            socialMediaNodeTitles.forEach {
                socialMediaCTATargets.add(socialMediaJSONNode.getString(it))
            }

            /* Display the individual "church info" card. */
            Column ( modifier = Modifier.padding(top = 10.dp) ) {
                staticDataList.forEachIndexed { _, itemObject ->

                    // The card title, thumbnail, etc.
                    var bannerURL = itemObject.getString("banner")
                    val title = itemObject.getString("title")

                    // For some reason, coil cannot render non-HTTPS images.
                    if (bannerURL.startsWith("http://")) bannerURL = bannerURL.replaceFirst("http://", "https://")

                    Card(
                        onClick = {
                            if (GlobalCompanion.DEBUG_ENABLE_TOAST) Toast.makeText(current.ctx, "You just clicked: $title!", Toast.LENGTH_SHORT).show()

                            // Display the church profile content folder list.
                            ScreenStaticContentListCompanion.targetStaticFolder = itemObject
                            AppNavigation.navigate(NavigationRoutes.SCREEN_STATIC_CONTENT_LIST)
                        },

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

            // The "open with mail" string text.
            val emailChooserTitle = stringResource(R.string.email_chooser_title)

            /* Displays the social media CTAs. */
            Spacer(Modifier.height(50.dp))
            Row (Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                // Assumes identical ArrayList size.
                socialMediaIcons.forEachIndexed { index, drawableIcon ->

                    Surface(Modifier.weight(1.0f).clickable(onClick = {
                        Logger.log({}, "Selected node: ${socialMediaNodeTitles[index]}")

                        if (socialMediaNodeTitles[index] == "email") {
                            // SOURCE: https://www.geeksforgeeks.org/how-to-send-an-email-from-your-android-app/
                            // SOURCE: https://www.tutorialspoint.com/android/android_sending_email.htm
                            // SOURCE: https://stackoverflow.com/a/59365539
                            val emailIntent = Intent(Intent.ACTION_SENDTO)
                            emailIntent.setData(Uri.parse("mailto:${socialMediaCTATargets[index]}"))
                            current.ctx.startActivity(Intent.createChooser(emailIntent, emailChooserTitle))
                        } else {
                            doTriggerBrowserOpen.value = true
                            externalLinkURL.value = socialMediaCTATargets[index]
                        }

                    })) {
                        Image(
                            painter = painterResource(drawableIcon),
                            "Social Media CTA No. ${socialMediaNodeTitles[index]}",
                            colorFilter = ColorFilter.tint(Colors.FRAGMENT_INFO_ICON_TINT_COLOR)
                        )
                    }
                }
            }

            /* Displays the copyright notice. */
            Column (Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(25.dp))
                Text(stringResource(R.string.about_copyright_notice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Colors.FRAGMENT_INFO_COPYRIGHT_TEXT_COLOR,
                    style = TextStyle (textAlign = TextAlign.Center),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

        }  // --- end of scrollable column.

        // Handles opening URLs in external browser.
        key(doTriggerBrowserOpen.value) {
            if (doTriggerBrowserOpen.value) {
                // Opens in an external browser.
                // SOURCE: https://stackoverflow.com/a/69103918
                current.uriHandler.openUri(externalLinkURL.value)

                doTriggerBrowserOpen.value = false
            }
        }
    }

}

class FragmentInfoCompanion : Application() {
    companion object {
        /* The fragment's remembered scroll state. */
        var rememberedScrollState: ScrollState? = null
    }
}