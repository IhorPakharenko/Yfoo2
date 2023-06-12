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

//    @Query("""
//        SELECT * FROM LikedImageCached ORDER BY
//        CASE WHEN :sortAscending = 1 THEN ID END ASC,
//        CASE WHEN :sortAscending = 0 THEN ID END DESC
//        LIMIT :limit OFFSET :offset
//        """)
//    fun getLikedImages(sortAscending: Boolean, limit: Int, offset: Int)

    @Upsert
    suspend fun saveLikedImage(item: LikedImageCached)
}