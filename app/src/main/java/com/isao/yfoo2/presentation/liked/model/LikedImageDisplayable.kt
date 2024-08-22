package com.isao.yfoo2.presentation.liked.model

import android.os.Parcelable
import com.isao.yfoo2.domain.model.ImageSource
import kotlinx.parcelize.Parcelize

@Parcelize
data class LikedImageDisplayable(
    val id: String,
    val imageUrl: String,
    val source: ImageSource,
) : Parcelable