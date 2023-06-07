package com.isao.yfoo2.core.database

import android.content.Context
import androidx.room.Room
import com.isao.yfoo2.data.local.dao.FeedImageDao
import com.isao.yfoo2.data.local.dao.LikedImageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val APP_DB_NAME = "room"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            APP_DB_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideFeedImageDao(database: AppDatabase): FeedImageDao {
        return database.feedImageDao()
    }

    @Singleton
    @Provides
    fun provideLikedImageDao(database: AppDatabase): LikedImageDao {
        return database.likedImageDao()
    }
}