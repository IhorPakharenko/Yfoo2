package com.isao.yfoo2.presentation.mapper

import com.isao.yfoo2.domain.model.LikedImage
import com.isao.yfoo2.presentation.model.LikedImageDisplayable

fun LikedImage.toPresentationModel() = LikedImageDisplayable(
    id = id,
    source = source,
    imageUrl = source.getImageUrl(id),
    sourceUrl = source.websiteUrl,
    dateAdded = dateAdded
)