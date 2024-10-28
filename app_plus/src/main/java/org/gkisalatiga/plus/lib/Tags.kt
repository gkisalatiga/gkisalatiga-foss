/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.lib

/**
 * Stores any system-wide (global) backend tags.
 */
class Tags {
    companion object {
        /* WorkManager periodic work tags. */
        const val TAG_MINUTELY_DEBUG = "tag_minutely_reminder_debug"
        const val TAG_SAREN_REMINDER = "tag_saren_reminder"
        const val TAG_YKB_REMINDER = "tag_ykb_reminder"

        /* WorkManager work names. */
        const val NAME_DEBUG_WORK = "work_debug"
        const val NAME_SAREN_WORK = "work_saren_reminder"
        const val NAME_YKB_WORK = "work_ykb_reminder"
    }
}
