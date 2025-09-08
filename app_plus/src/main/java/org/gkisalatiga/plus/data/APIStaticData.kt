/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.data

data class StaticFolderObject(
    var banner: String,
    var content: MutableList<StaticContentItemObject>,
    var title: String,
)

data class StaticContentItemObject(
    var featuredImage: String,
    var html: String,
    var subtitle: String,
    var title: String,
)
