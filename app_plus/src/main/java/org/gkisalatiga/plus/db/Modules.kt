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
import org.gkisalatiga.plus.data.APIModulesData
import org.gkisalatiga.plus.data.ModulesAttributionsItemObject
import org.gkisalatiga.plus.data.ModulesAttributionsRootObject
import org.gkisalatiga.plus.data.ModulesBibleItemObject
import org.gkisalatiga.plus.data.ModulesLibraryItemObject
import org.gkisalatiga.plus.data.ModulesSeasonalObject
import org.gkisalatiga.plus.data.ModulesSeasonalStaticItemObject
import org.gkisalatiga.plus.data.ModulesSeasonalStaticObject
import org.gkisalatiga.plus.data.ModulesSeasonalTwibbonItemObject
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
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
    fun getFallbackModulesData(): APIModulesData {
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
        // return JSONObject(inputAsString).getJSONObject("modules")
        return ModulesJSONParser.parseData(inputAsString)
    }

    @Suppress("unused")
    fun getFallbackModulesMetadata(): APIMetaData {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_modules)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "modules" node.
        // return JSONObject(inputAsString).getJSONObject("meta")
        return Meta.parseData(inputAsString)
    }

    /**
     * Initializes the modules data and assign the global variable that handles it.
     */
    fun initFallbackModulesData() {
        ModulesCompanion.api = getFallbackModulesData()
    }

    /**
     * Initializes the locally downloaded/stored modules data.
     * Then assign the global variable that handles it.
     */
    fun initLocalModulesData() {
        ModulesCompanion.api = getModulesData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's modules data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getModulesData(): APIModulesData {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(ModulesCompanion.absolutePathToJSONFile)

        // DEBUG: New feature.
        // TODO: Remove this block after v0.8.0 release.
        /*Logger.logTest({}, "Testing new features...")
        val x1 = ModulesJSONParser.parseData(_parsedJSONString)
        x1.seasonal.staticMenu.twibbon.twibs?.forEach {
            Logger.logTest({}, "Twibbons: ${it.title}")
        }
        x1.bible.forEach {
            Logger.logTest({}, "Bible: ${it.sourceJson}")
        }
        x1.attributions.webview.forEach {
            Logger.logTest({}, "Attrib: ${it.title}")
        }
        Meta.parseData(_parsedJSONString)*/

        return ModulesJSONParser.parseData(_parsedJSONString)
    }

    fun getModulesMetadata(): APIMetaData {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(ModulesCompanion.absolutePathToJSONFile)
        // return JSONObject(_parsedJSONString).getJSONObject("meta")
        return Meta.parseData(_parsedJSONString)
    }
}

class ModulesCompanion : Application() {
    companion object {
        const val REMOTE_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data-json/main/v2/data/gkisplus-modules.min.json"

        /* Back-end mechanisms. */
        var absolutePathToJSONFile: String = String()
        val mutableIsDataInitialized = mutableStateOf(false)
        val savedFilename = "gkisplus-data-modules.json"

        /* The JSON object that will be accessed by screens. */
        // var jsonRoot: JSONObject? = null
        var api: APIModulesData? = null
    }
}

class ModulesJSONParser {
    companion object {
        private fun getEmptyAPIData(): APIModulesData {
            return APIModulesData(
                attributions = ModulesAttributionsRootObject(
                    webview = mutableListOf(),
                    books = mutableListOf(),
                ),
                bible = mutableListOf(),
                library = mutableListOf(),
                seasonal = ModulesSeasonalObject(
                    title = "",
                    bannerFront = "",
                    bannerInside = "",
                    staticMenu = ModulesSeasonalStaticObject(
                        agenda = ModulesSeasonalStaticItemObject(
                            title = "",
                            albumKeyword = null,
                            ytPlaylist = null,
                            banner = "",
                            isShown = 0,
                            selectionTag = null,
                            twibs = mutableListOf(),
                            url = null,
                        ),
                        books = ModulesSeasonalStaticItemObject(
                            title = "",
                            albumKeyword = null,
                            ytPlaylist = null,
                            banner = "",
                            isShown = 0,
                            selectionTag = null,
                            twibs = mutableListOf(),
                            url = null,
                        ),
                        gallery = ModulesSeasonalStaticItemObject(
                            title = "",
                            albumKeyword = null,
                            ytPlaylist = null,
                            banner = "",
                            isShown = 0,
                            selectionTag = null,
                            twibs = mutableListOf(),
                            url = null,
                        ),
                        playlist = ModulesSeasonalStaticItemObject(
                            title = "",
                            albumKeyword = null,
                            ytPlaylist = null,
                            banner = "",
                            isShown = 0,
                            selectionTag = null,
                            twibs = mutableListOf(),
                            url = null,
                        ),
                        twibbon = ModulesSeasonalStaticItemObject(
                            title = "",
                            albumKeyword = null,
                            ytPlaylist = null,
                            banner = "",
                            isShown = 0,
                            selectionTag = null,
                            twibs = mutableListOf(),
                            url = null,
                        )
                    )
                )
            )
        }

        fun parseData(jsonString: String): APIModulesData {
            try {
                // First, we read the JSON object.
                val obj = JSONObject(jsonString).getJSONObject("modules")

                // Then we initialized the API object.
                val api = getEmptyAPIData()

                /* From this point on, we then populate the API data. */

                obj.getJSONArray("bible").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.bible.add(
                            ModulesBibleItemObject(
                                name = curNode.getString("name"),
                                abbr = curNode.getString("abbr"),
                                lang = curNode.getString("lang"),
                                source = curNode.getString("source"),
                                sourceJson = curNode.getString("source-json"),
                                sourceSize = curNode.getString("source-size"),
                                filename = curNode.getString("filename"),
                                license = curNode.getString("license"),
                                licenseUrl = curNode.getString("license-url"),
                                author = curNode.getString("author"),
                                authorUrl = curNode.getString("author-url"),
                                description = curNode.getString("description"),
                            )
                        )
                    }
                }

                obj.getJSONArray("library").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.library.add(
                            ModulesLibraryItemObject(
                                title = curNode.getString("title"),
                                author = curNode.getString("author"),
                                publisher = curNode.getString("publisher"),
                                publisherLoc = curNode.getString("publisher-loc"),
                                year = curNode.getString("year"),
                                thumbnail = curNode.getString("thumbnail"),
                                downloadUrl = curNode.getString("download-url"),
                                source = curNode.getString("source"),
                                size = curNode.getString("size"),
                                tags = mutableListOf<String>().let { it1 ->
                                    curNode.getJSONArray("tags").let { it2 ->
                                        for (j in 0 until it2.length()) {
                                            it1.add(it2.getString(j))
                                        }
                                    }
                                    it1
                                },
                                isShown = curNode.getInt("is_shown"),
                            )
                        )
                    }
                }

                obj.getJSONObject("attributions").let { it0 ->
                    mapOf(
                        "webview" to api.attributions.webview,
                        "books" to api.attributions.books,
                    ).forEach { it1 ->
                        it0.getJSONArray(it1.key).let { it2 ->
                            for (i in 0 until it2.length()) {
                                val curNode = it2[i] as JSONObject
                                it1.value.add(
                                    ModulesAttributionsItemObject(
                                        title = curNode.getString("title"),
                                        license = curNode.getString("license"),
                                        licenseUrl = curNode.getString("license-url"),
                                        year = curNode.getString("year"),
                                        author = curNode.getString("author"),
                                        link = curNode.getString("link"),
                                    )
                                )
                            }
                        }
                    }
                }

                obj.getJSONObject("seasonal").let { it0 ->
                    api.seasonal.title = it0.getString("title")
                    api.seasonal.bannerFront = it0.getString("banner-front")
                    api.seasonal.bannerInside = it0.getString("banner-inside")
                    mapOf(
                        "agenda" to api.seasonal.staticMenu.agenda,
                        "books" to api.seasonal.staticMenu.books,
                        "gallery" to api.seasonal.staticMenu.gallery,
                        "playlist" to api.seasonal.staticMenu.playlist,
                        "twibbon" to api.seasonal.staticMenu.twibbon,
                    ).forEach { it1 ->
                        it1.value.let { it2 ->
                            val curNode = it0.getJSONObject("static-menu").getJSONObject(it1.key)
                            it2.banner = curNode.getString("banner")
                            it2.isShown = curNode.getInt("is_shown")
                            it2.title = curNode.getString("title")
                            it2.albumKeyword = try {
                                curNode.getString("album-keyword")
                            } catch (e: Exception) {
                                null
                            }
                            it2.ytPlaylist = try {
                                curNode.getString("yt-playlist")
                            } catch (e: Exception) {
                                null
                            }
                            it2.selectionTag = try {
                                curNode.getString("selection-tag")
                            } catch (e: Exception) {
                                null
                            }
                            it2.url = try {
                                curNode.getString("url")
                            } catch (e: Exception) {
                                null
                            }
                            it2.twibs = try {
                                mutableListOf<ModulesSeasonalTwibbonItemObject>().let { it3 ->
                                    curNode.getJSONArray("twibs").let { it4 ->
                                        for (i in 0 until it4.length()) {
                                            @Suppress("SpellCheckingInspection")
                                            val curTwibNode = it4[i] as JSONObject
                                            it3.add(
                                                ModulesSeasonalTwibbonItemObject(
                                                    title = curTwibNode.getString("title"),
                                                    url = curTwibNode.getString("url"),
                                                    postPage = curTwibNode.getString("post-page"),
                                                )
                                            )
                                        }
                                    }
                                    it3
                                }
                            } catch (e: Exception) {
                                null
                            }
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