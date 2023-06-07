package com.isao.yfoo2.domain.usecase

import com.isao.yfoo2.core.utils.resultOf
import com.isao.yfoo2.data.local.mapper.toLikedImage
import com.isao.yfoo2.domain.repository.FeedImageRepository
import com.isao.yfoo2.domain.repository.LikedImageRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LikeImageUseCase @Inject constructor(
    private val feedImageRepository: FeedImageRepository,
    private val likedImageRepository: LikedImageRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return resultOf {
            val feedImage = feedImageRepository.getImage(id).first()
            likedImageRepository.saveImage(feedImage.toLikedImage())
            feedImageRepository.deleteImage(feedImage.id)
        }
    }
}