/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.data

import org.gkisalatiga.plus.lib.PreferenceSettingItem

/**
 * Controls whether a given app preferences item is selected.
 * It also determines the state of the currently selected value of a given preference item.
 * @param prefItem the [PreferenceSettingItem] associated with this settings item value.
 * @param stringText the localized text of this settings item value.
 * @param isActive whether this item is selected.
 */
data class PrefItemData (
    val prefItem: PreferenceSettingItem,
    val stringText: String,
    val isActive: Boolean,
)