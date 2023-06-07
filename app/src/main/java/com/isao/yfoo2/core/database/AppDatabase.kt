package com.isao.yfoo2.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.isao.yfoo2.data.local.dao.FeedImageDao
import com.isao.yfoo2.data.local.dao.LikedImageDao
import com.isao.yfoo2.data.local.model.FeedImageCached
import com.isao.yfoo2.data.local.model.LikedImageCached

private const val DB_VERSION = 1

@Database(
    entities = [FeedImageCached::class, LikedImageCached::class],
    version = DB_VERSION
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun feedImageDao(): FeedImageDao

    abstract fun likedImageDao(): LikedImageDao
}