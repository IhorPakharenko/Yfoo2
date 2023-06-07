package com.isao.yfoo2.presentation.feed.di

import com.isao.yfoo2.core.navigation.NavigationFactory
import com.isao.yfoo2.presentation.feed.FeedNavigationFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FeedModule {

    @Singleton
    @Binds
    @IntoSet
    fun bindFeedNavigationFactory(factory: FeedNavigationFactory): NavigationFactory
}