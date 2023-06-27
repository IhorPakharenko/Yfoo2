package com.isao.yfoo2.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.isao.yfoo2.core.BottomNavigationScreen
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun YfooNavHost(
    navController: NavHostController,
    factories: ImmutableSet<NavigationFactory>,
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
