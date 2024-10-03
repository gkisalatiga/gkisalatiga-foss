/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.fdroid.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.gkisalatiga.fdroid.global.GlobalSchema
import org.gkisalatiga.fdroid.services.NotificationService
import org.gkisalatiga.fdroid.services.WorkScheduler

/**
 * Creates a specific-action worker that gets triggered by a WorkManager.
 * SOURCE: https://medium.com/@ifr0z/workmanager-notification-date-and-time-pickers-aad1d938b0a3
 */
class YKBNotificationWorker(private val context: Context, private val params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_WORKER) Log.d("Groaker-Worker", "[YKBNotificationWorker.doWork] Carrying out the YKBNotificationWorker ...")

        // Perform the work.
        NotificationService.showYKBHarianNotification(context)

        if (GlobalSchema.DEBUG_ENABLE_LOG_CAT_WORKER) Log.d("Groaker-Worker", "[YKBNotificationWorker.doWork] What do we have here? ${params.tags}")

        // Carry out the rescheduling.
        WorkScheduler.scheduleYKBReminder(context)

        return Result.success()
    }
}
