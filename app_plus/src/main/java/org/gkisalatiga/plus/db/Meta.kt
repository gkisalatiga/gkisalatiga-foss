/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.db

import org.gkisalatiga.plus.data.APIMetaData
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.json.JSONObject

class Meta {
    companion object {
        private fun getEmptyAPIMetaData(): APIMetaData {
            return APIMetaData(
                lastUpdate = 0,
                lastActor = "",
                lastUpdatedItem = null,
                schemaVersion = "",
                updateCount = 0,
            )
        }

        fun parseData(jsonString: String): APIMetaData {
            try {
                // First, we read the JSON object.
                val obj = JSONObject(jsonString).getJSONObject("meta")

                // Then we initialized the API object.
                val api = getEmptyAPIMetaData()

                /* From this point on, we then populate the API data. */
                api.lastActor = obj.getString("last-actor")
                api.lastUpdate = obj.getInt("last-update")
                api.lastUpdatedItem = try {
                    obj.getString("last-updated-item")
                } catch (e: Exception) {
                    null
                }
                api.schemaVersion = obj.getString("schema-version")
                api.updateCount = obj.getInt("update-count")

                return api

            } catch (e: Exception) {
                e.printStackTrace()
                Logger.logTest({}, "Detected anomalies when parsing the JSON data: ${e.message}", LoggerType.ERROR)
                return getEmptyAPIMetaData()
            }
        }
    }
}