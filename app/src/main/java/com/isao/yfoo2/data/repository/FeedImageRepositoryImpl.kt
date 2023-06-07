package com.isao.yfoo2.data.repository

import com.isao.yfoo2.data.local.dao.FeedImageDao
import com.isao.yfoo2.data.local.mapper.toDomainModel
import com.isao.yfoo2.data.local.mapper.toEntityModel
import com.isao.yfoo2.domain.model.FeedImage
import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.domain.repository.FeedImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.random.Random

class FeedImageRepositoryImpl @Inject constructor(
    private val feedImageDao: FeedImageDao,
) : FeedImageRepository {

    override fun getImages(): Flow<List<FeedImage>> {
        return feedImageDao.getFeedImages().map { imagesCached ->
            imagesCached.map { it.toDomainModel() }
        }
    }

    override fun getImage(id: String): Flow<FeedImage> {
        return feedImageDao.getFeedImage(id).map { it.toDomainModel() }
    }

    override suspend fun addRandomFeedImage() {
        feedImageDao.saveFeedImage(
            FeedImage(
                id = Random.nextInt(100000 + 1).toString(),
                source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST
            ).toEntityModel()
        )
    }

    override suspend fun deleteImage(id: String) {
        feedImageDao.deleteFeedImage(id)
    }
}