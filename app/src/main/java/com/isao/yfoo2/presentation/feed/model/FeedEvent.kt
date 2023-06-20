package com.isao.yfoo2.presentation.feed.model

import com.isao.yfoo2.core.utils.UiText

sealed class FeedEvent {
    data class ShowSnackbar(val text: UiText) : FeedEvent()
}