/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.PersistentLogger

/**
 * Implementing push notification and other FCM-related functionalities.
 * SOURCE: https://www.geeksforgeeks.org/android/how-to-push-notification-in-android-using-firebase-cloud-messaging/
 * SOURCE: https://firebase.blog/posts/2023/08/adding-fcm-to-jetpack-compose-app
 */
class CloudMessageReceiver : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Logger.logFCM({}, "INCOMING_NEW_TOKEN: $token")
        PersistentLogger(applicationContext).write({}, "fcm-token: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let { message ->
            Logger.logFCM({}, "INCOMING_MESSAGE: $message")
            NotificationService.showFCMBasicNotification(applicationContext, message)
        }
    }
}
