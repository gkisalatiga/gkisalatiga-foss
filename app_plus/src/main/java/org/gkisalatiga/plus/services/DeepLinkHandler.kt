/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import org.gkisalatiga.plus.db.MainCompanion
import org.gkisalatiga.plus.lib.AppNavigation
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.gkisalatiga.plus.screen.ScreenVideoListCompanion
import org.gkisalatiga.plus.screen.ScreenWebViewCompanion
import org.json.JSONArray
import org.json.JSONObject

class DeepLinkHandler {
    companion object {
        fun handleContributors() {
            AppNavigation.navigateCold(NavigationRoutes.SCREEN_CONTRIB)
        }

        fun handleMainGraphics() {
            AppNavigation.navigateCold(NavigationRoutes.SCREEN_MAIN)
        }

        fun handleSaRen() {
            // The "all playlist" section.
            val allVideoPlaylists: JSONArray = MainCompanion.jsonRoot!!.getJSONArray("yt")

            // Enlist the SaRen video lists to be shown in the video list.
            var sarenPlaylistTitle = ""
            var sarenPlaylistContent = JSONArray()

            // Find the SaRen playlist.
            for (i in 0 until allVideoPlaylists.length()) {
                // Do it specifically for SaRen.
                if ((allVideoPlaylists[i] as JSONObject).getString("title") == "Sapaan dan Renungan Pagi") {
                    sarenPlaylistTitle = (allVideoPlaylists[i] as JSONObject).getString("title")
                    sarenPlaylistContent = (allVideoPlaylists[i] as JSONObject).getJSONArray("content")
                    break
                }
            }

            // Enlist the SaRen content.
            val playlistContentList: MutableList<JSONObject> = mutableListOf()
            for (i in 0 until sarenPlaylistContent.length()) {
                playlistContentList.add(sarenPlaylistContent[i] as JSONObject)
            }

            // Display the list of SaRen videos.
            ScreenVideoListCompanion.putArguments(playlistContentList, sarenPlaylistTitle)
            AppNavigation.navigateCold(NavigationRoutes.SCREEN_VIDEO_LIST)
        }

        fun handleYKB() {
            AppNavigation.navigateCold(NavigationRoutes.SCREEN_YKB)
        }

        /**
         * Opens a URL of "gkisalatiga.org" host in this app's WebView.
         */
        fun openDomainURL(url: String, title: String = "GKI Salatiga") {
            // Navigate to the WebView viewer.
            ScreenWebViewCompanion.putArguments(url, title)
            AppNavigation.navigateCold(NavigationRoutes.SCREEN_WEBVIEW)
        }
    }
}