package com.isao.yfoo2.data.di

import com.isao.yfoo2.data.repository.LikedImageRepositoryImpl
import com.isao.yfoo2.domain.repository.LikedImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LikedModule {

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindsModule {

        @Binds
        @Singleton
        fun bindLikedImageRepository(impl: LikedImageRepositoryImpl): LikedImageRepository
    }
}