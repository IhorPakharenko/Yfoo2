package com.isao.yfoo2.domain.usecase

import com.isao.yfoo2.domain.model.LikedImage
import com.isao.yfoo2.domain.repository.LikedImageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLikedImagesUseCase @Inject constructor(
    private val likedImageRepository: LikedImageRepository
) {
    operator fun invoke(
        shouldSortAscending: Boolean,
        limit: Int,
        offset: Int
    ): Flow<Result<List<LikedImage>>> {
        return likedImageRepository
            .getImages(
                shouldSortAscending = shouldSortAscending,
                limit = limit,
                offset = offset
            )
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }
}