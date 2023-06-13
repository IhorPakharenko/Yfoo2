package com.isao.yfoo2.data.local.mapper

import com.isao.yfoo2.data.local.model.FeedImageCached
import com.isao.yfoo2.domain.model.FeedImage
import com.isao.yfoo2.domain.model.LikedImage
import java.time.Instant

fun FeedImageCached.toDomainModel() = FeedImage(
    id = id,
    source = source
)

fun FeedImage.toEntityModel() = FeedImageCached(
    id = id,
    source = source,
)

fun FeedImage.toLikedImage() = LikedImage(
    id = id,
    source = source,
    dateAdded = Instant.now()
)