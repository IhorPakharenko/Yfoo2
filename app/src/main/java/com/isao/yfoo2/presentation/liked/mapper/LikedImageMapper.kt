package com.isao.yfoo2.presentation.liked.mapper

import com.isao.yfoo2.domain.model.LikedImage
import com.isao.yfoo2.presentation.liked.model.LikedImageDisplayable

fun LikedImage.toPresentationModel() = LikedImageDisplayable(
    id = id,
    imageUrl = source.getImageUrl(imageId),
    source = source
)