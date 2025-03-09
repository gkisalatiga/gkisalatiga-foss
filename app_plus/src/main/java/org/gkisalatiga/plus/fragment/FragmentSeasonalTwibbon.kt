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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.gkisalatiga.plus.data.ActivityData

class FragmentSeasonalTwibbon (private val current : ActivityData) : ComponentActivity() {

    @Composable
    fun getComposable() {
        Text("Testing Twibbon")
    }

}
