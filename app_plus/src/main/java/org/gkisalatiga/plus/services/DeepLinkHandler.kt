/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

package org.gkisalatiga.plus.services

import org.gkisalatiga.plus.global.GlobalSchema
import org.gkisalatiga.plus.lib.NavigationRoutes
import org.json.JSONArray
import org.json.JSONObject

class DeepLinkHandler {
    companion object {
        fun handleSaRen() {
            GlobalSchema.defaultScreen.value = NavigationRoutes.SCREEN_VIDEO_LIST

            // The "all playlist" section.
            val allVideoPlaylists: JSONArray = GlobalSchema.globalJSONObject!!.getJSONArray("yt")

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
            GlobalSchema.videoListContentArray = playlistContentList
            GlobalSchema.videoListTitle = sarenPlaylistTitle
            GlobalSchema.pushScreen.value = NavigationRoutes.SCREEN_VIDEO_LIST
            GlobalSchema.ytVideoListDispatcher = NavigationRoutes.SCREEN_MAIN
        }

        fun handleYKB() {
            GlobalSchema.defaultScreen.value = NavigationRoutes.SCREEN_YKB
            GlobalSchema.pushScreen.value = NavigationRoutes.SCREEN_YKB
        }

        /**
         * Opens a URL of "gkisalatiga.org" host in this app's WebView.
         */
        fun openDomainURL(url: String, title: String = "GKI Salatiga") {
            // Set the WebView as the default screen.
            GlobalSchema.defaultScreen.value = NavigationRoutes.SCREEN_WEBVIEW

            // Set the "backPressed" anchor point
            GlobalSchema.popBackScreen.value = NavigationRoutes.SCREEN_MAIN

            // Navigate to the WebView viewer.
            GlobalSchema.webViewTargetURL = url
            GlobalSchema.webViewTitle = title
            GlobalSchema.pushScreen.value = NavigationRoutes.SCREEN_WEBVIEW
        }
    }
}