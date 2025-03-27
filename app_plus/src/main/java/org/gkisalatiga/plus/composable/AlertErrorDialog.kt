package org.gkisalatiga.plus.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.interfaces.ComposableInterface

class AlertErrorDialog : ComposableInterface {

    companion object {
        private val mutableShowAlertDialog = mutableStateOf(false)
        private val txtAlertDialogSubtitle = mutableStateOf("")
    }

    override val composableName: String
        get() = "AlertErrorDialog"
    override val isHidden: Boolean
        get() = mutableShowAlertDialog.value

    @Composable
    override fun draw(onConfirmRequest: (() -> Unit)?, onDismissRequest: (() -> Unit)?): AlertErrorDialog {
        fun dismiss() {
            mutableShowAlertDialog.value = false
            if (onDismissRequest != null) onDismissRequest()
        }

        val alertDialogTitle = stringResource(R.string.composable_alerterrordialog_error_dialog_title)
        val alertDialogBtnString = stringResource(R.string.composable_alerterrordialog_error_dialog_btn_string)
        if (mutableShowAlertDialog.value) {
            AlertDialog(
                onDismissRequest = { dismiss() },
                title = { Text(alertDialogTitle) },
                text = { Text(txtAlertDialogSubtitle.value) },
                confirmButton = {
                    Button(onClick = { dismiss() }) {
                        Text(alertDialogBtnString, color = Color.White)
                    }
                }
            )
        }

        return this
    }

    override fun hide(): AlertErrorDialog {
        if (mutableShowAlertDialog.value) mutableShowAlertDialog.value = false
        return this
    }

    override fun show(): AlertErrorDialog {
        if (!mutableShowAlertDialog.value) mutableShowAlertDialog.value = true
        return this
    }

    fun show(subtitle: String): AlertErrorDialog {
        txtAlertDialogSubtitle.value = subtitle
        if (!mutableShowAlertDialog.value) mutableShowAlertDialog.value = true
        return this
    }
}