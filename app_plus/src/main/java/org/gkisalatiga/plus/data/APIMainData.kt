/**
 * AGPL-3.0-licensed
 * Copyright (C) GKI Salatiga 2024
 * Written by Samarthya Lykamanuella (github.com/groaking)
 */

@file:Suppress("SpellCheckingInspection")

package org.gkisalatiga.plus.data

data class APIMainData(
    var urlProfile: MainUrlProfileObject,
    var yt: MutableList<MainYouTubePlaylistObject>,
    var pdf: MainPdfRootObject,
    var agenda: MainAgendaRootObject,
    var forms: MutableList<MainFormsItemObject>,
    var ykb: MutableList<MainYKBListObject>,
    var offertory: MutableList<MainOffertoryObject>,
    var offertoryCode: MutableList<MainOffertoryCodeObject>,
    var carousel: MutableList<MainCarouselItemObject>,
    var pukatBerkat: MutableList<MainPukatBerkatItemObject>,
    var agendaRuangan: MutableList<MainAgendaRuanganItemObject>,
    var backend: MainBackendRootObject,
)

data class MainUrlProfileObject(
    var fb: String,
    var insta: String,
    var youtube: String,
    var web: String,
    var linktree: String,
    var whatsapp: String,
    var email: String,
    var maps: String,
)

data class MainYouTubePlaylistObject(
    var content: MutableList<MainYouTubeVideoContentObject>,
    var lastUpdate: String,
    var playlistId: String?,
    var rssTitleKeyword: String?,
    var title: String,
    var type: String,
    var pinned: Int,
)

data class MainYouTubeVideoContentObject(
    var title: String,
    var date: String,
    var desc: String,
    var link: String,
    var thumbnail: String,
)

data class MainPdfRootObject(
    var wj: MutableList<MainPdfItemObject>,
    var liturgi: MutableList<MainPdfItemObject>,
)

data class MainPdfItemObject(
    var title: String,
    var date: String,
    var link: String,
    var postPage: String,
    var thumbnail: String,
    var id: String,
    var size: String,
)

data class MainAgendaRootObject(
    var sun: MutableList<MainAgendaItemObject>,
    var mon: MutableList<MainAgendaItemObject>,
    var tue: MutableList<MainAgendaItemObject>,
    var wed: MutableList<MainAgendaItemObject>,
    var thu: MutableList<MainAgendaItemObject>,
    var fri: MutableList<MainAgendaItemObject>,
    var sat: MutableList<MainAgendaItemObject>,
    val mapping: MutableMap<String, MutableList<MainAgendaItemObject>> = mutableMapOf(
        "sun" to sun,
        "mon" to mon,
        "tue" to tue,
        "wed" to wed,
        "thu" to thu,
        "fri" to fri,
        "sat" to sat,
    )
)

data class MainAgendaItemObject(
    var name: String,
    var time: String,
    var timeTo: String,
    var timezone: String,
    var type: String,
    var place: String,
    var representative: String,
    var note: String
)

data class MainFormsItemObject(
    var title: String,
    var url: String,
)

data class MainYKBListObject(
    var title: String,
    var url: String,
    var banner: String,
    var posts: MutableList<MainYKBItemObject>,
)

data class MainYKBItemObject(
    var title: String,
    var shortlink: String,
    var scripture: MutableList<String>,
    var date: String,
    var featuredImage: String,
    var html: String
)

data class MainOffertoryObject(
    var bankName: String,
    var bankAbbr: String,
    var bankNumber: String,
    var bankLogoUrl: String,
    var accountHolder: String,
)

data class MainOffertoryCodeObject(
    var uniqueCode: String,
    var title: String,
    var desc: String,
)

data class MainCarouselItemObject(
    var banner: String,
    var title: String,
    var type: String,
    var dateCreated: String,
    var posterImage: String,
    var posterCaption: String,
)

data class MainPukatBerkatItemObject(
    var title: String,
    var desc: String,
    var price: String,
    var contact: String,
    var vendor: String,
    var type: String,
    var image: String,
)

data class MainAgendaRuanganItemObject(
    var name: String,
    var time: String,
    var timeTo: String,
    var timezone: String,
    var date: String,
    var weekday: String,
    var type: String,
    var place: String,
    var representative: String,
    var pic: String,
    var status: String,
    var note: String,
)

data class MainBackendRootObject(
    var flags: MainBackendFlagsItemObject,
    var strings: MainBackendStringsItemObject,
)

data class MainBackendFlagsItemObject(
    var isEasterEggDevmodeEnabled: Int,
    var isFeatureAgendaShown: Int,
    var isFeaturePersembahanShown: Int,
    var isFeatureYKBShown: Int,
    var isFeatureFormulirShown: Int,
    var isFeatureGaleriShown: Int,
    var isFeatureBibleShown: Int,
    var isFeatureLibraryShown: Int,
    var isFeatureLapakShown: Int,
    var isFeatureSeasonalShown: Int,
)

data class MainBackendStringsItemObject(
    var address: String,
    var aboutContactMail: String,
    var aboutChangelogUrl: String,
    var aboutGooglePlayListingUrl: String,
    var aboutSourceCodeUrl: String,
    var greetingsTop: String,
    var greetingsBottom: String,
)
