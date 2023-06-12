package com.isao.yfoo2.presentation.liked

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.isao.yfoo2.core.BottomNavigationScreen
import com.isao.yfoo2.core.navigation.NavigationFactory
import com.isao.yfoo2.presentation.liked.composable.LikedRoute
import javax.inject.Inject

class LikedNavigationFactory @Inject constructor() : NavigationFactory {

    override fun create(builder: NavGraphBuilder) {
        builder.composable(BottomNavigationScreen.Liked.route) {
            LikedRoute()
        }
    }
}
