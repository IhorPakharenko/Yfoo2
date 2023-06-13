package com.isao.yfoo2.data.local.mapper

import com.isao.yfoo2.data.local.model.FeedImageCached
import com.isao.yfoo2.domain.model.FeedImage
import com.isao.yfoo2.domain.model.LikedImage
import java.time.Instant

fun FeedImageCached.toDomainModel() = FeedImage(
    id = id,
    imageId = imageId,
    source = source
)

fun FeedImage.toEntityModel() = FeedImageCached(
    id = id,
    imageId = imageId,
    source = source,
)

fun FeedImage.toLikedImage() = LikedImage(
    id = id,
    imageId = imageId,
    source = source,
    dateAdded = Instant.now()
)