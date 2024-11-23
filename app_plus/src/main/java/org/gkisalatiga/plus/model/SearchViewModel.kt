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
import org.gkisalatiga.plus.lib.EmptySearchQueryException
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
    fun querySearch(searchTerm: String, searchFilter: List<SearchDataType>, searchSort: SearchDataSortType, alsoSearchContent: Boolean = false) : MutableLiveData<SearchUiEvent> {
        val result = MutableLiveData<SearchUiEvent>()

        val sanitizedSearchTerm = searchTerm.trim().lowercase()

        // Mark this operation as "initiating".
        result.postValue(SearchUiEvent.SearchLoading("Initiating the query search with search terms: $searchTerm"))

        launch(Dispatchers.IO) {
            try {

                val root = MainCompanion.jsonRoot!!

                if (searchTerm.isBlank()) throw EmptySearchQueryException()

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
                            SearchItemData(sanitizedSearchTerm, it.getString("title"), it.getString("date"), SearchDataType.PDF_TATA_IBADAH, it).let { obj -> allSearchResults.add(obj) }
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
                            SearchItemData(sanitizedSearchTerm, it.getString("title"), it.getString("date"), SearchDataType.PDF_WARTA_JEMAAT, it).let { obj -> allSearchResults.add(obj) }
                    }
                }

                /* Renungan YKB. */
                if (SearchDataType.RENUNGAN_YKB in searchFilter) {
                    val parent = root.getJSONArray("ykb")

                    // Converting JSONArray to an iterable.
                    val iterableParent = mutableListOf<JSONObject>()
                    for (i in 0 until parent.length()) iterableParent.add(i, parent[i] as JSONObject)

                    iterableParent.forEach {

                        // Opening the content of each YKB category.
                        val contentRoot = it.getJSONArray("posts")
                        val contentRootTag1 = it.getString("title")
                        val contentRootTag2 = it.getString("banner")
                        val iterableContentRoot = mutableListOf<JSONObject>().let { root -> for (i in 0 until contentRoot.length()) root.add(i, contentRoot[i] as JSONObject); root }

                        // Iterating through every YKB post and finding matches.
                        iterableContentRoot.forEach { node ->
                            if (node != JSONObject() && node.getString("title").lowercase().contains(sanitizedSearchTerm))
                                SearchItemData(sanitizedSearchTerm, node.getString("title"), node.getString("date"), SearchDataType.RENUNGAN_YKB, node, tag1 = contentRootTag1, tag2 = contentRootTag2).let { obj -> allSearchResults.add(obj) }
                        }
                    }
                }

                /* YouTube video. */
                if (SearchDataType.YOUTUBE_VIDEO in searchFilter) {
                    val parent = root.getJSONArray("yt")

                    // Converting JSONArray to an iterable.
                    val iterableParent = mutableListOf<JSONObject>()
                    for (i in 0 until parent.length()) iterableParent.add(i, parent[i] as JSONObject)

                    iterableParent.forEach {

                        // Opening the content of each YKB category.
                        val contentRoot = it.getJSONArray("content")
                        val contentRootTag = it.getString("title")
                        val iterableContentRoot = mutableListOf<JSONObject>().let { root -> for (i in 0 until contentRoot.length()) root.add(i, contentRoot[i] as JSONObject); root }

                        // Iterating through every YKB post and finding matches.
                        iterableContentRoot.forEach { node ->
                            if (node != JSONObject() && node.getString("title").lowercase().contains(sanitizedSearchTerm))
                                SearchItemData(sanitizedSearchTerm, node.getString("title"), node.getString("date"), SearchDataType.YOUTUBE_VIDEO, node, tag1 = contentRootTag).let { obj -> allSearchResults.add(obj) }
                        }
                    }
                }

                // Sorting the stuffs.
                when (searchSort) {
                    SearchDataSortType.SORT_BY_DATE_ASCENDING -> allSearchResults.sortBy { it.date }
                    SearchDataSortType.SORT_BY_DATE_DESCENDING -> allSearchResults.sortByDescending { it.date }
                    SearchDataSortType.SORT_BY_NAME_ASCENDING -> allSearchResults.sortBy { it.title }
                    SearchDataSortType.SORT_BY_NAME_DESCENDING -> allSearchResults.sortByDescending { it.title }
                }

                // 404.
                if (searchFilter.isEmpty()) result.postValue(SearchUiEvent.SearchFilterEmpty("You did not specify any search filter!"))
                else if (allSearchResults.isEmpty()) result.postValue(SearchUiEvent.SearchNotFound("Your search string does not match with any content."))
                else result.postValue(SearchUiEvent.SearchFinished("Search successful!", allSearchResults.size, allSearchResults))

            } catch (e: EmptySearchQueryException) {
                val msg = "${e::class.simpleName}: ${e.message}"
                result.postValue(SearchUiEvent.SearchQueryEmpty(msg))
                Logger.log({}, msg, LoggerType.WARNING)
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

enum class SearchDataSortType {
    SORT_BY_NAME_ASCENDING,
    SORT_BY_NAME_DESCENDING,
    SORT_BY_DATE_ASCENDING,
    SORT_BY_DATE_DESCENDING,
}

sealed class SearchUiEvent {
    data class SearchLoading(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_RESULT_LOADING) : SearchUiEvent()
    data class SearchFinished(val message: String, val queriedSearchItemCount: Int, val searchResult: List<SearchItemData>, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_FINISHED) : SearchUiEvent()
    data class SearchFilterEmpty(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_FILTER_EMPTY) : SearchUiEvent()
    data class SearchQueryEmpty(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_QUERY_EMPTY) : SearchUiEvent()
    data class SearchNotFound(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_RESULT_NOT_FOUND) : SearchUiEvent()
    data class SearchError(val message: String, val eventIdentifier: SearchUiEventIdentifier = SearchUiEventIdentifier.EVENT_SEARCH_ERROR) : SearchUiEvent()
}

enum class SearchUiEventIdentifier {
    EVENT_SEARCH_RESULT_LOADING,
    EVENT_SEARCH_FINISHED,
    EVENT_SEARCH_FILTER_EMPTY,
    EVENT_SEARCH_QUERY_EMPTY,
    EVENT_SEARCH_RESULT_NOT_FOUND,
    EVENT_SEARCH_ERROR,
}
