/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 *
 * ---
 * Manages formatting of dates.
 */

package org.gkisalatiga.plus.lib

import android.net.UrlQuerySanitizer

class StringFormatter {

    // List of local month names in Indonesian.
    private val monthLocaleInIndonesia = mapOf<String, String>(
        "01" to "Januari",
        "02" to "Februari",
        "03" to "Maret",
        "04" to "April",
        "05" to "Mei",
        "06" to "Juni",
        "07" to "Juli",
        "08" to "Agustus",
        "09" to "September",
        "10" to "Oktober",
        "11" to "November",
        "12" to "Desember"
    )

    // List of local day names in Indonesian.
    public val dayLocaleInIndonesian = mapOf<String, String>(
        "mon" to "Senin",
        "tue" to "Selasa",
        "wed" to "Rabu",
        "thu" to "Kamis",
        "fri" to "Jumat",
        "sat" to "Sabtu",
        "sun" to "Minggu"
    )

    /**
     * Convert YYYY-MM-DD date format used in the JSON metadata
     * into locale date, similar to "31 Desember 2000"
     * SOURCE: https://stackoverflow.com/a/57405294
     * @param inputString the input string to format into locale.
     */
    public fun convertDateFromJSON(inputString: String): String {
        val a = inputString.split("-")

        // Removes leading zeros from the day.
        val day = if (a[2].startsWith("0")) a[2].replaceFirst("0", "") else a[2]

        return "${day} ${monthLocaleInIndonesia[a[1]]} ${a[0]}"
    }

    /**
     * Returns a Google Drive link that has "/preview" suffix in the URL.
     */
    public fun getGoogleDrivePreview(url: String): String {
        var a: String = ""

        if (url.endsWith("/view")) {
            a = url.replace("/view", "/preview")
        } else if (url.endsWith("/preview")) {
            a = url
        } else if (url.endsWith("/edit")) {
            a = url.replace("/edit", "/preview")
        } else if (url.endsWith("/")) {
            a = url + "preview"
        }

        return a
    }

    public fun getGoogleDriveDownloadURL(photoId: String): String {
        return "https://drive.google.com/uc?export=download&id=$photoId"
    }

    /**
     * Returns the statically generated thumbnail of photos stored in Google Drive.
     * SOURCE: https://stackoverflow.com/questions/25648388/permanent-links-to-thumbnails-in-google-drive-api
     */
    public fun getGoogleDriveThumbnail(photoId: String, quality: Int = 80): String {
        return "https://drive.google.com/thumbnail?authuser=0&sz=w$quality&id=$photoId"
    }

    /**
     * Cleans a YouTube link by removing redundant parts.
     * @param url the link of the YouTube video.
     * @return the YouTube video ID of the link.
     */
    public fun getYouTubeIDFromUrl(url: String): String {

        // Sanitize the URL, so that we only obtain the "watch?v=..." query argument.
        // SOURCE: https://stackoverflow.com/a/56622688
        val sanitizer = UrlQuerySanitizer()
        sanitizer.allowUnregisteredParamaters = true
        sanitizer.parseUrl(url)
        // Retrieve the YouTube ID string from the URL.
        val ytID = sanitizer.getValue("v")!!

        return ytID
    }

    /**
     * Returns the link to the thumbnail of a given YouTube video,
     * according to the passed URL parameter.
     *
     * @param quality the quality of the YouTube thumbnail.
     * Possible quality values:
     * - 0: "default"   120x90
     * - 1: "mq"        320x180
     * - 2: "hq"        480x360
     * - 3: "sq"        640x480
     * - 4: "max"       1280x720
     * Defaults to the highest possible quality ("hq")
     */
    public fun getYouTubeThumbnailFromUrl(url: String, quality: Int = 2): String {
        // First we extract the YouTube ID from the URL.
        val ytID = getYouTubeIDFromUrl(url)

        // Determining the thumbnail quality parameter.
        var qualityParam = "default"
        when(quality) {
            0 -> qualityParam = "default"
            1 -> qualityParam = "mqdefault"
            2 -> qualityParam = "hqdefault"
            3 -> qualityParam = "sddefault"
            4 -> qualityParam = "maxresdefault"
        }

        // Then we return the "formatted" YouTube thumbnail URL.
        // SOURCE: https://stackoverflow.com/a/55890696
        return "https://img.youtube.com/vi/$ytID/$qualityParam.jpg"
    }
}