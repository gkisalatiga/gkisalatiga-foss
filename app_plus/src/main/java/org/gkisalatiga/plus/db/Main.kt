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
import org.gkisalatiga.plus.data.APIMetaData
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
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class Main(private val ctx: Context) {

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
    @Suppress("unused")
    fun getRawDumped(): String {
        return this._parsedJSONString
    }

    /**
     * Returns the attribution JSON file of this applications,
     * which open source works are used in this app.
     */
    fun getAttributions(): JSONObject {
        // Loading the local JSON file.
        // SOURCE: https://stackoverflow.com/a/2856501
        // SOURCE: https://stackoverflow.com/a/39500046
        val input: InputStream = ctx.resources.openRawResource(R.raw.app_attributions_open_source)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "data" node.
        return JSONObject(inputAsString)
    }

    /**
     * Returns the fallback JSONObject stored and packaged within the app.
     * This is useful especially when the app has not yet loaded the refreshed JSON metadata
     * from the internet yet.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getFallbackMainData(): APIMainData {
        // Loading the local JSON file.
        // SOURCE: https://stackoverflow.com/a/2856501
        // SOURCE: https://stackoverflow.com/a/39500046
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_main)
        val inputAsString: String = input.bufferedReader().use { it.readText() }
        val secondInput: InputStream = ctx.resources.openRawResource(R.raw.fallback_main)
        val inputAsByteArray = secondInput.use { it.readBytes() }

        // Write the raw-resource-shipped file buffer as an actual file.
        // Creating the private file.
        val privateFile = File(InternalFileManager(ctx).DATA_DIR_FILE_CREATOR, MainCompanion.savedFilename)

        // Writing the fallback file into an actual file in the app's internal storage.
        val out = FileOutputStream(privateFile)
        out.flush()
        out.write(inputAsByteArray)
        out.close()

        // Return the fallback JSONObject, and then navigate to the "data" node.
        return MainJSONParser.parseData(inputAsString)
    }

    @Suppress("unused")
    fun getFallbackMainMetadata(): APIMetaData {
        // Loading the local JSON file.
        // SOURCE: https://stackoverflow.com/a/2856501
        // SOURCE: https://stackoverflow.com/a/39500046
        val input: InputStream = ctx.resources.openRawResource(R.raw.fallback_main)
        val inputAsString: String = input.bufferedReader().use { it.readText() }

        // Return the fallback JSONObject, and then navigate to the "data" node.
        return Meta.parseData(inputAsString)
    }

    /**
     * Initializes the main data and assign the global variable that handles it.
     */
    fun initFallbackMainData() {
        MainCompanion.api = getFallbackMainData()
    }

    /**
     * Initializes the locally downloaded/stored main data.
     * Then assign the global variable that handles it.
     */
    fun initLocalMainData() {
        MainCompanion.api = getMainData()
    }

    /**
     * Parse the specified JSON string and serialize it, then
     * return a JSON object that reads the database's main data.
     * SOURCE: https://stackoverflow.com/a/50468095
     * ---
     * Assumes the JSON metadata has been initialized by the Downloader class.
     * Please run Downloader().initMetaData() before executing this function.
     */
    fun getMainData(): APIMainData {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(MainCompanion.absolutePathToJSONFile)
        return MainJSONParser.parseData(this._parsedJSONString)
    }

    fun getMainMetadata(): APIMetaData {
        // Load the downloaded JSON.
        // Prevents error-returning when this function is called upon offline.
        this.loadJSON(MainCompanion.absolutePathToJSONFile)
        return Meta.parseData(this._parsedJSONString)
    }

}

class MainCompanion : Application() {
    companion object {
        const val REMOTE_JSON_SOURCE = "https://raw.githubusercontent.com/gkisalatiga/gkisplus-data-json/main/v2/data/gkisplus-main.min.json"

        /* Back-end mechanisms. */
        var absolutePathToJSONFile: String = String()
        val mutableIsDataInitialized = mutableStateOf(false)
        const val savedFilename = "gkisplus-data-main.json"

        /* The JSON object that will be accessed by screens. */
        // var jsonRoot: JSONObject? = null
        var api: APIMainData? = null
    }
}

class MainJSONParser {
    companion object {
        private fun getEmptyAPIData(): APIMainData {
            return APIMainData(
                agenda = MainAgendaRootObject(
                    sun = mutableListOf(),
                    mon = mutableListOf(),
                    tue = mutableListOf(),
                    wed = mutableListOf(),
                    thu = mutableListOf(),
                    fri = mutableListOf(),
                    sat = mutableListOf(),
                ),
                agendaRuangan = mutableListOf(),
                backend = MainBackendRootObject(
                    flags = MainBackendFlagsItemObject(
                        isEasterEggDevmodeEnabled = 0,
                        isFeatureAgendaShown = 0,
                        isFeatureBibleShown = 0,
                        isFeatureFormulirShown = 0,
                        isFeatureGaleriShown = 0,
                        isFeatureLapakShown = 0,
                        isFeatureLibraryShown = 0,
                        isFeaturePersembahanShown = 0,
                        isFeatureSeasonalShown = 0,
                        isFeatureYKBShown = 0,
                    ),
                    strings = MainBackendStringsItemObject(
                        address = "",
                        aboutChangelogUrl = "",
                        aboutContactMail = "",
                        aboutGooglePlayListingUrl = "",
                        aboutSourceCodeUrl = "",
                        greetingsBottom = "",
                        greetingsTop = "",
                    )
                ),
                carousel = mutableListOf(),
                forms = mutableListOf(),
                offertory = mutableListOf(),
                offertoryCode = mutableListOf(),
                pdf = MainPdfRootObject(
                    wj = mutableListOf(),
                    liturgi = mutableListOf(),
                ),
                pukatBerkat = mutableListOf(),
                urlProfile = MainUrlProfileObject(
                    fb = "",
                    email = "",
                    insta = "",
                    linktree = "",
                    maps = "",
                    web = "",
                    whatsapp = "",
                    youtube = "",
                ),
                ykb = mutableListOf(),
                yt = mutableListOf(),
            )
        }

        fun parseData(jsonString: String): APIMainData {
            try {
                // First, we read the JSON object.
                val obj = JSONObject(jsonString).getJSONObject("data")

                // Then we initialized the API object.
                val api = getEmptyAPIData()

                /* From this point on, we then populate the API data. */

                mapOf(
                    "sun" to api.agenda.sun,
                    "mon" to api.agenda.mon,
                    "tue" to api.agenda.tue,
                    "wed" to api.agenda.wed,
                    "thu" to api.agenda.thu,
                    "fri" to api.agenda.fri,
                    "sat" to api.agenda.sat,
                ).forEach { it0 ->
                    obj.getJSONObject("agenda").let { it1 ->
                        it1.getJSONArray(it0.key).let { it2 ->
                            for (i in 0 until it2.length()) {
                                val curNode = it2[i] as JSONObject
                                it0.value.add(
                                    MainAgendaItemObject(
                                        name = curNode.getString("name"),
                                        time = curNode.getString("time"),
                                        timeTo = curNode.getString("time-to"),
                                        timezone = curNode.getString("timezone"),
                                        type = curNode.getString("type"),
                                        place = curNode.getString("place"),
                                        representative = curNode.getString("representative"),
                                        note = curNode.getString("note"),
                                    )
                                )
                            }
                        }
                    }
                }

                obj.getJSONArray("agenda-ruangan").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.agendaRuangan.add(
                            MainAgendaRuanganItemObject(
                                name = curNode.getString("name"),
                                time = curNode.getString("time"),
                                timeTo = curNode.getString("time-to"),
                                timezone = curNode.getString("timezone"),
                                date = curNode.getString("date"),
                                weekday = curNode.getString("weekday"),
                                type = curNode.getString("type"),
                                place = curNode.getString("place"),
                                representative = curNode.getString("representative"),
                                pic = curNode.getString("pic"),
                                status = curNode.getString("status"),
                                note = curNode.getString("note"),
                            )
                        )
                    }
                }

                obj.getJSONObject("backend").let { it0 ->
                    it0.getJSONObject("flags").let { it1 ->
                        api.backend.flags.isEasterEggDevmodeEnabled = it1.getInt("is_easter_egg_devmode_enabled")
                        api.backend.flags.isFeatureAgendaShown = it1.getInt("is_feature_agenda_shown")
                        api.backend.flags.isFeaturePersembahanShown = it1.getInt("is_feature_persembahan_shown")
                        api.backend.flags.isFeatureYKBShown = it1.getInt("is_feature_ykb_shown")
                        api.backend.flags.isFeatureFormulirShown = it1.getInt("is_feature_formulir_shown")
                        api.backend.flags.isFeatureGaleriShown = it1.getInt("is_feature_galeri_shown")
                        api.backend.flags.isFeatureBibleShown = it1.getInt("is_feature_bible_shown")
                        api.backend.flags.isFeatureLibraryShown = it1.getInt("is_feature_library_shown")
                        api.backend.flags.isFeatureLapakShown = it1.getInt("is_feature_lapak_shown")
                        api.backend.flags.isFeatureSeasonalShown = it1.getInt("is_feature_seasonal_shown")
                    }
                    it0.getJSONObject("strings").let { it1 ->
                        api.backend.strings.address = it1.getString("address")
                        api.backend.strings.aboutContactMail = it1.getString("about_contact_mail")
                        api.backend.strings.aboutChangelogUrl = it1.getString("about_changelog_url")
                        api.backend.strings.aboutGooglePlayListingUrl = it1.getString("about_google_play_listing_url")
                        api.backend.strings.aboutSourceCodeUrl = it1.getString("about_source_code_url")
                        api.backend.strings.greetingsTop = it1.getString("greetings_top")
                        api.backend.strings.greetingsBottom = it1.getString("greetings_bottom")
                    }
                }

                obj.getJSONArray("carousel").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.carousel.add(
                            MainCarouselItemObject(
                                banner = curNode.getString("banner"),
                                dateCreated = curNode.getString("date-created"),
                                posterCaption = curNode.getString("poster-caption"),
                                posterImage = curNode.getString("poster-image"),
                                title = curNode.getString("title"),
                                type = curNode.getString("type"),
                            )
                        )
                    }
                }

                obj.getJSONArray("forms").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.forms.add(
                            MainFormsItemObject(
                                title = curNode.getString("title"),
                                url = curNode.getString("url"),
                            )
                        )
                    }
                }

                obj.getJSONArray("offertory").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.offertory.add(
                            MainOffertoryObject(
                                bankAbbr = curNode.getString("bank-abbr"),
                                bankName = curNode.getString("bank-name"),
                                bankNumber = curNode.getString("bank-number"),
                                bankLogoUrl = curNode.getString("bank-logo-url"),
                                accountHolder = curNode.getString("account-holder"),
                            )
                        )
                    }
                }

                obj.getJSONArray("offertory-code").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.offertoryCode.add(
                            MainOffertoryCodeObject(
                                uniqueCode = curNode.getString("unique-code"),
                                title = curNode.getString("title"),
                                desc = curNode.getString("desc"),
                            )
                        )
                    }
                }

                obj.getJSONObject("pdf").let { it0 ->
                    mapOf(
                        "wj" to api.pdf.wj,
                        "liturgi" to api.pdf.liturgi,
                    ).forEach { it1 ->
                        it0.getJSONArray(it1.key).let { it2 ->
                            for (i in 0 until it2.length()) {
                                val curNode = it2[i] as JSONObject
                                it1.value.add(
                                    MainPdfItemObject(
                                        title = curNode.getString("title"),
                                        date = curNode.getString("date"),
                                        link = curNode.getString("link"),
                                        postPage = curNode.getString("post-page"),
                                        thumbnail = curNode.getString("thumbnail"),
                                        id = curNode.getString("id"),
                                        size = curNode.getString("size"),
                                    )
                                )
                            }
                        }
                    }
                }

                obj.getJSONArray("pukat-berkat").let { it0 ->
                    for (i in 0 until it0.length()) {
                        val curNode = it0[i] as JSONObject
                        api.pukatBerkat.add(
                            MainPukatBerkatItemObject(
                                title = curNode.getString("title"),
                                desc = curNode.getString("desc"),
                                price = curNode.getString("price"),
                                contact = curNode.getString("contact"),
                                vendor = curNode.getString("vendor"),
                                type = curNode.getString("type"),
                                image = curNode.getString("image"),
                            )
                        )
                    }
                }

                obj.getJSONObject("url-profile").let { it0 ->
                    api.urlProfile.fb = it0.getString("fb")
                    api.urlProfile.insta = it0.getString("insta")
                    api.urlProfile.youtube = it0.getString("youtube")
                    api.urlProfile.web = it0.getString("web")
                    api.urlProfile.linktree = it0.getString("linktree")
                    api.urlProfile.whatsapp = it0.getString("whatsapp")
                    api.urlProfile.email = it0.getString("email")
                    api.urlProfile.maps = it0.getString("maps")
                }

                obj.getJSONArray("ykb").let { it0 ->
                    for (i in 0 until it0.length()) {
                        (it0[i] as JSONObject).let { it1 ->
                            api.ykb.add(
                                MainYKBListObject(
                                    title = it1.getString("title"),
                                    url = it1.getString("url"),
                                    banner = it1.getString("banner"),
                                    posts = mutableListOf<MainYKBItemObject>().let { it2 ->
                                        it1.getJSONArray("posts").let { it3 ->
                                            for (j in 0 until it3.length()) {
                                                val curNode = it3[j] as JSONObject
                                                it2.add(
                                                    MainYKBItemObject(
                                                        title = curNode.getString("title"),
                                                        shortlink = curNode.getString("shortlink"),
                                                        date = curNode.getString("date"),
                                                        featuredImage = curNode.getString("featured-image"),
                                                        html = curNode.getString("html"),
                                                        scripture = mutableListOf<String>().let { it4 ->
                                                            curNode.getJSONArray("scripture").let { it5 ->
                                                                for (k in 0 until it5.length()) {
                                                                    it4.add(it5.getString(k))
                                                                }
                                                            }
                                                            it4
                                                        }
                                                    )
                                                )
                                            }
                                        }
                                        it2
                                    },
                                )
                            )
                        }
                    }
                }

                obj.getJSONArray("yt").let { it0 ->
                    for (i in 0 until it0.length()) {
                        (it0[i] as JSONObject).let { it1 ->
                            api.yt.add(
                                MainYouTubePlaylistObject(
                                    title = it1.getString("title"),
                                    lastUpdate = it1.getString("last-update"),
                                    type = it1.getString("type"),
                                    pinned = it1.getInt("pinned"),
                                    rssTitleKeyword = try {
                                        it1.getString("rss-title-keyword")
                                    } catch (e: Exception) {
                                        null
                                    },
                                    playlistId = try {
                                        it1.getString("playlist-id")
                                    } catch (e: Exception) {
                                        null
                                    },
                                    content = mutableListOf<MainYouTubeVideoContentObject>().let { it2 ->
                                        it1.getJSONArray("content").let { it3 ->
                                            for (j in 0 until it3.length()) {
                                                val curNode = it3[j] as JSONObject
                                                it2.add(
                                                    MainYouTubeVideoContentObject(
                                                        title = curNode.getString("title"),
                                                        desc = curNode.getString("desc"),
                                                        date = curNode.getString("date"),
                                                        link = curNode.getString("link"),
                                                        thumbnail = curNode.getString("thumbnail"),
                                                    )
                                                )
                                            }
                                        }
                                        it2
                                    },
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