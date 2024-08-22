@file:OptIn(KoinExperimentalAPI::class)

package com.isao.yfoo2.presentation.liked

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.isao.yfoo2.core.BottomNavigationScreen
import com.isao.yfoo2.core.navigation.NavigationFactory
import com.isao.yfoo2.presentation.liked.composable.LikedRoute
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.annotation.Single

@Single
class LikedNavigationFactory : NavigationFactory {

    override fun create(builder: NavGraphBuilder) {
        builder.composable(BottomNavigationScreen.Liked.route) {
            // Needed as a workaround for some crashes (mostly in tests)
            // https://github.com/InsertKoinIO/koin/issues/1557
            KoinAndroidContext {
            LikedRoute()
            }
        }
    }
}
