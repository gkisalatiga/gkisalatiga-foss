package org.gkisalatiga.plus.interfaces

import androidx.compose.runtime.Composable

interface ComposableInterface {
    val composableName: String
    val isHidden: Boolean

    @Composable
    fun draw(onConfirmRequest: (() -> Unit)?, onDismissRequest: (() -> Unit)?) : ComposableInterface

    fun hide() : ComposableInterface

    fun show() : ComposableInterface
}