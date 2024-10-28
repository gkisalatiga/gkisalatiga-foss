/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.services.NotificationService
import org.gkisalatiga.plus.services.WorkScheduler

/**
 * Creates a specific-action worker that gets triggered by a WorkManager.
 * SOURCE: https://medium.com/@ifr0z/workmanager-notification-date-and-time-pickers-aad1d938b0a3
 */
class SarenNotificationWorker(private val context: Context, private val params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Logger.logWorker({}, "Carrying out the SarenNotificationWorker ...")

        // Perform the work.
        NotificationService.showSarenNotification(context)

        Logger.logWorker({}, "What do we have here? ${params.tags}")

        // Carry out the rescheduling.
        WorkScheduler.scheduleSarenReminder(context)

        return Result.success()
    }
}
