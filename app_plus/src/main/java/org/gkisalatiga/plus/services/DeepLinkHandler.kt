/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import org.gkisalatiga.plus.data.MainYouTubeVideoContentObject
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
            // val allVideoPlaylists: JSONArray = MainCompanion.jsonRoot!!.getJSONArray("yt")
            val allVideoPlaylists = MainCompanion.api!!.yt

            // Enlist the SaRen video lists to be shown in the video list.
            var sarenPlaylistTitle: String = ""
            var sarenPlaylistContent: MutableList<MainYouTubeVideoContentObject> = mutableListOf()

            // Find the SaRen playlist.
            for (i in 0 until allVideoPlaylists.size) {
                // Do it specifically for SaRen.
                if (allVideoPlaylists[i].title == "Sapaan dan Renungan Pagi") {
                    sarenPlaylistTitle = allVideoPlaylists[i].title
                    sarenPlaylistContent = allVideoPlaylists[i].content
                    break
                }
            }

            // Enlist the SaRen content.
            val playlistContentList: MutableList<MainYouTubeVideoContentObject> = mutableListOf()
            for (i in 0 until sarenPlaylistContent.size) {
                playlistContentList.add(sarenPlaylistContent[i])
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