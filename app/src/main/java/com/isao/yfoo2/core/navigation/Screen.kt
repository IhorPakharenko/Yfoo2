package com.isao.yfoo2.core.navigation

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Liked : Screen("liked")
}
