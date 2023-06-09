package com.isao.yfoo2.presentation.mapper

import com.isao.yfoo2.domain.model.FeedImage
import com.isao.yfoo2.presentation.model.FeedItemDisplayable

fun FeedImage.toPresentationModel() = FeedItemDisplayable(
    id = id,
    source = source,
    imageUrl = source.getImageUrl(id),
    sourceUrl = source.websiteUrl,
    isDismissed = false
)