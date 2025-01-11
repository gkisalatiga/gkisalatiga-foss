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
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class Modules(private val ctx: Context) {

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
    fun getFallbackModulesData(): JSONObject {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_modules)
        val inputAsString: String = input.bufferedReader().use { it.readText() }
        val secondInput: InputStream = ctx.resources.openRawResource(R.raw.fallback_modules)
        val inputAsByteArray = secondInput.use { it.readBytes() }

        // Write the raw-resource-shipped file buffer as an actual file.
        // Creating the private file.
        val privateFile = File(InternalFileManager(ctx).DATA_DIR_FILE_CREATOR, ModulesCompanion.savedFilename)

        // Writing the fallback file into an actual file in the app's internal storage.
        val out = FileOutputStream(privateFile)
        out.flush()
        out.write(inputAsByteArray)
        out.close()

        // Return the fallback JSONObject, and then navigate to the "modules" node.
        return JSONObject(inputAsString).getJSONObject("modules")
    }

    @Suppress("unused")
    fun getFallbackModulesMetadata(): JSONObject {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_modules)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "modules" node.
        return JSONObject(inputAsString).getJSONObject("meta")
    }

    /**
     * Initializes the modules data and assign the global variable that handles it.
     */
    fun initFallbackModulesData() {
        ModulesCompanion.jsonRoot = getFallbackModulesData()
    }

    /**
     * Initializes the locally downloaded/stored modules data.
     * Then assign the global variable that handles it.
     */
    fun initLocalModulesData() {
        ModulesCompanion.jsonRoot = getModulesData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's modules data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getModulesData(): JSONObject {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(ModulesCompanion.absolutePathToJSONFile)
        return JSONObject(_parsedJSONString).getJSONObject("modules")
    }

    fun getModulesMetadata(): JSONObject {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(ModulesCompanion.absolutePathToJSONFile)
        return JSONObject(_parsedJSONString).getJSONObject("meta")
    }
}

class ModulesCompanion : Application() {
    companion object {
        const val REMOTE_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data-json/main/v2/data/gkisplus-modules.min.json"

        /* Back-end mechanisms. */
        var absolutePathToJSONFile: String = String()
        val mutableIsDataInitialized = mutableStateOf(false)
        val savedFilename = "gkisplus-modules-v2.json"

        /* The JSON object that will be accessed by screens. */
        var jsonRoot: JSONObject? = null
    }
}