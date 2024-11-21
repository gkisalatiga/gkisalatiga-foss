/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gkisalatiga.plus.data.SearchItemData
import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.lib.Logger
import org.gkisalatiga.plus.lib.LoggerType
import org.json.JSONObject

/**
 * This class does the searching of contents stored in the app's JSON data.
 */
class SearchViewModel(ctx: Context) : CoroutineViewModel() {

    /**
     * Begin searching a specific content in GKI Salatiga+ based on a given search term.
     * @param searchTerm the content search term to look up for.
     * @param searchFilter the type of content that we look for.
     * @return the [SearchUiEvent] resembling the current state of the content search.
     */
    fun querySearch(searchTerm: String, searchFilter: List<SearchDataType>, alsoSearchContent: Boolean = false) : MutableLiveData<SearchUiEvent> {
        val result = MutableLiveData<SearchUiEvent>()

        val sanitizedSearchTerm = searchTerm.trim().lowercase()

        // Mark this operation as "initiating".
        result.postValue(SearchUiEvent.SearchLoading("Initiating the query search with search terms: $searchTerm"))

        launch(Dispatchers.IO) {
            try {

                val root = MainCompanion.jsonRoot!!

                // This stores all of the resulting/matching search data.
                val allSearchResults = mutableListOf<SearchItemData>()

                /* Tata Ibadah. */
                if (SearchDataType.PDF_TATA_IBADAH in searchFilter) {
                    val parent = root.getJSONObject("pdf").getJSONArray("liturgi")

                    // Converting JSONArray to an iterable.
                    val iterableParent = mutableListOf<JSONObject>()
                    for (i in 0 until parent.length()) iterableParent.add(i, parent[i] as JSONObject)

                    iterableParent.forEach {
                        if (it != JSONObject() && it.getString("title").lowercase().contains(sanitizedSearchTerm))
                            SearchItemData(sanitizedSearchTerm, SearchDataType.PDF_TATA_IBADAH, it).let { obj -> allSearchResults.add(obj) }
                    }
                }

                /* Warta Jemaat. */
                if (SearchDataType.PDF_WARTA_JEMAAT in searchFilter) {
                    val parent = root.getJSONObject("pdf").getJSONArray("wj")

                    // Converting JSONArray to an iterable.
                    val iterableParent = mutableListOf<JSONObject>()
                    for (i in 0 until parent.length()) iterableParent.add(i, parent[i] as JSONObject)

                    iterableParent.forEach {
                        if (it != JSONObject() && it.getString("title").lowercase().contains(sanitizedSearchTerm))
                            SearchItemData(sanitizedSearchTerm, SearchDataType.PDF_WARTA_JEMAAT, it).let { obj -> allSearchResults.add(obj) }
                    }
                }

                // 404.
                if (allSearchResults.size == 0) result.postValue(SearchUiEvent.SearchNotFound("Your search string does not match with any content."))
                else result.postValue(SearchUiEvent.SearchFinished("Search successful!", allSearchResults.size, allSearchResults))

            } catch (e: Exception) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(SearchUiEvent.SearchError(msg))
                Logger.log({}, msg, LoggerType.ERROR)
            }
        }

        return result
    }
}

enum class SearchDataType {
    PDF_TATA_IBADAH,
    PDF_WARTA_JEMAAT,
    RENUNGAN_YKB,
    YOUTUBE_VIDEO,
}

sealed class SearchUiEvent {
    data class SearchLoading(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_RESULT_LOADING) : SearchUiEvent()
    data class SearchFinished(val message: String, val queriedSearchItemCount: Int, val searchResult: List<SearchItemData>, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_FINISHED) : SearchUiEvent()
    data class SearchNotFound(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_RESULT_NOT_FOUND) : SearchUiEvent()
    data class SearchError(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_ERROR) : SearchUiEvent()
}

enum class SearchUiEventIdentifier {
    EVENT_SEARCH_RESULT_LOADING,
    EVENT_SEARCH_FINISHED,
    EVENT_SEARCH_RESULT_NOT_FOUND,
    EVENT_SEARCH_ERROR,
}
