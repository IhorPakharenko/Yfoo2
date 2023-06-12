package com.isao.yfoo2.presentation.feed.mapper

import com.isao.yfoo2.domain.model.FeedImage
import com.isao.yfoo2.presentation.feed.model.FeedItemDisplayable

fun FeedImage.toPresentationModel() = FeedItemDisplayable(
    id = id,
    source = source,
    imageUrl = source.getImageUrl(id),
    sourceUrl = source.websiteUrl,
    isDismissed = false
)