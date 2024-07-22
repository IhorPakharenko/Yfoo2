package com.isao.yfoo2.core.database

import androidx.room.Room
import com.isao.yfoo2.data.local.dao.FeedImageDao
import com.isao.yfoo2.data.local.dao.LikedImageDao
import org.koin.dsl.module

private const val APP_DB_NAME = "room"

val databaseModule = module {
    single<AppDatabase> {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            APP_DB_NAME
        ).build()
    }

    single<FeedImageDao> { get<AppDatabase>().feedImageDao() }
    single<LikedImageDao> { get<AppDatabase>().likedImageDao() }
}