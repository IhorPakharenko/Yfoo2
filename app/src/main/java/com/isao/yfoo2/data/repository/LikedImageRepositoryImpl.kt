package com.isao.yfoo2.data.repository

import com.isao.yfoo2.data.local.dao.LikedImageDao
import com.isao.yfoo2.data.local.mapper.toDomainModel
import com.isao.yfoo2.data.local.mapper.toEntityModel
import com.isao.yfoo2.domain.model.LikedImage
import com.isao.yfoo2.domain.repository.LikedImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LikedImageRepositoryImpl @Inject constructor(
    private val likedImageDao: LikedImageDao,
) : LikedImageRepository {

    override suspend fun getImages(): Flow<List<LikedImage>> {
        return likedImageDao.getLikedImages().map { imagesCached ->
            imagesCached.map { it.toDomainModel() }
        }
    }

    override suspend fun saveImage(item: LikedImage) {
        likedImageDao.saveLikedImage(item.toEntityModel())
    }

    override suspend fun deleteImage(id: String) {
        TODO("Not yet implemented")
    }
}