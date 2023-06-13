package com.isao.yfoo2.presentation.feed.model

import android.os.Parcelable
import com.isao.yfoo2.domain.model.ImageSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedItemDisplayable(
    val id: String,
    val imageId: String,
    val source: ImageSource,
    val imageUrl: String,
    val sourceUrl: String,
    val isDismissed: Boolean
) : Parcelable