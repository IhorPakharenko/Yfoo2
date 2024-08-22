package com.isao.yfoo2.presentation.liked.model

import com.isao.yfoo2.domain.model.ImageSource

object LikedImageDisplayableDummies {
    fun generateLikedImageDisplayables(size: Int) = List(size) { index ->
        LikedImageDisplayable(
            id = "${index}",
            imageUrl = "https://example.com/${index}",
            source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST
        )
    }
}