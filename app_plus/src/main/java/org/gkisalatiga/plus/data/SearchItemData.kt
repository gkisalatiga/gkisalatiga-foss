/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.data

import org.gkisalatiga.plus.model.SearchDataType
import org.json.JSONObject

/**
 * This data class is returned by the search view model
 * so that the matching contents can be displayed in [ScreenSearch].
 * @param matchingSearchQuery the search query which summons this search item data.
 * @param title the title of this search item (for alphabetical sorting).
 * @param date the date this search item was created (for sorting by date).
 * @param dataType the type of this search item corresponding to a given JSON data node.
 * @param content the content extracted from the JSON data.
 * @param tag1 additional tag that needs to be specified to classify a given search item.
 */
data class SearchItemData (
    val matchingSearchQuery: String,
    val title: String,
    val date: String,
    val dataType: SearchDataType,
    val content: Any,
    val tag1: String = String(),
    val tag2: String = String(),
)
