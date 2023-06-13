package com.isao.yfoo2.presentation.liked

import com.isao.yfoo2.presentation.model.LikedImageDisplayable

sealed class LikedIntent {

    data class SetSorting(val shouldSortAscending: Boolean) : LikedIntent()

    data class ImageClicked(val item: LikedImageDisplayable) : LikedIntent()

    data class ImageLongClicked(val item: LikedImageDisplayable) : LikedIntent()
}