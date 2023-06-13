package com.isao.yfoo2.domain.model

import java.time.Instant

data class LikedImage(
    val id: String,
    val source: ImageSource,
    val dateAdded: Instant
)