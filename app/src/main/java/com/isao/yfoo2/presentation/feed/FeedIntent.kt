package com.isao.yfoo2.presentation.feed

sealed class FeedIntent {
    data class Like(val id: String) : FeedIntent()
    data class Dislike(val id: String) : FeedIntent()
}