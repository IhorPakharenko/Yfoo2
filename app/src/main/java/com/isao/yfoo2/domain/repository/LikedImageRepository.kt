package com.isao.yfoo2.domain.repository

import com.isao.yfoo2.domain.model.LikedImage
import kotlinx.coroutines.flow.Flow

interface LikedImageRepository {
    suspend fun getImages(): Flow<List<LikedImage>>

    suspend fun saveImage(item: LikedImage)

    suspend fun deleteImage(id: String)
}