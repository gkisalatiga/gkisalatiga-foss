/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's database and information retrieval, whether stored in the APK
 * or downloaded online.
 */

package org.gkisalatiga.plus.model

import android.app.Application
import org.gkisalatiga.plus.data.BibleBookObject
import org.gkisalatiga.plus.data.BibleData
import org.gkisalatiga.plus.data.BibleMetaObject
import org.gkisalatiga.plus.data.BibleVerseObject
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream

class BibleDataModel() {

    private var _parsedJSONString: String = ""

    /**
     * Loads a given JSON file (in the phone's absolute path, not the app's Android resource manager),
     * and parse them into string inside the class.
     * SOURCE: https://stackoverflow.com/a/45202002
     */
    private fun loadJSON(absolutePathToJSON: String) {
        // SOURCE: https://stackoverflow.com/a/45202002
        val file = File(absolutePathToJSON)
        val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }

        this._parsedJSONString = inputAsString
    }

    /**
     * Initializes the locally downloaded/stored main data.
     * Then assign the global variable that handles it.
     */
    fun initBibleData() {
        BibleDataModelCompanion.curData = getBibleData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's main data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getBibleData(): BibleData {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(BibleDataModelCompanion.curLocalBiblePath!!)
        return BibleDataParser.parseData(this._parsedJSONString)
    }
}

class BibleDataModelCompanion : Application() {
    companion object {
        /* The JSON object that will be accessed by screens. */
        var curLocalBiblePath: String? = null
        var curData: BibleData? = null
    }
}

class BibleDataParser {
    companion object {
        private fun getEmptyBibleData(): BibleData {
            return BibleData(
                meta = BibleMetaObject(
                    name = "",
                    abbr = "",
                    license = "",
                    licenseUrl = "",
                    author = "",
                    authorUrl = "",
                    source = "",
                    desc = "",
                ),
                books = mutableListOf(),
                verses = mutableListOf(),
            )
        }

        fun parseData(jsonString: String): BibleData {
            try {
                // First, we read the JSON object.
                val obj = JSONObject(jsonString)

                // Then we initialized the API object.
                val api = getEmptyBibleData()

                /* From this point on, we then populate the API data. */

                val metaObj = obj.getJSONObject("meta")
                api.meta.name = metaObj.getString("name")
                api.meta.abbr = metaObj.getString("abbr")
                api.meta.license = metaObj.getString("license")
                api.meta.licenseUrl = metaObj.getString("license-url")
                api.meta.author = metaObj.getString("author")
                api.meta.authorUrl = metaObj.getString("author-url")
                api.meta.source = metaObj.getString("source")
                api.meta.desc = metaObj.getString("description")

                obj.getJSONObject("index").keys().forEach {
                    val curNode = obj.getJSONObject("index").getJSONObject(it)
                    api.books.add(
                        BibleBookObject(
                            code = curNode.getString("code"),
                            abbr = curNode.getString("abbr"),
                            short = curNode.getString("short"),
                            long = curNode.getString("long"),
                            alt = curNode.getString("alt"),
                        )
                    )
                }

                obj.getJSONArray("verses").let {
                    for (i in 0 until it.length()) {
                        val curNode = it[i] as JSONObject
                        api.verses.add(
                            BibleVerseObject(
                                bookCode = curNode.getString("b"),
                                chapter = curNode.getInt("c"),
                                verse = curNode.getInt("v"),
                                text = curNode.getString("t"),
                            )
                        )
                    }
                }

                return api

            } catch (e: Exception) {
                e.printStackTrace()
                Logger.logBible({}, "Detected anomalies when parsing the JSON data: ${e.message}", LoggerType.ERROR)
                return getEmptyBibleData()
            }
        }
    }
}