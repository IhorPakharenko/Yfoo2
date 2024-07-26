package com.isao.yfoo2.data.testdoubles

import com.isao.yfoo2.data.local.dao.FeedImageDao
import com.isao.yfoo2.data.local.model.FeedImageCached
import kotlinx.coroutines.flow.Flow

class FakeFeedImageDao : FeedImageDao {
    override fun getFeedImages(): Flow<List<FeedImageCached>> {
        TODO("Not yet implemented")
    }

    override fun getFeedImage(id: String): Flow<FeedImageCached> {
        TODO("Not yet implemented")
    }

    override suspend fun saveFeedImage(item: FeedImageCached): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFeedImage(id: String) {
        TODO("Not yet implemented")
    }
}