# gkisalatiga-foss
GKI Salatiga's free and open source Android church application based on Jetpack Compose.

## A. Documentation

### Coding Convention

- `const val` types should be written in uppercase (e.g., `MAIN_TOP_BAR_COLOR`). Meanwhile `val` types can be written in lowercase, if preferred.
- Mutable composable variable name should have `mutable` name prefix.
- Inline comments in a companion object should use the `/* */` syntax.

### Deep link URI pattern handling

The application is designed to handle URLs matching `gkisalatiga.org` URI with `https` scheme.

The application specifically use the `https://gkisalatiga.org/app/deeplink` URI pattern to handle navigations and patterns for internal uses (e.g., notification user-click action). This means we assume the path `/app/deeplink` should not exist in `gkisalatiga.org`'s actual website root, so that we can handle internal intent deep-linkings.

Currently, the list of registered deeplinks in this app is as follows:

- **`https://gkisalatiga.org/app/deeplink/saren`:** Opens the "SaRen" video playlist menu
- **`https://gkisalatiga.org/app/deeplink/ykb`:** Opens the list of YKB daily devotionals

Any URI with `gkisalatiga.org` host that does not match the above registered deeplink will automatically trigger the WebView and display the link in the app's WebView.

### Debug Logging

- Every debug message should clarify the class name where the logging comes from. Use `lib.Logger` logging methods do perform back-end logcat loggings. These methods automatically handle verbosing of enclosing class and method names. For instance, `Logger.log({}, "Hello World!")` to print out "Hello World" in the logcat display while also verbosing the class and method names where the logging takes place.
- Logging and `android.util.Log`-calling must only be done through `lib.Logger` class methods, for consistency and neatness.

### Notification

In the most recent update, GKI Salatiga activates the following scheduled notification:

- **04:00:05 (Daily):** The SaRen devotional video
- **12:00:05 (Daily):** The YKB devotional article reminder

### UI Class

User interface classes are categorized into two main classifications: **Screen** and **Fragment**.

A **Screen** is similar to an activity in the conventional Android ViewModel. It occupies the whole screen and can be navigated to/from another screen. Each screen UI in GKI Salatiga should be registered in `lib.NavigationRoutes` so that the navigator can handle the composition of the screen.

A **Fragment** is any reusable or dynamically rendered composable inside a screen. It cannot be navigated to/from another fragment or screen, and must be explicitly called in a UI composable class in order to be displayed.

Each UI class file should bear a global companion object, useful in passing data and arguments between screens and to expose the current UI's state to the rest of the app. The companion object's class should be prefixed with the word "companion." For instance, `ScreenMainCompanion` and `FragmentHomeCompanion`. The companion class should be stored within the same file wherein the respective UI class (e.g., `ScreenMain`) resides.

## B. Roadmap

### September 2024

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

### October 2024

- [ ] Add background feed fetcher (data updater) using `WorkManager`
- [X] Add "ruang berbagi" (promotional) feature
- [ ] Add search content feature
- [ ] Create the wiki page and migrate documentations from `README.md` to the respective wiki pages
- [X] Fix "Carousel not displaying the latest data"
- [ ] Fix double splash screen on Android 12 or higher (or, perhaps, just remove splash screen entirely?)
- [ ] Fix "Notification appears at exact time of the day, but at both AM and PM"
- [X] Migrate/upgrade main JSON data to v2.0
- [ ] Migrate/upgrade gallery JSON data to v2.0
- [ ] Migrate/upgrade static JSON data to v2.0
- [X] Remove ambiguous "upload date" of videos in "Content" tab

### November 2024

- [ ] Migrate ytView parameter implementation from using `GlobalSchema` to using data class
- [ ] Migrate the scroll states from `GlobalSchema` to the companion object of each screen class
- [ ] Rename `GlobalSchema` class to something more appropriate (e.g., `GlobalState` or `GlobalParameter`)

## C. Privacy Policy

The latest privacy policy document of GKI Salatiga can be read in this [GitHub repository file](https://github.com/gkisalatiga/gkisalatiga-foss/blob/main/PRIVACY_POLICY.md). However, this **README** file has summarized the privacy policy as follows:

- GKI Salatiga is open source
- GKI Salatiga collects personally identifiable data, such as (but not limited to): email, name, age, gender, address, date of birth, profile photo, location, and phone number
- GKI Salatiga does not use any third-party tracking service

## D. Attribution

This repository was migrated from the original repo, [GKI Salatiga Plus](https://github.com/gkisalatiga/gki-salatiga-plus), which has now been made a public archive.

### License of Materials Used

#### Open source materials used as hard-coded parts of the application

- Android Studio Asset Studio Icon Library; The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://developer.android.com/studio/write/create-app-icons)
- Android YouTube Player; Pierfrancesco Soffritti (C) 2023 (MIT) [Link](https://github.com/PierfrancescoSoffritti/android-youtube-player)
- Compose Markdown; Jeziel Lago (C) 2024 (MIT) [Link](https://github.com/jeziellago/compose-markdown)
- Compose UI - Coil; Coil Contributors  (C) 2024 (Apache 2.0) [Link](https://github.com/coil-kt/coil)
- Jetpack Compose Material3; The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-beta04)
- Material Symbols & Icons - Google Fonts; The Android Open Source Project (C) 2024 (SIL Open Font License) [Link](https://fonts.google.com/icons)
- Pdf-Viewer; Rajat Mittal (C) 2024 (MIT) [Link](https://github.com/afreakyelf/Pdf-Viewer)
- Phosphor; Phosphor Icons (C) 2024 (MIT) [Link 1](https://icon-sets.iconify.design/ph), [Link 2](https://github.com/phosphor-icons/core)
- RemixIcon Icon Set; Remix-Design (C) 2024 (Apache 2.0) [Link 1](https://icon-sets.iconify.design/ri), [Link 2](https://github.com/Remix-Design/RemixIcon)
- UnzipUtil; Nitin Praksh (C) 2021 (Apache 2.0) [Link 1](https://prakashnitin.medium.com/unzipping-files-in-android-kotlin-2a2a2d5eb7ae), [Link 2](https://gist.github.com/NitinPraksash9911/dea21ec4b8ae7df068f8f891187b6d1e)
- WorkManager Kotlin Extensions; The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://mvnrepository.com/artifact/androidx.work/work-runtime-ktx)
- ZoomableBox; Sean (C) 2022 (CC BY-SA 4.0) [Link](https://stackoverflow.com/a/72528056)

#### Open source materials dynamically used in the WebView

- Framacarte; Framasoft (C) 2024 (CC BY-SA 4.0) [Link](https://framacarte.org/abc/en/)
- OpenStreetMap; OpenStreetMap Foundation (OSMF) (C) 2024 (ODbL 1.0) [Link](https://www.openstreetmap.org)
- Plus Jakarta Sans; Tokotype (C) 2020 (OFL 1.1) [Link](https://fonts.google.com/specimen/Plus+Jakarta+Sans?query=plus+jakarta+sans)

#### License of biblical materials and electronic books used in the app per user's download consent

- Alkitab Yang Terbuka; Yayasan Lembaga SABDA (YLSA) (C) 2011-2024 (CC-BY-NC-SA-4.0) [Link 1](https://ebible.org/details.php?id=indayt) [Link 2](https://ayt.co)
- Bible in Basic English; Samuel Henry Hooke, Cambridge Press (Public Domain) [Link](https://ebible.org/find/show.php?id=engBBE)
- World English Bible; Michael Paul Johnson (Public Domain) [Link 1](https://ebible.org/find/show.php?id=engwebp) [Link 2](https://worldenglish.bible)
