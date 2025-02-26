# gkisalatiga-foss

GKI Salatiga's free and open source Android church application based on Jetpack Compose.

This repository was migrated from the original repo, [GKI Salatiga Plus](https://github.com/gkisalatiga/gki-salatiga-plus), which has now been made a public archive.

## A. Documentation

### Coding Convention

- `const val` types should be written in uppercase (e.g., `MAIN_TOP_BAR_COLOR`). Meanwhile `val` types can be written in lowercase, if preferred.
- Mutable composable variable name should have `mutable` name prefix.
- Inline comments in a companion object should use the `/* */` syntax.

### Deep link URI pattern handling

The application is designed to handle URLs matching `gkisalatiga.org` URI with `https` scheme.

The application specifically use the `https://gkisalatiga.org/app/deeplink` URI pattern to handle navigations and patterns for internal uses (e.g., notification user-click action). This means we assume the path `/app/deeplink` should not exist in `gkisalatiga.org`'s actual website root, so that we can handle internal intent deep-linkings.

Currently, the list of registered deeplinks in this app is as follows:

- **`https://gkisalatiga.org/app/deeplink/consumption`:** Dummy for preventing deeplink redirection upon screen orientation change
- **`https://gkisalatiga.org/app/deeplink/contributors`:** Opens the list of contributors of GKI Salatiga+
- **`https://gkisalatiga.org/app/deeplink/main_graphics`:** Opens the main activity of the app
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

### Data Update

GKI Salatiga automatically fetches the latest JSON data from its server every time the app launches. The user can pull down in some screens in order to invoke data update manually. There is also an hourly `WorkManager` that automatically updates the app's content data to the latest in the background.

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

- [X] Add background feed fetcher (data updater) using `WorkManager`
- [X] Add "ruang berbagi" (promotional) feature
- [X] Add search content feature
- [X] Fix "Carousel not displaying the latest data"
- [X] Fix double splash screen on Android 12 or higher (or, perhaps, just remove splash screen entirely?)
- [X] Fix "Notification appears at exact time of the day, but at both AM and PM"
- [X] Migrate/upgrade main JSON data to v2.0
- [X] Migrate/upgrade gallery JSON data to v2.0
- [X] Migrate/upgrade static JSON data to v2.0
- [X] Remove ambiguous "upload date" of videos in "Content" tab

### November 2024

- [X] Migrate ytView parameter implementation from using `GlobalSchema` to using data class
- [X] Migrate the scroll states from `GlobalSchema` to the companion object of each screen class
- [X] Rename `GlobalSchema` class to something more appropriate (e.g., `GlobalState` or `GlobalParameter`)
- [X] Introduce "putArguments" implementation to `ScreenInternalHTMLCompanion` `ScreenYKBListCompanion`
- [X] Run a test: Migration from `v0.4.5-rc` and `v0.5.0-rc` to `v0.6.0-rc`
- [X] Add download progress display when downloading PDF files
- [X] Add the automatic PDF remover back-end functionality
- [X] Add the settings helper/documentation

### December 2024

- [X] Upload a new release with native debug code
- [X] Fix "Double Splash Screen" issue by actually implementing the `SplashScreen` API on Android 12+
- [X] Fix "Content Labeling Warning" by adding labels to clickable objects
- [X] Fix YouTube player suddenly stops when changing screen orientation (caused by commit 560426d "Added dark theme")
- [X] Add notification prompt in the `FragmentHome` if the user hasn't granted notification permission
- [X] Add notification prompt in the `FragmentHome` about new app update
- [X] Add "Sinode GKI" logo to the app
- [X] Improve icon sets ~and color theming in dark mode~
- [X] Remove JSON data fallback completely when launch count is more than 1 (prevent data not updated when offline)

### January 2025

- [X] Reduced the amount of visible page dots indication in the main menu's carousel
- [X] Migrate the JSON data source to `gkisalatiga/gkisplus-data-json` repository
- [X] Add street address of GKI Salatiga
- [X] Add non-open source, non-hard-coded third party resources attribution
- [X] Fix bottom keyboard padding when typing in forms menu
- [X] Fix main image not filling the entire screen on larger screens
- [X] Pukat Berkat: Hide the functionality until it is ready
- [X] Pukat Berkat: change name to "Lapak Jemaat"
- [X] Agenda: Add viewer of room borrowing schedules
- [X] Migrate the JSON data that includes "agenda-ruangan"
- [X] Add "GKI Salatiga's 82nd Anniversary" logo to the splash screen

### February 2025

- [X] Add QRIS image save button
- [X] Add button to manually delete downloaded PDF file in Warta Jemaat & Tata Ibadahmenu
- [X] Add button to manually delete downloaded PDF file in E-Book menu
- [ ] Gallery: fix export error on lower Android versions
- [ ] Add "max width" for screens when opened on tablet devices (or when the orientation is landscape)
- [ ] Agenda: Add hard-coded form to borrow room & differentiate regular from non-regular items
- [ ] Main carousel: only display posters
- [ ] Migrate video "carousel" content somewhere else
- [ ] Migrate article "carousel" content somewhere else

## C. Privacy Policy

The latest privacy policy document of GKI Salatiga can be read in this [GitHub repository file](https://github.com/gkisalatiga/gkisalatiga-foss/blob/main/PRIVACY_POLICY.md). However, this **README** file has summarized the privacy policy as follows:

- GKI Salatiga is open source
- GKI Salatiga collects personally identifiable data, such as (but not limited to): email, name, age, gender, address, date of birth, profile photo, location, and phone number
- GKI Salatiga does not use any third-party tracking service

## D. Attribution

### Open source materials used as hard-coded parts of the application

- Android Studio Asset Studio Icon Library; The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://developer.android.com/studio/write/create-app-icons)
- Android YouTube Player; Pierfrancesco Soffritti (C) 2023 (MIT) [Link](https://github.com/PierfrancescoSoffritti/android-youtube-player)
- Compose Markdown; Jeziel Lago (C) 2024 (MIT) [Link](https://github.com/jeziellago/compose-markdown)
- Compose UI - Coil; Coil Contributors (C) 2024 (Apache 2.0) [Link](https://github.com/coil-kt/coil)
- CoroutineFileDownload; Jovche Mitrejchevski (C) 2018 (Apache 2.0) [Link](https://github.com/mitrejcevski/coroutineFileDownload)
- Jetpack Compose Material3; The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://developer.android.com/jetpack/androidx/releases/compose-material3#1.3.0-beta04)
- Material Design Icons; Pictogrammers (C) 2024 (Apache 2.0) [Link 1](https://icon-sets.iconify.design/mdi/), [Link 2](https://github.com/Templarian/MaterialDesign)
- Material Symbols & Icons - Google Fonts; The Android Open Source Project (C) 2024 (SIL Open Font License) [Link](https://fonts.google.com/icons)
- Phosphor; Phosphor Icons (C) 2024 (MIT) [Link 1](https://icon-sets.iconify.design/ph), [Link 2](https://github.com/phosphor-icons/core)
- RemixIcon Icon Set; Remix-Design (C) 2024 (Apache 2.0) [Link 1](https://icon-sets.iconify.design/ri), [Link 2](https://github.com/Remix-Design/RemixIcon)
- WorkManager Kotlin Extensions; The Android Open Source Project (C) 2024 (Apache 2.0) [Link](https://mvnrepository.com/artifact/androidx.work/work-runtime-ktx)
- Zoomable; Atsushi USUI (C) 2024 (Apache 2.0) [Link](https://github.com/usuiat/Zoomable)

### Open source materials dynamically used in the WebView

- Framacarte; Framasoft (C) 2024 (CC BY-SA 4.0) [Link](https://framacarte.org/abc/en/)
- OpenStreetMap; OpenStreetMap Foundation (OSMF) (C) 2024 (ODbL 1.0) [Link](https://www.openstreetmap.org)
- Plus Jakarta Sans; Tokotype (C) 2020 (OFL 1.1) [Link](https://fonts.google.com/specimen/Plus+Jakarta+Sans?query=plus+jakarta+sans)

### License of biblical materials and electronic books used in the app per user's download consent

- Alkitab Yang Terbuka; Yayasan Lembaga SABDA (YLSA) (C) 2011-2024 (CC-BY-NC-SA-4.0) [Link 1](https://ebible.org/details.php?id=indayt) [Link 2](https://ayt.co)
- Bible in Basic English; Samuel Henry Hooke, Cambridge Press (Public Domain) [Link](https://ebible.org/find/show.php?id=engBBE)
- World English Bible; Michael Paul Johnson (Public Domain) [Link 1](https://ebible.org/find/show.php?id=engwebp) [Link 2](https://worldenglish.bible)

## E. Credits and Contributions

We thank all the people who have contributed gratefully to the development of GKI Salatiga app. This project was started on **July 12, 2024** and would not be possible without the help of these amazing people.

May the heavenly Father bless all your lives!

### Advisors

- Budi Santoso
- Tunggul L. A. Dewanto

### Lead Developer & Programmer

- Samarthya Lykamanuella

### Co-Developers

- Joaquim P. Agung
- Natanael J. Susanto

### Project Intern

- Alfina G. Y. Christy

### Pre-Release Testers

- Bambang Setiadhy
- Bayu Octariyanto
- Bobby Wilianto
- Budi Santoso
- Chrissandhy Botmir
- Christian R. Sukan
- Danang Purnomo
- Daniel E. Yudhianto
- Elza C. Tampubolon
- Enrico P. Wijaya
- Erio R. P. Fanggidae
- Eriyani T. Lunga
- Ester Juliawati
- Evangs Mailoa
- Evelina Purnama
- Gita Hastuti
- Gita K. Dewi
- Hendra Aribowo
- Jack E. Parsaoran
- Joaquim P. Agung
- Johnson U. Radda
- Joshua A. Gracia
- Lusiana Nurhayati
- Natanael J. Susanto
- Nathalia Arviandri
- Oktoviana B. Mowata
- Reny Handayani
- Ristiani G. Mendrofa
- Samuel A. B. Sulistyo
- Sriono Sudarso
- Stefanus F. S. Harefa
- Swarni N. Malimou
- Tunggul L. A. Dewanto
- Visi P. Pananginan
- Wurjayanti
- Yosua C. H. Kuncoro

### Pre-Release Supporters

- Amatya K. Paramasatya
- Desemy C. N. Ballo
- Helen R. Manurung
- Joe E. M. Sau
- William Handoko
