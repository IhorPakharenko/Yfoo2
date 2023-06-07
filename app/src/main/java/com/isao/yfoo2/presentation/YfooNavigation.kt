package com.isao.yfoo2.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.isao.yfoo2.core.BottomNavigationScreen

//TODO do we need it to be sealed class? Why not enum? Will this approach work with params?
sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Liked : Screen("liked")
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun YfooNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = BottomNavigationScreen.Feed.route
) {
    AnimatedNavHost(navController, startDestination, modifier) {
        composable(BottomNavigationScreen.Feed.route) {
//            FeedScreen()
        }
        composable(BottomNavigationScreen.Liked.route) {
            Box(Modifier.fillMaxSize())
        }
    }
}

//TODO JetNews sample executes more actions in similar cases. Check if they are of any use
fun NavHostController.navigateToSwipe() = navigateToMainScreen(Screen.Feed)
fun NavHostController.navigateToLiked() = navigateToMainScreen(Screen.Liked)

private fun NavHostController.navigateToMainScreen(screen: Screen) {
    navigate(screen.route) {
        launchSingleTop = true
    }
}
