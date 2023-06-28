package com.isao.yfoo2.presentation.mapper

import com.isao.yfoo2.domain.model.LikedImage
import com.isao.yfoo2.presentation.model.LikedImageDisplayable

fun LikedImage.toPresentationModel() = LikedImageDisplayable(
    id = id,
    imageUrl = source.getImageUrl(imageId),
    source = source
)