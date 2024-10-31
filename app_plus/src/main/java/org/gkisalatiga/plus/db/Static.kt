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
        val inputAsString = FileInputStream(file).bufferedReader().use { it.readText() }

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
    fun getFallbackStaticData(): JSONArray {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_static)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "gallery" node.
        return JSONObject(inputAsString).getJSONArray("static")
    }

    fun getFallbackStaticMetadata(): JSONObject {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_static)
        val inputAsString: String = input.bufferedReader().use { it.readText() }
        val inputAsByteArray = input.readBytes()

        // Write the raw-resource-shipped file buffer as an actual file.
        // Creating the private file.
        val privateFile = File(InternalFileManager(ctx).DOWNLOAD_FILE_CREATOR, StaticCompanion.savedFilename)

        // Writing the fallback file into an actual file in the app's internal storage.
        val out = FileOutputStream(privateFile)
        out.flush()
        out.write(inputAsByteArray)
        out.close()

        // Return the fallback JSONObject, and then navigate to the "gallery" node.
        return JSONObject(inputAsString).getJSONObject("meta")
    }

    /**
     * Initializes the gallery data and assign the global variable that handles it.
     */
    fun initFallbackStaticData() {
        StaticCompanion.jsonRoot = getFallbackStaticData()
    }

    /**
     * Initializes the locally downloaded/stored static data.
     * Then assign the global variable that handles it.
     */
    fun initLocalStaticData() {
        StaticCompanion.jsonRoot = getStaticData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's main data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getStaticData(): JSONArray {
        // Determines if we have already downloaded the JSON file.
        val JSONExists = File(StaticCompanion.absolutePathToJSONFile).exists()

        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        if (StaticCompanion.mutableIsDataInitialized.value || JSONExists) {
            this.loadJSON(StaticCompanion.absolutePathToJSONFile)
            return JSONObject(_parsedJSONString).getJSONArray("static")
        } else {
            return getFallbackStaticData()
        }

    }

    fun getStaticMetadata(): JSONObject {
        // Determines if we have already downloaded the JSON file.
        val JSONExists = File(StaticCompanion.absolutePathToJSONFile).exists()

        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        if (StaticCompanion.mutableIsDataInitialized.value || JSONExists) {
            this.loadJSON(StaticCompanion.absolutePathToJSONFile)
            return JSONObject(_parsedJSONString).getJSONObject("meta")
        } else {
            return getFallbackStaticMetadata()
        }

    }

}

class StaticCompanion : Application() {
    companion object {
        const val REMOTE_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/v2/data/gkisplus-static.min.json"

        /* Back-end mechanisms. */
        var absolutePathToJSONFile: String = String()
        val mutableIsDataInitialized = mutableStateOf(false)
        val savedFilename = "gkisplus-static-v2.json"

        /* The JSON object that will be accessed by screens. */
        var jsonRoot: JSONArray? = null
    }
}