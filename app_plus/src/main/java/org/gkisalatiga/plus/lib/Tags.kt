/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.lib

/**
 * Stores any system-wide (global) backend tags.
 */
enum class Tags {
    /* WorkManager work names. */
    NAME_DEBUG_WORK,
    NAME_SAREN_WORK,
    NAME_YKB_WORK,

    /* WorkManager periodic work tags. */
    TAG_MINUTELY_DEBUG,
    TAG_SAREN_REMINDER,
    TAG_YKB_REMINDER,
}