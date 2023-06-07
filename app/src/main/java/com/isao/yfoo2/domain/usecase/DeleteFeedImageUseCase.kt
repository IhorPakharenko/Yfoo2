package com.isao.yfoo2.domain.usecase

import com.isao.yfoo2.core.utils.resultOf
import com.isao.yfoo2.domain.repository.FeedImageRepository
import javax.inject.Inject

class DeleteFeedImageUseCase @Inject constructor(
    private val feedImageRepository: FeedImageRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return resultOf {
            feedImageRepository.deleteImage(id)
        }
    }
}