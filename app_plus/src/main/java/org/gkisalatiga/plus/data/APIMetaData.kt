/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.data

data class APIMetaData(
    var updateCount: Int,
    var lastUpdate: Int,
    var lastUpdatedItem: String?,
    var lastActor: String,
    var schemaVersion: String,
)
