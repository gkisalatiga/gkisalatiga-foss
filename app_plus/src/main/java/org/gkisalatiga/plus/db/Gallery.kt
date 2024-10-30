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
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class Gallery(private val ctx: Context) {

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
    fun getFallbackGalleryData(): JSONObject {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_gallery)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "gallery" node.
        return JSONObject(inputAsString).getJSONObject("gallery")
    }

    fun getFallbackGalleryMetadata(): JSONObject {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_gallery)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "gallery" node.
        return JSONObject(inputAsString).getJSONObject("meta")
    }

    /**
     * Initializes the gallery data and assign the global variable that handles it.
     */
    fun initFallbackGalleryData() {
        GalleryCompanion.jsonRoot = getFallbackGalleryData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's main data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getGalleryData(): JSONObject {
        // Determines if we have already downloaded the JSON file.
        val JSONExists = File(GalleryCompanion.absolutePathToJSONFile).exists()

        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        if (GalleryCompanion.mutableIsDataInitialized.value || JSONExists) {
            this.loadJSON(GalleryCompanion.absolutePathToJSONFile)
            return JSONObject(_parsedJSONString).getJSONObject("gallery")
        } else {
            return getFallbackGalleryData()
        }

    }

    fun getGalleryMetadata(): JSONObject {
        // Determines if we have already downloaded the JSON file.
        val JSONExists = File(GalleryCompanion.absolutePathToJSONFile).exists()

        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        if (GalleryCompanion.mutableIsDataInitialized.value || JSONExists) {
            this.loadJSON(GalleryCompanion.absolutePathToJSONFile)
            return JSONObject(_parsedJSONString).getJSONObject("meta")
        } else {
            return getFallbackGalleryMetadata()
        }

    }
}

// TODO: Update to gallery JSON data v2
class GalleryCompanion : Application() {
    companion object {
        const val REMOTE_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data/main/gkisplus-gallery.json"

        /* Back-end mechanisms. */
        var absolutePathToJSONFile: String = String()
        val mutableIsDataInitialized = mutableStateOf(false)
        val savedFilename = "gkisplus-gallery.json"

        /* The JSON object that will be accessed by screens. */
        var jsonRoot: JSONObject? = null
    }
}