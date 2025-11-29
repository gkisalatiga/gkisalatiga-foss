/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.data

data class BibleData(
    var meta: BibleMetaObject,
    var books: MutableList<BibleBookObject>,
    var verses: MutableList<BibleVerseObject>,
)

data class BibleMetaObject(
    var name: String,
    var abbr: String,
    var license: String,
    var licenseUrl: String,
    var author: String,
    var authorUrl: String,
    var source: String,
    var desc: String,
)

data class BibleBookObject(
    var code: String,
    var abbr: String,
    var short: String,
    var long: String,
    var alt: String,
)

data class BibleVerseObject(
    var bookCode: String,
    var chapter: Int,
    var verse: Int,
    var text: String,
)