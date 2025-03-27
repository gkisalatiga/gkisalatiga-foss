package org.gkisalatiga.plus.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.interfaces.ComposableInterface

class FileDownloadDialog() : ComposableInterface {
    @Suppress("SpellCheckingInspection")
    companion object {
        /* Stores the state of the current file downloading/loading. */
        private val mutableFileDownloadPercentage = mutableIntStateOf(0)
        private val mutableFileDownloadFilename = mutableStateOf("")
        private val mutableShowLoadingDialog = mutableStateOf(false)
        private val txtLoadingPercentageAnimatable = Animatable(0.0f)
    }

    override val composableName: String
        get() = "FileDownloadDialog"
    override val isHidden: Boolean
        get() = mutableShowLoadingDialog.value

    @Composable
    override fun draw(onConfirmRequest: (() -> Unit)?, onDismissRequest: (() -> Unit)?): FileDownloadDialog {
        fun dismiss() {
            mutableShowLoadingDialog.value = false
            if (onDismissRequest != null) onDismissRequest()
        }

        val loadingDialogTitle = stringResource(R.string.composable_filedownloaddialog_loading_dialog_title)
        val loadingDialogSubtitle = stringResource(R.string.composable_filedownloaddialog_loading_dialog_subtitle)
            .replace("%%%", mutableFileDownloadFilename.value)
        val loadingDialogBtnString = stringResource(R.string.screen_pdfviewer_loading_dialog_btn_string)

        if (mutableShowLoadingDialog.value) {
            AlertDialog(
                onDismissRequest = { dismiss() },
                title = { Text(loadingDialogTitle) },
                text = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(loadingDialogSubtitle)
                        Row(Modifier.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            LinearProgressIndicator(
                                progress = { txtLoadingPercentageAnimatable.value },
                                modifier = Modifier.fillMaxWidth().weight(5.0f),
                            )
                            Text("${mutableFileDownloadPercentage.intValue}%",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1.0f),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { dismiss() }) {
                        Text(loadingDialogBtnString, color = Color.White)
                    }
                }
            )
        }

        // Animating the progress bar during downloads.
        LaunchedEffect(key1 = mutableFileDownloadPercentage.intValue) {
            val floatPercentage = mutableFileDownloadPercentage.intValue.toFloat() / 100
            txtLoadingPercentageAnimatable.animateTo(targetValue = floatPercentage, animationSpec = tween(durationMillis = 75, easing = { FastOutSlowInEasing.transform(it) /*OvershootInterpolator(2f).getInterpolation(it)*/ }))
        }

        return this
    }

    override fun hide(): FileDownloadDialog {
        mutableShowLoadingDialog.value = false
        return this
    }

    fun resetPercentage(): FileDownloadDialog {
        mutableFileDownloadPercentage.intValue = 0
        return this
    }

    override fun show(): FileDownloadDialog {
        mutableShowLoadingDialog.value = true
        return this
    }

    fun show(filename: String): FileDownloadDialog {
        mutableFileDownloadFilename.value = filename
        mutableShowLoadingDialog.value = true
        return this
    }

    fun show(filename: String, percentage: Int): FileDownloadDialog {
        mutableFileDownloadPercentage.intValue = percentage
        mutableFileDownloadFilename.value = filename
        mutableShowLoadingDialog.value = true

        return this
    }
}