package com.isao.yfoo2.presentation.feed

import com.isao.yfoo2.presentation.feed.model.FeedItemDisplayable

sealed class FeedPartialState {

    object ItemsLoading : FeedPartialState()
    data class ItemsFetched(val items: List<FeedItemDisplayable>) : FeedPartialState()

    data class Error(val throwable: Throwable) : FeedPartialState()

    data class ItemDismissed(val item: FeedItemDisplayable) : FeedPartialState()
}