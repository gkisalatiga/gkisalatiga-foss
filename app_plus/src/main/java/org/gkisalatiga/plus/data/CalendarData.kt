/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.data

import java.util.Calendar

data class CalendarData (
    val cal: Calendar,
    val dateString: String,
    val tense: TenseStatus,
    val weekday: String,  // --- "mon", "tue", "wed", etc.
)

enum class TenseStatus {
    PAST,
    PRESENT,  // --- this means "today".
    FUTURE
}