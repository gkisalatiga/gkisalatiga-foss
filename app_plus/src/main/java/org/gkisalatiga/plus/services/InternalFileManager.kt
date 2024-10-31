/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import android.content.Context
import org.gkisalatiga.plus.global.GlobalCompanion

/**
 * Manages the app's internal file management.
 * @param ctx the current local context of the app.
 */
class InternalFileManager (ctx: Context) {
    // The file creator to create the private file (as a downloaded file).
    val DOWNLOAD_FILE_CREATOR = ctx.getDir(GlobalCompanion.FILE_CREATOR_TARGET_DOWNLOAD_DIR, Context.MODE_PRIVATE)
}
