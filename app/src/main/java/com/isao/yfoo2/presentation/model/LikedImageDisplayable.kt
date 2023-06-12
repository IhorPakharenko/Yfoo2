package com.isao.yfoo2.presentation.model

import android.os.Parcelable
import com.isao.yfoo2.domain.model.ImageSource
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class LikedImageDisplayable(
    val id: String,
    val source: ImageSource,
    val imageUrl: String,
    val sourceUrl: String,
    val dateAdded: LocalDateTime
) : Parcelable