package com.isao.yfoo2.data.local.mapper

import com.isao.yfoo2.data.local.model.LikedImageCached
import com.isao.yfoo2.domain.model.LikedImage

fun LikedImageCached.toDomainModel() = LikedImage(
    id = id,
    source = source,
    dateAdded = dateAdded
)

fun LikedImage.toEntityModel() = LikedImageCached(
    id = id,
    source = source,
    dateAdded = dateAdded
)