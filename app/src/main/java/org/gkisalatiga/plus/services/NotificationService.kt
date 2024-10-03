package org.gkisalatiga.plus.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import okhttp3.internal.notify
import org.gkisalatiga.plus.ActivityLauncher
import org.gkisalatiga.plus.R
import java.util.concurrent.Executors


class NotificationService {
    companion object {

        val DEBUG_CHANNEL_ID = "debug_notif"
        val DEBUG_NOTIFICATION_ID = 999990

        val SAREN_CHANNEL_ID = "saren_notif"
        val SAREN_NOTIFICATION_ID = 12526

        val YKB_HARIAN_CHANNEL_ID = "daily_ykb_notif"
        val YKB_HARIAN_NOTIFICATION_ID = 12891

        /**
         * The notification channel for fallback and debugging.
         * SOURCE: https://medium.com/@anandmali/creating-a-basic-android-notification-5e5ee1614aae
         */
        fun initFallbackDebugChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Beta Version Notification Debugger"
                val desc = "Only used in the back-end to trace notification errors."
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(DEBUG_CHANNEL_ID, name, importance).apply { description = desc }

                // Do not use "ContextCompat.getSystemService".
                // SOURCE: https://stackoverflow.com/a/61709171
                val notificationManager: NotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Initializing the notification channel, for Android API 26+.
         * SOURCE: https://medium.com/@anandmali/creating-a-basic-android-notification-5e5ee1614aae
         */
        fun initSarenNotificationChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "SaRen Pagi"
                val desc = "Pengingat untuk bersaat teduh dan mendengarkan \"Sapaan dan Renungan Pagi\" setiap pukul 05:00 WIB di hari Senin-Sabtu."
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(SAREN_CHANNEL_ID, name, importance).apply { description = desc }

                // Do not use "ContextCompat.getSystemService".
                // SOURCE: https://stackoverflow.com/a/61709171
                val notificationManager: NotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Initializing the notification channel, for Android API 26+.
         * SOURCE: https://medium.com/@anandmali/creating-a-basic-android-notification-5e5ee1614aae
         */
        fun initYKBHarianNotificationChannel(ctx: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Renungan YKB"
                val desc = "Pengingat untuk membaca renungan YKB (Yayasan Komunikasi Bersama) di siang hari, setelah jam makan siang."
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(YKB_HARIAN_CHANNEL_ID, name, importance).apply { description = desc }

                // Do not use "ContextCompat.getSystemService".
                // SOURCE: https://stackoverflow.com/a/61709171
                val notificationManager: NotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        /**
         * Showing the debug notification.
         *
         * Also, add navigational actions and deep-linkings when the user clicks on the notifications.
         * SOURCE: https://composables.com/tutorials/deeplinks
         */
        fun showDebugNotification(ctx: Context) {
            val title = "Notification Debugger"
            val content = "If this notification gets triggered, there is something wrong with the scheduler. (Or, perhaps, you accidentally clicked the \"Easter Egg\"?"

            // Prepares the post-user click action handler (i.e., opening an activity).
            val intent = Intent(ctx, ActivityLauncher::class.java)
            intent.setData(Uri.parse("https://www.gkisalatiga.org"))
            val activity = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_MUTABLE)

            val builder = NotificationCompat.Builder(ctx, DEBUG_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_notif_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(activity)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))  // --- showing multiline content.
                .setAutoCancel(true)  // --- remove notification on user tap.

            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                with(NotificationManagerCompat.from(ctx)) {
                    notify(DEBUG_NOTIFICATION_ID, builder.build())
                }
            }
        }

        /**
         * Showing the saren notification.
         */
        fun showSarenNotification(ctx: Context) {
            val title = "SaRen Pagi"
            val content = "Shalom! Mari bersaat teduh sejenak bersama GKI Salatiga+"

            // Prepares the post-user click action handler (i.e., opening an activity).
            val intent = Intent(ctx, ActivityLauncher::class.java)
            intent.setData(Uri.parse("https://gkisalatiga.org/plus/deeplink/saren"))
            val activity = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_MUTABLE)

            val builder = NotificationCompat.Builder(ctx, SAREN_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_notif_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(activity)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))  // --- showing multiline content.
                .setAutoCancel(true)  // --- remove notification on user tap.

            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                with(NotificationManagerCompat.from(ctx)) {
                    notify(SAREN_NOTIFICATION_ID, builder.build())
                }
            }
        }

        /**
         * Showing the YKB daily notification.
         */
        fun showYKBHarianNotification(ctx: Context) {
            val title = "Renungan YKB"
            val content = "Shalom! Sedang penatkah kehidupan Anda? Mari luangkan waktu sebentar untuk membaca renungan YKB. " +
                    "Pulihkan kekuatan dan semangat Anda dengan membaca firman Tuhan di jam rawan ini."

            // Prepares the post-user click action handler (i.e., opening an activity).
            val intent = Intent(ctx, ActivityLauncher::class.java)
            intent.setData(Uri.parse("https://gkisalatiga.org/plus/deeplink/ykb"))
            val activity = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_MUTABLE)

            val builder = NotificationCompat.Builder(ctx, YKB_HARIAN_CHANNEL_ID)
                .setSmallIcon(R.drawable.app_notif_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(activity)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))  // --- showing multiline content.
                .setAutoCancel(true)  // --- remove notification on user tap.

            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                with(NotificationManagerCompat.from(ctx)) {
                    notify(YKB_HARIAN_NOTIFICATION_ID, builder.build())
                }
            }
        }

    }  // --- end of companion object.
}  // --- end of class.