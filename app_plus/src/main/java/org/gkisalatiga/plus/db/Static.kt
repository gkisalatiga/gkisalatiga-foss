/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages the application's database and information retrieval, whether stored in the APK
 * or downloaded online.
 */

package org.gkisalatiga.plus.db

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import org.gkisalatiga.plus.R
import org.gkisalatiga.plus.data.APIMetaData
import org.gkisalatiga.plus.data.GalleryAlbumObject
import org.gkisalatiga.plus.data.GalleryItemObject
import org.gkisalatiga.plus.data.GalleryYearObject
import org.gkisalatiga.plus.data.StaticContentItemObject
import org.gkisalatiga.plus.data.StaticFolderObject
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.services.InternalFileManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class Static(private val ctx: Context) {

    private var _parsedJSONString: String = ""

    /**
     * Loads a given JSON file (in the phone's absolute path, not the app's Android resource manager),
     * and parse them into string inside the class.
     * SOURCE: https://stackoverflow.com/a/45202002
     */
    fun loadJSON(absolutePathToJSON: String) {
        // SOURCE: https://stackoverflow.com/a/45202002
        val file = File(absolutePathToJSON)
        val inputAsString = try {
            FileInputStream(file).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.logTest({}, "Cannot load JSON buffered reader: ${e.message}", LoggerType.ERROR)
            ""
        }

        this._parsedJSONString = inputAsString
    }

    /**
     * Experimental; please only use this function in debug mode.
     * This function returns the parsed JSON's entire string content.
     * Could be dangerous.
     */
    fun getRawDumped(): String {
        return this._parsedJSONString
    }

    /**
     * Returns the fallback JSONObject stored and packaged within the app.
     * This is useful especially when the app has not yet loaded the refreshed JSON metadata
     * from the internet yet.
     */
    fun getFallbackStaticData(): MutableList<StaticFolderObject> {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_static)
        val inputAsString: String = input.bufferedReader().use { it.readText() }
        val secondInput: InputStream = ctx.resources.openRawResource(R.raw.fallback_static)
        val inputAsByteArray = secondInput.use { it.readBytes() }

        // Write the raw-resource-shipped file buffer as an actual file.
        // Creating the private file.
        val privateFile = File(InternalFileManager(ctx).DATA_DIR_FILE_CREATOR, StaticCompanion.savedFilename)

        // Writing the fallback file into an actual file in the app's internal storage.
        val out = FileOutputStream(privateFile)
        out.flush()
        out.write(inputAsByteArray)
        out.close()

        // Return the fallback JSONObject, and then navigate to the "gallery" node.
        return StaticJSONParser.parseData(inputAsString)
        // return JSONObject(inputAsString).getJSONArray("static")
    }

    @Suppress("unused")
    fun getFallbackStaticMetadata(): APIMetaData {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_static)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "gallery" node.
        return Meta.parseData(inputAsString)
    }

    /**
     * Initializes the gallery data and assign the global variable that handles it.
     */
    fun initFallbackStaticData() {
        StaticCompanion.api = getFallbackStaticData()
    }

    /**
     * Initializes the locally downloaded/stored static data.
     * Then assign the global variable that handles it.
     */
    fun initLocalStaticData() {
        StaticCompanion.api = getStaticData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's main data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getStaticData(): MutableList<StaticFolderObject> {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(StaticCompanion.absolutePathToJSONFile)

        // DEBUG: New feature.
        // TODO: Remove this block after v0.8.0 release.
        /*Logger.logTest({}, "Testing new features...")
        val x1 = StaticJSONParser.parseData(_parsedJSONString)
        x1.forEachIndexed { idx, it ->
            it.content.forEach { it2 ->
                Logger.logRapidTest({}, "${it.title}: ${it2.title}", LoggerType.DEBUG)
            }
        }
        Meta.parseData(_parsedJSONString)*/

        return StaticJSONParser.parseData(_parsedJSONString)
    }

    fun getStaticMetadata(): APIMetaData {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(StaticCompanion.absolutePathToJSONFile)
        return Meta.parseData(_parsedJSONString)
    }

}

class StaticCompanion : Application() {
    companion object {
        const val REMOTE_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data-json/main/v2/data/gkisplus-static.min.json"

        /* Back-end mechanisms. */
        var absolutePathToJSONFile: String = String()
        val mutableIsDataInitialized = mutableStateOf(false)
        const val savedFilename = "gkisplus-data-static.json"

        /* The JSON object that will be accessed by screens. */
        // var jsonRoot: JSONArray? = null
        var api: MutableList<StaticFolderObject>? = null
    }
}

class StaticJSONParser {
    companion object {
        private fun getEmptyAPIData(): MutableList<StaticFolderObject> {
            return mutableListOf()
        }

        fun parseData(jsonString: String): MutableList<StaticFolderObject> {
            try {
                // First, we read the JSON object.
                val obj = JSONObject(jsonString)

                // Then we initialized the API object.
                val api = getEmptyAPIData()

                /* From this point on, we then populate the API data. */

                obj.getJSONArray("static").let { it0 ->
                    for (i in 0 until it0.length()) {
                        (it0[i] as JSONObject).let { it1 ->
                            api.add(
                                StaticFolderObject(
                                    banner = it1.getString("banner"),
                                    title = it1.getString("title"),
                                    content = mutableListOf<StaticContentItemObject>().let { it2 ->
                                        it1.getJSONArray("content").let { it3 ->
                                            for (j in 0 until it3.length()) {
                                                val curNode = it3[j] as JSONObject
                                                it2.add(
                                                    StaticContentItemObject(
                                                        featuredImage = curNode.getString("featured-image"),
                                                        html = curNode.getString("html"),
                                                        subtitle = curNode.getString("subtitle"),
                                                        title = curNode.getString("title"),
                                                    )
                                                )
                                            }
                                        }
                                        it2
                                    }
                                )
                            )
                        }
                    }
                }
                return api

            } catch (e: Exception) {
                e.printStackTrace()
                Logger.logTest({}, "Detected anomalies when parsing the JSON data: ${e.message}", LoggerType.ERROR)
                return getEmptyAPIData()
            }
        }
    }
}