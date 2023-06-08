package com.isao.yfoo2.domain.usecase

import com.isao.yfoo2.domain.model.FeedImage
import com.isao.yfoo2.domain.repository.FeedImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val MIN_ITEM_COUNT = 20

class GetFeedImagesUseCase @Inject constructor(
    private val feedImageRepository: FeedImageRepository
) {
    //TODO the list grows endlessly due to the onEach block and race conditions
    operator fun invoke(): Flow<Result<List<FeedImage>>> {
        return feedImageRepository
            .getImages()
            .onEach { items ->
                val itemsToFetchCount = MIN_ITEM_COUNT - items.size
                repeat(itemsToFetchCount) {
                    feedImageRepository.addRandomFeedImage()
                }
            }
            .map { Result.success(it) }
            .catch {
                emit(Result.failure(it))
            }
    }
}