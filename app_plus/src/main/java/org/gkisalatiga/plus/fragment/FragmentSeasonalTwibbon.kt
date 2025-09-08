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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.gkisalatiga.plus.data.ActivityData
import org.gkisalatiga.plus.db.ModulesCompanion

class FragmentSeasonalTwibbon (private val current : ActivityData) : ComponentActivity() {

    // The root seasonal node.
    // private val seasonalNode = ModulesCompanion.jsonRoot!!.getJSONObject("seasonal")
    private val seasonalNode = ModulesCompanion.api!!.seasonal

    @Composable
    fun getComposable() {
        Column {
            // Display the main title.
            // val mainTitle = seasonalNode.getJSONObject("static-menu").getJSONObject("twibbon").getString("title")
            val mainTitle = seasonalNode.staticMenu.twibbon.title.let { if (it.isNullOrBlank()) "" else it }
            Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Text(mainTitle, modifier = Modifier, fontWeight = FontWeight.Bold, fontSize = 24.sp, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(20.dp))

            Text("Testing Twibbon")
        }
    }

}
