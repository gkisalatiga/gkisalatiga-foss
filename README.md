# gkisalatiga-foss
GKI Salatiga's free and open source Android church application based on Jetpack Compose, distributed for F-Droid.

## A. Documentation

### Deep link URI pattern handling

The application is designed to handle URLs matching `gkisalatiga.org` URI with `https` scheme.

The application specifically use the `https://gkisalatiga.org/plus/deeplink` URI pattern to handle navigations and patterns for internal uses (e.g., notification user-click action). This means we assume the path `/plus/deeplink` should not exist in `gkisalatiga.org`'s actual website root, so that we can handle internal intent deep-linkings.

Currently, the list of registered deeplinks in this app is as follows:

- **`https://gkisalatiga.org/plus/deeplink/saren`:** Opens the "SaRen" video playlist menu
- **`https://gkisalatiga.org/plus/deeplink/ykb`:** Opens the list of YKB daily devotionals

Any URI with `gkisalatiga.org` host that does not match the above registered deeplink will automatically trigger the WebView and display the link in the app's WebView.

### Notification

In the most recent update, GKI Salatiga activates the following scheduled notification:

- **04:00:05 (Daily):** The SaRen devotional video
- **12:00:05 (Daily):** The YKB devotional article reminder

## B. To-Do

- [X] Add splash screen at launch
- [X] Add change log to the "About" screen
- [X] Add new app updates checker
- [X] Add content refresher
- [X] Add privacy policy
- [X] Change SVG resources color according to theme [(reference)](https://stackoverflow.com/questions/33126904/change-fillcolor-of-a-vector-in-android-programmatically)
- [X] Replace hard-coded strings, values, and dimensions with Android resource XML values
- [X] Replace the implementation of "GlobalSchema.context" with "LocalContext.current" to prevent memory leak
- [X] Replace debug toasts with "if (debug)" expressions, in which "debug" variable can be toggled manually
- [X] Fix bottom nav not scrolling the horizontal pager issue
- [ ] Create the wiki page and migrate documentations from `README.md` to the respective wiki pages

## C. Privacy Policy

The latest privacy policy document of GKI Salatiga+ can be read in this [GitHub repository file](https://github.com/gkisalatiga/gkisalatiga-foss/blob/main/PRIVACY_POLICY.md). However, this **README** file has summarized the privacy policy as follows:

- GKI Salatiga is open source
- GKI Salatiga collects personally identifiable data, such as (but not limited to): email, name, age, gender, address, date of birth, profile photo, location, and phone number
- GKI Salatiga does not use any third-party tracking service

## D. Attribution

This repository was migrated from the original repo, [GKI Salatiga Plus](https://https://github.com/gkisalatiga/gki-salatiga-plus). You will see throughout this repo that some codes still refer to GKI Salatiga **_Plus_** instead of just GKI Salatiga. This is minor and does not affect the overall experience of the app.

### License of Materials Used

#### Open source materials used as hard-coded parts of the application

- Android Studio Asset Studio Icon Library, The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://developer.android.com/studio/write/create-app-icons)
- Android YouTube Player, Pierfrancesco Soffritti (C) 2023 (MIT) [Link](https://github.com/PierfrancescoSoffritti/android-youtube-player)
- Compose Markdown, Jeziel Lago (C) 2024 (MIT) [Link](https://github.com/jeziellago/compose-markdown)
- Jetpack Compose Material3, The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-beta04)
- Material Symbols & Icons - Google Fonts, The Android Open Source Project (c) 2024 (SIL Open Font License) [Link](https://fonts.google.com/icons)
- RemixIcon Icon Set, Remix-Design (C) 2024 (Apache 2.0) [Link 1](https://icon-sets.iconify.design/ri), [Link 2](https://github.com/Remix-Design/RemixIcon)
- UnzipUtil, Nitin Praksh (C) 2021 (Apache 2.0) [Link 1](https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae), [Link 2](https://gist.github.com/NitinPraksash9911/dea21ec4b8ae7df068f8f891187b6d1e)
- WorkManager Kotlin Extensions (C) 2024 (Apache 2.0) [Link](https://mvnrepository.com/artifact/androidx.work/work-runtime-ktx)
- ZoomableBox, Sean (C) 2022 (CC BY-SA 4.0) [Link](https://stackoverflow.com/a/72528056)

#### Open source materials dynamically used in the WebView

- Framacarte, Framasoft (C) 2024 (CC BY-SA 4.0) [Link](https://framacarte.org/abc/en/)
- OpenStreetMap, OpenStreetMap Foundation (OSMF) (C) 2024 (ODbL 1.0) [Link](https://www.openstreetmap.org)
- Plus Jakarta Sans, Tokotype (C) 2020 (OFL 1.1) [Link](https://fonts.google.com/specimen/Plus+Jakarta+Sans?query=plus+jakarta+sans)
