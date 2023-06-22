package com.isao.yfoo2.presentation

//TODO do we need it to be sealed class? Why not enum? Will this approach work with params?
sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Liked : Screen("liked")
}
