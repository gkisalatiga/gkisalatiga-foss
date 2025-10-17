/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.data

data class APIModulesData(
    var bible: MutableList<ModulesBibleItemObject>,
    var library: MutableList<ModulesLibraryItemObject>,
    var attributions: ModulesAttributionsRootObject,
    var seasonal: ModulesSeasonalObject,
)

data class ModulesBibleItemObject(
    var name: String,
    var abbr: String,
    var lang: String,
    var source: String,
    var sourceJson: String,
    var sourceSize: String,
    var filename: String,
    var license: String,
    var licenseUrl: String,
    var author: String,
    var authorUrl: String,
    var description: String,
)

data class ModulesLibraryItemObject(
    var title: String,
    var author: String,
    var publisher: String,
    var publisherLoc: String,
    var year: String,
    var thumbnail: String,
    var downloadUrl: String,
    var source: String,
    var size: String,
    var tags: MutableList<String>,
    var isShown: Int,
)

data class ModulesAttributionsRootObject(
    var webview: MutableList<ModulesAttributionsItemObject>,
    var books: MutableList<ModulesAttributionsItemObject>,
)

data class ModulesAttributionsItemObject(
    var title: String,
    var license: String,
    var licenseUrl: String,
    var year: String,
    var author: String,
    var link: String,
)

data class ModulesSeasonalObject(
    var title: String,
    var bannerFront: String,
    var bannerInside: String,
    var staticMenu: ModulesSeasonalStaticObject,
)

data class ModulesSeasonalStaticObject(
    var agenda: ModulesSeasonalStaticItemObject,
    var books: ModulesSeasonalStaticItemObject,
    var gallery: ModulesSeasonalStaticItemObject,
    var playlist: ModulesSeasonalStaticItemObject,
    var twibbon: ModulesSeasonalStaticItemObject,
)

data class ModulesSeasonalStaticItemObject(
    // Must be present in every seasonal static item.
    var title: String,
    var banner: String,
    var isShown: Int,

    // Item-specific keywords.
    var url: String?,
    var selectionTag: String?,
    var albumKeyword: String?,
    var ytPlaylist: String?,
    var twibs: MutableList<ModulesSeasonalTwibbonItemObject>?,
)

data class ModulesSeasonalTwibbonItemObject(
    var title: String,
    var url: String,
    var postPage: String,
)
