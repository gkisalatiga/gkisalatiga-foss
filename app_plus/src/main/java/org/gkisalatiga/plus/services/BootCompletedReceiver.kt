/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.gkisalatiga.plus.lib.Logger

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.logBoot({}, "BootCompletedReceiver got triggered.")
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED") {
            Logger.logBoot({}, "BOOT_COMPLETED received! Carrying out appropriate actions ...")

            // Restart the workers.
            WorkScheduler.scheduleYKBReminder(context!!)
        }
    }
}
