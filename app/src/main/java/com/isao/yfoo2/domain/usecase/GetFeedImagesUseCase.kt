package com.isao.yfoo2.domain.usecase

import com.isao.yfoo2.domain.model.FeedImage
import com.isao.yfoo2.domain.repository.FeedImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFeedImagesUseCase @Inject constructor(
    private val feedImageRepository: FeedImageRepository
) {
    operator fun invoke(): Flow<Result<List<FeedImage>>> {
        return feedImageRepository
            .getImages()
            .map { Result.success(it) }
            .catch {
                emit(Result.failure(it))
            }
    }
}