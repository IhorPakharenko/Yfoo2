package com.isao.yfoo2.presentation.feed

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.isao.yfoo2.core.BottomNavigationScreen
import com.isao.yfoo2.core.navigation.NavigationFactory
import com.isao.yfoo2.presentation.feed.composable.FeedRoute
import javax.inject.Inject

class FeedNavigationFactory @Inject constructor() : NavigationFactory {

    override fun create(builder: NavGraphBuilder) {
        builder.composable(BottomNavigationScreen.Feed.route) {
            FeedRoute()
        }
    }
}
