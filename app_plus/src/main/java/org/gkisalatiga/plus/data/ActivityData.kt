/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.data

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope

/**
 * This is the activity-wide data transmitted between screens and composables.
 * ---
 * Since Jetpack Compose (this project) has only one activity, we must seek a way to transmit
 * app context, coroutine, etc. across screens.
 * It can usually be done by calling [LocalContext.current].
 * However, we found that using data class like this gets rid of having to declare
 * and assign variables at the beginning of a composable function to get the current
 * app's lcoal context.
 * ---
 * This implementation prevents [StaticMemoryLeak] caused by storing app context statically
 * as a global variable.
 * See: https://github.com/gkisalatiga/gkisalatiga-foss/commit/e5d0386dd52519d27e187c523e475d6424947322
 */
data class ActivityData (
    val ctx: Context,
    val scope: CoroutineScope,
    val lifecycleOwner: LifecycleOwner,
    val lifecycleScope: LifecycleCoroutineScope,
)
