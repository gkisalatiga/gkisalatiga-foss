# GKI Salatiga App Changelog

## v0.6.3 2025.01.15 (38)

:star2: **NEW**

- Added the feature to view room borrowing schedules and proposals
- Added prompt when clicking on the social media CTA buttons
- (Back-End) Introduced feature flags to change the greeting message
- (Back-End) Introduced feature flags to enable/disable main menu items

:four_leaf_clover: **IMPROVEMENT**

- Added non-hard-coded third party resources attribution
- Added GKI Salatiga's 82th anniversary logo as a splash screen
- Improved the outlook of the carousel page indicator
- Updated the gallery banner

:hammer_and_wrench: **FIX**

- Fixed virtual keyboard overlapping forms content

## v0.6.2 2024.12.29 (37)

:four_leaf_clover: **IMPROVEMENT**

- (Back-End) Migrated the JSON repository source to `gkisalatiga/gkisplus-data-json`

## v0.6.1 2024.12.28 (36)

:star2: **NEW**

- Added main menu prompt for notification permission on Android API 33 and above
- Added main menu prompt to update the application

:four_leaf_clover: **IMPROVEMENT**

- Added "GKI" logo to several locations of interest in the app
- Attempted to solve "Content Labeling Warning" by adding labels to clickable objects
- Attempted to solve "Double Splash Screen" by implementing Android 12+ splash screen API
- (Back-End) Compressed the fallback JSON data to conserve more space
- (Back-End) Implemented `ActivityData` in all screens and fragments, replacing `LocalContext` and `LocalUriHandler`
- (Back-End) Removed JSON fallback calling when app launch count is more than one to prevent old data from showing up

:hammer_and_wrench: **FIX**

- Fixed YouTube player suddenly stops playing when the screen orientation changes (caused by commit 560426d "Added dark theme")

## v0.6.0-rc.5 2024.11.26 (35)

:hammer_and_wrench: **FIX**

- Fixed incomplete PDF download when using the Kotlin R8 release compilation
- Fixed PDF download cancellation does not halt the back-end of data transfer

## v0.6.0-rc.4 2024.11.26 (34)

:hammer_and_wrench: **FIX**

- Attempted to fix incomplete PDF download when using the signed Google Play APK

## v0.6.0-rc.3 2024.11.26 (33)

:hammer_and_wrench: **FIX**

- Internal PDF viewer's page does not fill the entire screen's width

## v0.6.0-rc.2 2024.11.26 (32)

:information_source: **INFO**

- This release ships minor patches to Google Play Store

## v0.6.0-rc 2024.11.26 (31)

:star2: **NEW**

- Added dark theme
- Added content search functionality for menus: Warta Jemaat, Liturgi, YKB, and YouTube video
- Added search history
- Added e-book module reader menu

## v0.5.2-rc 2024.11.20 (30)

:information_source: **INFO**

- This release includes code cleaning and import optimization
- This release does not generate any app build

:star2: **NEW**

- Fully supported internal PDF viewer with the ability to cancel ongoing PDF download operations
- (Back-End) Added automatic PDF remover if a given PDF file has not been accessed for a given time

:four_leaf_clover: **IMPROVEMENT**

- Standardized the color scheme and improved the splash screen display
- Updated several menu's banner graphics to match app's color palette
- (Back-End) Improved deep link handling and routing mechanisms

:hammer_and_wrench: **FIX**

- Bug on fullscreen youtube player (especially from opening saren notification)

## v0.5.1-rc 2024.11.08 (29)

:information_source: **INFO**

- This release is **for internal/developer use only**
- This release uses a custom signing key generated using Android Studio. Any releases previously installed through Google Play Store must be uninstalled first

:star2: **NEW**

- Added a preliminary design of the internal PDF viewer
- Added page navigation and PDF info in the internal PDF viewer
- Added the app's internal preference menu
- Added the ability to change the PDF page rendering quality
- (Back-End) Introduced `LocalStorage` for storing persistent data across launches

:four_leaf_clover: **IMPROVEMENT**

- Added the ability to switch between new/old YouTube video player UI
- (Back-End) Added support for composite/mixed nested screen routing
- (Back-End) Simplified the screen navigation by centralizing all navigations to `AppNavigation`
- (Back-End) Migrated scroll states from `GlobalSchema` to the respective companion class of screens

:hammer_and_wrench: **FIX**

- Fixed deeplinking not working with `https://www.gkisalatiga.org` domain
- Fixed very long screen blank upon launching the app (caused by blocking due to JSON data download)
- Fixed app crashes when there is network error (38b6e5fa9f061ddd31525d554c24e0239bc43838)
- Fixed the AM/PM notification showing up bug of Renungan YKB

## v0.5.0-rc 2024.10.28 (28)

:star2: **NEW**

- Added the "Pukat Berkat" menu
- (Back-End) Added the developer menu
- (Minor) Added emoticons to the changelog document

:four_leaf_clover: **IMPROVEMENT**

- Changed the logo to reflect the "new look" of GKI Salatiga
- Changed the main menu logo to "Phosphor" icon sets for a better look
- Converted the license, privacy policy, and contributor meta-information page from dialog to screen
- Improved the agenda menu by introducing paging and material cards
- Improved the YKB menu by adding daily "feed" and YKB devotional archives
- Removed the "upload date" text from the video list in the main menu because it looks confusing
- Upgraded the JSON schema version of the main data to `v2.0`
- (Back-End) Standardized the logcat logging mechanism by introducing `lib.Logger`, which verboses method and class names

:hammer_and_wrench: **FIX**

- Fixed carousel not displaying the latest data, even after data download
- Fixed notification cannot be removed and does not call any action upon click
- Fixed the "About" menu content list does not save scroll state
- Now notifications show up at an exact time of the day; fixed random notification appearance

## v0.4.4-rc 2024.09.02 (27)

:information_source: **INFO**

- This release includes code cleaning of some obsolete code blocks, including the `ScreenMinistry` class

:four_leaf_clover: **IMPROVEMENT**

- Added the gallery banner
- Adjusted paddings in the "About Church" section
- Now the YouTube player seamlessly transitions between normal and fullscreen player

:hammer_and_wrench: **FIX**

- Fixed app update notification gets displayed even when the app is already up-to-date
- Fixed the YouTube video player seekbar gets trimmed when the device has bottom/navigation bar

## v0.4.3-beta 2024.08.29 (26)

:information_source: **INFO**

- This release is a general back-end update before advancing to the closed testing phase

:star2: **NEW**

- Added snack bar for offline notice and bottom sheet dialog for new app update notice
- (Back-End) Added the ability to manually refresh data
- (Back-End) Added internet connectivity checker
- (Back-End) Added new application update detector

## v0.4.2-beta 2024.08.27 (25)

:information_source: **INFO**

- This release contains general bug fixes and feature update without APK compilation

:star2: **NEW**

- Added English translation for the `strings.xml` file
- Added offertory transfer code list
- Added offertory bank accoount logo

:hammer_and_wrench: **FIX**

- `ScreenMain` leaves a significant white gap between the content and the top banner #42
- White color on banner image when the carousel data has not been downloaded #46
- App crashes after 5 seconds since launch on Pixel 5 #44

## v0.4.1-beta 2024.08.25 (24)

:information_source: **INFO**

- This release is general bug fixes and visual improvement release
- This relase uses signing key generated from Google Play Store
- Warning! You must uninstall any previous installation of GKI Salatiga+ in order to install this version

:four_leaf_clover: **IMPROVEMENT**

- Changed the banners on menus: "Warta", "Liturgi", "YKB", "Agenda", and "Forms" by @KimJoZer
- Changed dependency from "Compose RichText" to "Compose Markdown" for more advanced functionalities
- The "profile info" static content now has sub-folders and nested content support
- For performance optimalization, the "profile info" static data now uses JSON files instead of zip archives

:hammer_and_wrench: **FIX**

- Fixed carousel page always resetting to page no. 1 after clicking a carousel content/poster
- Email app not showing the destination email address upon clicking a "mailto" button in GKI Salatiga+
- Gallery crashes and cannot open when the app is offline #37
- Performance issue due to continuous reading of JSON main data is now solved
- StaticMemoryLeak solved by using `LocalContext.current` implementation to obtain the current context in non-main classes

## v0.4.0-beta 2024.08.21 (23)

:star2: **NEW**

- Added privacy policy notice
- Added changelog, license information, open source library attributions, contributor list, and repo link in the "About" screen

:four_leaf_clover: **IMPROVEMENT**

- Adjusted the font size of forms, gallery, YKB devotion, church news, and liturgy menus
- Changed the image loading graph from "omon-omon" to "no internet connection"

:hammer_and_wrench: **FIX**

- Fixed services fragment not displaying the first YouTube video in a playlist

## v0.3.2-beta 2024.08.19 (22)

:star2: **NEW**

- Added the "Media" screen that displays non-pinned GKI Salatiga YouTube playlists

:four_leaf_clover: **IMPROVEMENT**

- Added thumbnail aspect ratios in order to maintain layout size and position
- Locked orientation to portrait on non-fullscreen YouTube screens
- Improved the outlook and display of the video viewer and playlist display
- (Back-End) Added override debug settings to disable downloading static and carousel zip files

## v0.3.1-beta.2 2024.08.16 (21)

:hammer_and_wrench: **FIX**

- Fixed "targeting S+ version 31 and above" error on HyperOS devices

## v0.3.0-beta 2024.08.16 (20)

:star2: **NEW**

- The event documentation gallery feature has now been introduced
- The scheduled app's notification is introduced
- Added OnBoot listener so that the app continues to display important notifications even after restarting the phone

:hammer_and_wrench: **FIX**

- Fixed app not displaying cached images and static menus when going offline
- Fixed bottom bar item flickering when transitioning from one fragment to another in ScreenMain
- Temporary fix for full screen video player displaying white paddings
- BackHandler not responding to "back button press" on main screen

## v0.2.2-beta 2024.08.14 (19)

:star2: **NEW**

- Added QRIS code image to the "Offertory" menu

:hammer_and_wrench: **FIX**

- Fixed app rotating even with the auto-rotate turned off
- Fixed banner carousel does not extract the latest zip file

## v0.2.1-alpha 2024.08.04 (18)

:star2: **NEW**

- Added new screens: agenda and offertory (persembahan)
- Added social media call-to-action (CTA) buttons in the FragmentAbout

:four_leaf_clover: **IMPROVEMENT**

- Added representative icons to main menu items

## v0.2.0-alpha 2024.08.03 (17)

:star2: **NEW**

- The main menu now has an elegant scrolling behavior

:hammer_and_wrench: **FIX**

- Fixed YouTube player not resetting to "time = 0" when opening a new video

## v0.1.9-alpha 2024.08.02 (16)

:information_source: **INFO**

- The YouTube player now automatically pauses when the app is minimized to background

:star2: **NEW**

- The YouTube video viewer now has a fully functional full screen player

:hammer_and_wrench: **FIX**

- Fixed current screen state not saved when the phone's orientation changes

## v0.1.8-alpha 2024.08.01 (15)

:information_source: **INFO**

- The fullscreen YouTube viewer is now being prototyped

:star2: **NEW**

- Added "clickable logo" in ScreenAbout
- Carousel banner now has three action types: YouTube video, internet article, and poster display

:hammer_and_wrench: **FIX**

- Main menu fragments now remember scroll state across navigations
- Fixed church profile menus not showing when there is no static data update
- LazyVerticalGrid in FragmentHome is now replaced with mathematically calculated rows and columns in order to increase stability
- OutOfMemoryError when scaling large images on Xiaomi Mi-4c (By moving all images to drawable-nodpi. See: https://stackoverflow.com/a/77082456)

## v0.1.7-alpha 2024.07.31 (14)

:information_source: **INFO**

- The screen orientation is now locked to portrait mode

:star2: **NEW**

- Added internal preferences/settings which are persistently saved across launches
- Introduced the packed static data for zipping the "profile" menu

:four_leaf_clover: **IMPROVEMENT**

- ScreenVideoLive now has better visual appearance and scrollable description box

## v0.1.6-alpha 2024.07.30 (13)

:information_source: **INFO**

- The extended Jetpack Compose material icon pack is now enabled in Gradle

:star2: **NEW**

- Added transition animation (fade) between screens

:hammer_and_wrench: **FIX**

- Changed WebView external icon to "OpenAsNew"
- Enabled zooming by pinching in WebView
- Element colors now follow main theme
- YouTube viewer throws NPE when pressing "back"

## v0.1.5-alpha 2024.07.27 (12)

:star2: **NEW**

- Added YouTube video description display
- "Show more videos" feature

:four_leaf_clover: **IMPROVEMENT**

- Smoothed out transitions between HorizontalPager pages in the main menu

## v0.1.4-alpha 2024.07.26 (11)

:star2: **NEW**

- The JSON database now autofetches online updates without requiring admin supervision

## v0.1.3-alpha 2024.07.23 (10)

:star2: **NEW**

- Added the internal HTML WebView for displaying custom HTML string data

:four_leaf_clover: **IMPROVEMENT**

- Updated the main menu carousel duration from 1 second to 2 second
- Replaced the dummy bottom nav bar icons with the appropriate icons
- In the "About Church" menu, each menu item card now has visually appealing background.
- Enabled APK compression and optimization

:hammer_and_wrench: **FIX**

- YouTube ID grabber now mitigates additional query parameter being added into the URL
- Fixed the section title in the "Konten" menu overlapping the "show more" button

## v0.1.2-alpha 2024.07.23 (9)

:star2: **NEW**

- Added horizontal auto-scrolling carousel in the main menu

:four_leaf_clover: **IMPROVEMENT**

- Church news and liturgy are now highlighted in the main menu as the "primary items"
- Tinted the background color blue during splash screen to harmonize with the logo
- Merged live and pre-recorded YouTube videos into the "content" navigation menu

## v0.1.1-alpha 2024.07.21 (8)

:star2: **NEW**

- Added GKI Salatiga+ application logo

:hammer_and_wrench: **FIX**

- Fixed bottom nav not scrolling to the intended horizontal pager page

## v0.1.0-alpha 2024.07.20 (7)

:information_source: **INFO**

- Info: This version is major feature introduction pre-release

:star2: **NEW**

- Added "Warta Jemaat" PDF document viewer feature
- Added "Tata Ibadah" PDF document viewer feature
- Added "SaRen Pagi" morning devotional
- Added "Renungan YKB" daily devotion from Yayasan Komunikasi Bersama
- Added church forms menu

## v0.0.6-alpha 2024.07.20 (6)

:information_source: **INFO**

- This release is non-production ready

:star2: **NEW**

- Added splash screen to the app
- Added the live YouTube video viewer

:hammer_and_wrench: **FIX**

- Replaced AnimatedVisibility with HorizontalPager for the main menu fragment display
- Replaced NavHost-based navigation with custom-made GlobalSchema to improve code cleanliness
- Reduced the number of bottom navigation menu to 3 menus

## v0.0.5-alpha 2024.07.17 (5)

:information_source: **INFO**

- This non-production ready release generates a signed APK

:star2: **NEW**

- Added link confirmation dialog for opening external social media links
- Added preliminary profile information screen and fragments

## v0.0.4-alpha 2024.07.17 (4)

:star2: **NEW**

- Added the preliminary layout of the bottom sheet

:hammer_and_wrench: **FIX**

- Fix: Fixed padding on most YouTube video thumbnails
- Fix: Fixed padding on text-based card elements

## v0.0.3-alpha 2024.07.16 (3)

:star2: **NEW**

- Added the main screen menus: Home, Services, News, and Events
- Added features to display daily Bible verses and welcome banner
- Added church profile buttons

## v0.0.2-alpha 2024.07.16 (2)

:star2: **NEW**

- Added inter-fragment switching mechanism

:hammer_and_wrench: **FIX**

- Fix: Fixed nested NavHost not working by substituting with AnimatedVisibility

## v0.0.1-alpha 2024.07.15 (1)

:information_source: **INFO**

- This is the very first release of GKI Salatiga+

:star2: **NEW**

- Added bottom navigation and FAB
- Added top navigation
- Created the Composable navigation routing for screens and fragments
