package org.gkisalatiga.plus.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.interfaces.ComposableInterface

class FileDeleteDialog : ComposableInterface {

    companion object {
        private val mutableDeletableFilename = mutableStateOf("")
        private val mutableDeletableFileUrl = mutableStateOf("")
        private val mutableShowPdfDeleteDialog = mutableStateOf(false)
    }

    override val composableName: String
        get() = "FileDeleteDialog"
    override val isHidden: Boolean
        get() = mutableShowPdfDeleteDialog.value

    @Composable
    override fun draw(onConfirmRequest: (() -> Unit)?, onDismissRequest: (() -> Unit)?): FileDeleteDialog {
        fun confirm() {
            mutableShowPdfDeleteDialog.value = false
            if (onConfirmRequest != null) onConfirmRequest()
        }

        fun dismiss() {
            mutableShowPdfDeleteDialog.value = false
            if (onDismissRequest != null) onDismissRequest()
        }

        if (mutableShowPdfDeleteDialog.value) {
            AlertDialog(
                onDismissRequest = { dismiss() },
                title = { Text(stringResource(R.string.composable_filedeletedialog_dialog_title)) },
                text = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(R.string.composable_filedeletedialog_dialog_desc).replace("%%%TITLE%%%", mutableDeletableFilename.value))
                    }
                },
                confirmButton = {
                    Row {
                        Button(onClick = { confirm() }) {
                            Text(stringResource(R.string.composable_filedeletedialog_dialog_yes), color = Color.White)
                        }
                        Spacer(Modifier.width(10.dp))
                        Button(onClick = { dismiss() }) {
                            Text(stringResource(R.string.composable_filedeletedialog_dialog_no), color = Color.White)
                        }
                    }
                }
            )
        }

        return this
    }

    override fun hide(): FileDeleteDialog {
        mutableShowPdfDeleteDialog.value = false
        return this
    }

    override fun show(): FileDeleteDialog {
        mutableShowPdfDeleteDialog.value = true
        return this
    }

    fun getFileNameToDelete() : String {
        return mutableDeletableFilename.value
    }

    fun getFileUrlToDelete() : String {
        return mutableDeletableFileUrl.value
    }

    fun show(filename: String): FileDeleteDialog {
        mutableDeletableFilename.value = filename
        mutableShowPdfDeleteDialog.value = true
        return this
    }

    fun show(filename: String, fileUrl: String): FileDeleteDialog {
        mutableDeletableFilename.value = filename
        mutableDeletableFileUrl.value = fileUrl
        mutableShowPdfDeleteDialog.value = true
        return this
    }
}