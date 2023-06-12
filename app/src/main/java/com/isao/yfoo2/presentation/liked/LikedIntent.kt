package com.isao.yfoo2.presentation.liked

import com.isao.yfoo2.presentation.model.LikedImageDisplayable

sealed class LikedIntent {

    object GetImages : LikedIntent()

    data class ImageClicked(val item: LikedImageDisplayable) : LikedIntent()

    data class ImageLongClicked(val item: LikedImageDisplayable) : LikedIntent()
}