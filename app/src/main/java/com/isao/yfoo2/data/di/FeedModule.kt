package com.isao.yfoo2.data.di

import com.isao.yfoo2.data.repository.FeedImageRepositoryImpl
import com.isao.yfoo2.domain.repository.FeedImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {

        @Binds
        @Singleton
        fun bindFeedImageRepository(impl: FeedImageRepositoryImpl): FeedImageRepository
    }
}