package com.isao.yfoo2.data.testdoubles

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.isao.yfoo2.core.database.AppDatabase
import com.isao.yfoo2.data.local.dao.FeedImageDao
import com.isao.yfoo2.data.local.dao.LikedImageDao
import org.koin.core.annotation.Single

@Single(binds = [AppDatabase::class])
class FakeAppDatabase : AppDatabase() {
    override fun feedImageDao(): FeedImageDao {
        TODO("Not yet implemented")
    }

    override fun likedImageDao(): LikedImageDao {
        TODO("Not yet implemented")
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        TODO("Not yet implemented")
    }
}