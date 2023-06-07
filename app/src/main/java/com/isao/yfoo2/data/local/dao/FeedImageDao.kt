package com.isao.yfoo2.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.isao.yfoo2.data.local.model.FeedImageCached
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedImageDao {

    @Query("SELECT * FROM FeedImageCached")
    fun getFeedImages(): Flow<List<FeedImageCached>>

    @Query("SELECT * FROM FeedImageCached WHERE id = :id")
    fun getFeedImage(id: String): Flow<FeedImageCached>

    @Upsert
    suspend fun saveFeedImage(item: FeedImageCached)

    @Query("DELETE FROM FeedImageCached WHERE id = :id")
    suspend fun deleteFeedImage(id: String)
}