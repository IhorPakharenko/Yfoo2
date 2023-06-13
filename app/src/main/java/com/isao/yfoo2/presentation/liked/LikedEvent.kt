package com.isao.yfoo2.presentation.liked

sealed class LikedEvent {

    data class OpenWebBrowser(val uri: String) : LikedEvent()
}