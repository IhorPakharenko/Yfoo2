package com.isao.yfoo2.core

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.isao.yfoo2.R
import com.isao.yfoo2.core.navigation.NavigationFactories
import com.isao.yfoo2.core.navigation.Screen
import com.isao.yfoo2.core.navigation.YfooNavHost
import com.isao.yfoo2.core.theme.Yfoo2Theme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.koinInject

enum class BottomNavigationScreen(
    val route: String,
    @StringRes val nameRes: Int,
    val icon: ImageVector
) {
    Feed(Screen.Feed.route, R.string.feed, Icons.Filled.Explore),
    Liked(Screen.Liked.route, R.string.liked, Icons.Filled.Favorite),
}

class MainActivity : FragmentActivity() {

    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Let the app take up all screen space, including system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            Yfoo2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberAnimatedNavController()
                    val bottomNavigationScreens = remember {
                        listOf(
                            BottomNavigationScreen.Feed,
                            BottomNavigationScreen.Liked
                        )
                    }

                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination

                                bottomNavigationScreens.forEach { screen ->
                                    NavigationBarItem(
                                        icon = {
                                            Icon(
                                                imageVector = screen.icon,
                                                contentDescription = null
                                            )
                                        },
                                        label = { Text(stringResource(screen.nameRes)) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                // Pop up to the start destination of the graph to
                                                // avoid building up a large stack of destinations
                                                // on the back stack as users select items
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                // Avoid multiple copies of the same destination when
                                                // reselecting the same item
                                                launchSingleTop = true
                                                // Restore state when reselecting a previously selected item
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        // Let the content take up all available space.
                        // Material3 components handle the insets themselves
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { padding ->
                        // Needed as a workaround for some crashes (mostly in tests)
                        // https://github.com/InsertKoinIO/koin/issues/1557
                        KoinAndroidContext {
                            YfooNavHost(
                                navController = navController,
                                factories = koinInject<NavigationFactories>().list,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(padding),
                            )
                        }
                    }
                }
            }
        }
    }
}
