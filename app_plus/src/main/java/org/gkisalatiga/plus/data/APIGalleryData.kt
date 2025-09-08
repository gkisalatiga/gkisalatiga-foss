/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.data

data class GalleryYearObject(
    var title: String,
    var albumData: MutableList<GalleryAlbumObject>,
)

data class GalleryAlbumObject(
    var folderId: String,
    var lastUpdate: String,
    var featuredPhotoId: String,
    var story: String,
    var title: String,
    var eventDate: String,
    var photos: MutableList<GalleryItemObject>,
)

data class GalleryItemObject(
    var id: String,
    var name: String,
    var date: String,
)
