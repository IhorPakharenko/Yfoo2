package com.isao.yfoo2.presentation.liked.di

import com.isao.yfoo2.core.navigation.NavigationFactory
import com.isao.yfoo2.presentation.liked.LikedNavigationFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface LikedModule {

    @Singleton
    @Binds
    @IntoSet
    fun bindLikedNavigationFactory(factory: LikedNavigationFactory): NavigationFactory
}