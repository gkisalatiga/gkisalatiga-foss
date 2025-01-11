/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import org.gkisalatiga.plus.global.GlobalCompanion

class PermissionChecker (private val ctx: Context) {
    fun checkNotificationPermission() {
        GlobalCompanion.isNotificationGranted.value =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
                ctx.packageManager.checkPermission(Manifest.permission.POST_NOTIFICATIONS, ctx.packageName) == PackageManager.PERMISSION_GRANTED
            else
                true
    }
}
