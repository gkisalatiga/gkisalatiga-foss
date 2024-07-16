/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.fragment

import android.app.DownloadManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import org.gkisalatiga.plus.R
// import coil.compose.AsyncImage
import org.gkisalatiga.plus.lib.AppDatabase
import org.gkisalatiga.plus.lib.DownloadAndSaveImageTask
import java.io.InputStream
import java.net.URL
import java.util.concurrent.Executors


class FragmentServices : ComponentActivity() {

    // The state of the currently selected chip.
    // (The default is to set the first chip as the one initially selected.)
    var selectedChip = listOf(
        mutableStateOf(true),
        mutableStateOf(false)
    )

    // The names of the chips.
    var nameOfChip = listOf(
        "Kebaktian Umum",
        "English Service"
    )

    /**
     * Navigation between screens
     * SOURCE: https://medium.com/@husayn.fakher/a-guide-to-navigation-in-jetpack-compose-questions-and-answers-d86b7e6a8523
     */
    @Composable
    public fun getComposable(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        Column {
            // Display the top chips for selecting between different services.
            // SOURCE: https://developer.android.com/develop/ui/compose/components/chip
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(20.dp)
                    ) {
                nameOfChip.forEachIndexed { index, item ->
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = {
                            // Set the current chip as selected and the rest of the other chips unselected.
                            selectedChip.forEach{ it.value = false }
                            selectedChip[index].value = true
                        },
                        label = { Text(item) },
                        selected = selectedChip[index].value,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        },
                    )
                }
            }

            // Setting the layout to center both vertically and horizontally
            // SOURCE: https://codingwithrashid.com/how-to-center-align-ui-elements-in-android-jetpack-compose/
            // ---
            // Enabling vertical scrolling
            // SOURCE: https://stackoverflow.com/a/72769561
            val scrollState = rememberScrollState()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
                    .padding(20.dp)
            ) {
                if (selectedChip[0].value) {
                    getRegularService(screenController, fragmentController, context)
                } else if (selectedChip[1].value) {
                    getEnglishService(screenController, fragmentController, context)
                }
            }
        }
    }

    @Composable
    private fun getEnglishService(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        // Get the application's JSON object
        val db = AppDatabase().loadRaw(context).getMainData()

        // Enlist the cards to be shown in this fragment
        val cardsList = listOf(
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es"),
            db.getJSONObject("broadcast").getJSONObject("es")
        )

        // Display the cards
        cardsList.forEach {
            val title = it.getString("title")
            val desc = it.getString("description")
            val imgSrc = it.getString("thumbnail")

            // Downloading the thumbnail and converting it to bitmap
            // val downloadedStream = mutableStateOf(InputStream.nullInputStream())
            // val bmp = BitmapFactory.decodeStream(downloadedStream.value)

            Card (
                onClick = { Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Column {
                    // Displaying the image
                    // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/loading
                    // SOURCE: https://stackoverflow.com/a/69689287
                    // Image(
                    // bitmap = bmp.asImageBitmap(),
                    // contentDescription = "Desc here"
                    // )

                    /*
                    AsyncImage(
                        model = imgSrc,
                        contentDescription = null,
                    )
                     */

                    DownloadAndSaveImageTask(context).execute(imgSrc)

                    Text("This is a sample bar", modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center )
                    Text(title)
                    Text(desc)
                }
            }
        }
    }

    @Composable
    private fun getRegularService(screenController: NavHostController, fragmentController: NavHostController, context: Context) {
        // Get the application's JSON object
        val db = AppDatabase().loadRaw(context).getMainData()

        // Enlist the cards to be shown in this fragment
        val cardsList = listOf(
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum"),
            db.getJSONObject("broadcast").getJSONObject("ibadah-umum")
        )

        // Display the cards
        cardsList.forEach {
            val title = it.getString("title")
            val desc = it.getString("description")
            val imgSrc = it.getString("thumbnail")

            // Downloading the thumbnail and converting it to bitmap
            // val downloadedStream = mutableStateOf(InputStream.nullInputStream())
            // val bmp = BitmapFactory.decodeStream(downloadedStream.value)

            Card (
                onClick = { Toast.makeText(context, "The card $title is clicked", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                Column {
                    // Displaying the image
                    // SOURCE: https://developer.android.com/develop/ui/compose/graphics/images/loading
                    // SOURCE: https://stackoverflow.com/a/69689287
                    // Image(
                    // bitmap = bmp.asImageBitmap(),
                    // contentDescription = "Desc here"
                    // )

                    /*
                    AsyncImage(
                        model = imgSrc,
                        contentDescription = null,
                    )
                     */

                    DownloadAndSaveImageTask(context).execute(imgSrc)

                    Text("This is a sample bar", modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center )
                    Text(title)
                    Text(desc)
                }
            }
        }
    }
}