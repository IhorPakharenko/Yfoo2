package com.isao.yfoo2.data.local.mapper

import com.isao.yfoo2.data.local.model.LikedImageCached
import com.isao.yfoo2.domain.model.LikedImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LikedImageCached.toDomainModel() = LikedImage(
    id = id,
    source = source,
    dateAdded = LocalDateTime.parse(dateAdded)
)

fun LikedImage.toEntityModel() = LikedImageCached(
    id = id,
    source = source,
    dateAdded = dateAdded.format(DateTimeFormatter.ISO_LOCAL_DATE)
)