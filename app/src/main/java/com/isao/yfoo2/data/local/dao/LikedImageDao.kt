package com.isao.yfoo2.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.isao.yfoo2.data.local.model.LikedImageCached
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedImageDao {

    @Query("SELECT * FROM LikedImageCached")
    fun getLikedImages(): Flow<List<LikedImageCached>>

    @Upsert
    suspend fun saveLikedImage(item: LikedImageCached)
}