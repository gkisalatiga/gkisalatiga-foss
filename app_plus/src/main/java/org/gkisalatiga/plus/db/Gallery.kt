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
import org.gkisalatiga.plus.data.APIMainData
import org.gkisalatiga.plus.data.GalleryAlbumObject
import org.gkisalatiga.plus.data.GalleryItemObject
import org.gkisalatiga.plus.data.GalleryYearObject
import org.gkisalatiga.plus.data.MainAgendaItemObject
import org.gkisalatiga.plus.data.MainAgendaRootObject
import org.gkisalatiga.plus.data.MainAgendaRuanganItemObject
import org.gkisalatiga.plus.data.MainBackendFlagsItemObject
import org.gkisalatiga.plus.data.MainBackendRootObject
import org.gkisalatiga.plus.data.MainBackendStringsItemObject
import org.gkisalatiga.plus.data.MainCarouselItemObject
import org.gkisalatiga.plus.data.MainFormsItemObject
import org.gkisalatiga.plus.data.MainOffertoryCodeObject
import org.gkisalatiga.plus.data.MainOffertoryObject
import org.gkisalatiga.plus.data.MainPdfItemObject
import org.gkisalatiga.plus.data.MainPdfRootObject
import org.gkisalatiga.plus.data.MainPukatBerkatItemObject
import org.gkisalatiga.plus.data.MainUrlProfileObject
import org.gkisalatiga.plus.data.MainYKBItemObject
import org.gkisalatiga.plus.data.MainYKBListObject
import org.gkisalatiga.plus.data.MainYouTubePlaylistObject
import org.gkisalatiga.plus.data.MainYouTubeVideoContentObject
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.gkisalatiga.plus.services.InternalFileManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
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
    @Suppress("MemberVisibilityCanBePrivate")
    fun getFallbackGalleryData(): JSONArray {
        // Loading the local JSON file.
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_gallery)
        val inputAsString: String = input.bufferedReader().use { it.readText() }
        val secondInput: InputStream = ctx.resources.openRawResource(R.raw.fallback_gallery)
        val inputAsByteArray = secondInput.use { it.readBytes() }

        // Write the raw-resource-shipped file buffer as an actual file.
        // Creating the private file.
        val privateFile = File(InternalFileManager(ctx).DATA_DIR_FILE_CREATOR, GalleryCompanion.savedFilename)

        // Writing the fallback file into an actual file in the app's internal storage.
        val out = FileOutputStream(privateFile)
        out.flush()
        out.write(inputAsByteArray)
        out.close()

        // Return the fallback JSONObject, and then navigate to the "gallery" node.
        return JSONObject(inputAsString).getJSONArray("gallery")
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
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
     * Initializes the locally downloaded/stored gallery data.
     * Then assign the global variable that handles it.
     */
    fun initLocalGalleryData() {
        GalleryCompanion.jsonRoot = getGalleryData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's main data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getGalleryData(): JSONArray {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(GalleryCompanion.absolutePathToJSONFile)

        // DEBUG: New feature.
        // TODO: Remove this block after v0.8.0 release.
        Logger.logTest({}, "Testing new features...")
        val x1 = GalleryJSONParser.parseData(_parsedJSONString)
        x1.forEachIndexed { idx, it ->
            it.albumData.forEach { it2 ->
                it2.photos.forEach { it3 ->
                    // Logger.logRapidTest({}, "${idx}: ${it.title}, ${it2.folderId}, ${it3.id}", LoggerType.DEBUG)
                }
            }
        }
        Meta.parseData(_parsedJSONString)

        return JSONObject(_parsedJSONString).getJSONArray("gallery")
    }

    fun getGalleryMetadata(): JSONObject {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(GalleryCompanion.absolutePathToJSONFile)
        return JSONObject(_parsedJSONString).getJSONObject("meta")
    }
}

@Suppress("MayBeConstant", "RedundantSuppression", "SpellCheckingInspection")
class GalleryCompanion : Application() {
    companion object {
        const val REMOTE_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data-json/main/v2/data/gkisplus-gallery.min.json"

        /* Back-end mechanisms. */
        var absolutePathToJSONFile: String = String()
        val mutableIsDataInitialized = mutableStateOf(false)
        val savedFilename = "gkisplus-data-gallery.json"

        /* The JSON object that will be accessed by screens. */
        var jsonRoot: JSONArray? = null
    }
}

class GalleryJSONParser {
    companion object {
        private fun getEmptyAPIData(): MutableList<GalleryYearObject> {
            return mutableListOf()
        }

        fun parseData(jsonString: String): MutableList<GalleryYearObject> {
            try {
                // First, we read the JSON object.
                val obj = JSONObject(jsonString)

                // Then we initialized the API object.
                val api = getEmptyAPIData()

                /* From this point on, we then populate the API data. */

                obj.getJSONArray("gallery").let { it0 ->
                    for (i in 0 until it0.length()) {
                        (it0[i] as JSONObject).let { it1 ->
                            api.add(
                                GalleryYearObject(
                                    title = it1.getString("title"),
                                    albumData = mutableListOf<GalleryAlbumObject>().let { it2 ->
                                        it1.getJSONArray("album-data").let { it3 ->
                                            for (j in 0 until it3.length()) {
                                                val curAlbum = it3[j] as JSONObject
                                                it2.add(
                                                    GalleryAlbumObject(
                                                        folderId = curAlbum.getString("folder_id"),
                                                        lastUpdate = curAlbum.getString("last_update"),
                                                        featuredPhotoId = curAlbum.getString("featured_photo_id"),
                                                        story = curAlbum.getString("story"),
                                                        title = curAlbum.getString("title"),
                                                        eventDate = curAlbum.getString("event_date"),
                                                        photos = mutableListOf<GalleryItemObject>().let { it4 ->
                                                            curAlbum.getJSONArray("photos").let { it5 ->
                                                                for (k in 0 until it5.length()) {
                                                                    val curItem = it5[k] as JSONObject
                                                                    it4.add(
                                                                        GalleryItemObject(
                                                                            id = curItem.getString("id"),
                                                                            name = curItem.getString("name"),
                                                                            date = curItem.getString("date"),
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                            it4
                                                        },
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