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
        throw NotImplementedError() // Stub
    }

    override fun likedImageDao(): LikedImageDao {
        throw NotImplementedError() // Stub
    }

    override fun clearAllTables() {
        throw NotImplementedError() // Stub
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        throw NotImplementedError() // Stub
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        throw NotImplementedError() // Stub
    }
}