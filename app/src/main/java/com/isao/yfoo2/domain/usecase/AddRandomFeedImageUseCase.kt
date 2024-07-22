package com.isao.yfoo2.domain.usecase

import com.isao.yfoo2.core.extensions.resultOf
import com.isao.yfoo2.domain.repository.FeedImageRepository
import org.koin.core.annotation.Factory

@Factory
class AddRandomFeedImageUseCase(
    private val feedImageRepository: FeedImageRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return resultOf {
            feedImageRepository.addRandomFeedImage()
        }
    }
}
