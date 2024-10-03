/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.gkisalatiga.plus.global.GlobalSchema

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_BOOT)  Log.d("Groaker-Boot", "BootCompletedReceiver got triggered.")
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_BOOT) Log.d("Groaker-Boot", "BOOT_COMPLETED received! Carrying out appropriate actions ...")

            // Restart the workers.
            WorkScheduler.scheduleYKBReminder(context!!)
        }
    }
}
