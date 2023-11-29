# Yfoo 2 - A Compose & MVI + Clean Architecture showcase

An Android project showcasing best practices with Kotlin and latest Jetpack libraries.

The architecture was inspired by the [Android Kotlin starter project](https://github.com/krzdabrowski/android-starter-2022), though it has been modified significantly.

<img src="readme/feed.gif" alt="Feed" width="240">

### Description
This application showcases a custom Tinder-like UI designed for viewing an endless list of images. Images that are liked can subsequently be accessed on a separate screen. Utilizes images from [This Waifu Does Not Exist](https://www.thiswaifudoesnotexist.net/). Adopts offline-first approach. Supports light/dark mode and dynamic color from Material 3.

### Libraries/concepts used

* Kotlin Coroutines & Kotlin Flow
* Hilt
* Jetpack: Navigation, Room and Lifecycle
* Coil image loading library
* Material 3
* Accompanist: Navigation animation, Drawable painter and Placeholder
* KtLint and Detekt linters

<img src="readme/feed_day.png" alt="Feed" width="240"> <img src="readme/feed_night.png" alt="Feed with night mode" width="240"> <img src="readme/liked_day.png" alt="Liked" width="240"> <img src="readme/liked_day_2.png" alt="Liked" width="240"> <img src="readme/liked_day_3.png" alt="Liked" width="240"> <img src="readme/liked_night.png" alt="Liked with night mode" width="240">
