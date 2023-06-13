package com.isao.yfoo2.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LikedImageDisplayable(
    val id: String,
    val imageUrl: String,
    val sourceUrl: String
) : Parcelable