package com.isao.yfoo2.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.isao.yfoo2.core.BottomNavigationScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    factories: Set<NavigationFactory>,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationScreen.Feed.route,
        modifier = modifier,
    ) {
        factories.forEach {
            it.create(this)
        }
    }
}
