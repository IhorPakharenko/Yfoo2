package com.isao.yfoo2.presentation.liked

import com.isao.yfoo2.presentation.model.LikedImageDisplayable

sealed class LikedPartialState {

    data class ItemsFetched(val items: List<LikedImageDisplayable>)

    data class Error(val throwable: Throwable) : LikedPartialState()

    object Loading : LikedPartialState()
}