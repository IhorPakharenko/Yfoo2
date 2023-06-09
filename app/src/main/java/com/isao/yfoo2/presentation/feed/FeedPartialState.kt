package com.isao.yfoo2.presentation.feed

import com.isao.yfoo2.presentation.model.FeedItemDisplayable

sealed class FeedPartialState {

    data class ErrorSavingItem(val throwable: Throwable) : FeedPartialState()

    data class ErrorDeletingItem(val throwable: Throwable) : FeedPartialState()

    data class ItemsFetched(val items: List<FeedItemDisplayable>) : FeedPartialState()

    data class ErrorFetchingItem(val throwable: Throwable) : FeedPartialState()

    data class ItemDismissed(val item: FeedItemDisplayable) : FeedPartialState()
}