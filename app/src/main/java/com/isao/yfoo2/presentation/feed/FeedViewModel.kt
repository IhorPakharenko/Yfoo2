package com.isao.yfoo2.presentation.feed

import androidx.lifecycle.SavedStateHandle
import com.isao.yfoo2.core.BaseViewModel
import com.isao.yfoo2.domain.usecase.DeleteFeedImageUseCase
import com.isao.yfoo2.domain.usecase.GetFeedImagesUseCase
import com.isao.yfoo2.domain.usecase.LikeImageUseCase
import com.isao.yfoo2.presentation.feed.mapper.toPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val likeImageUseCase: LikeImageUseCase,
    private val deleteFeedImageUseCase: DeleteFeedImageUseCase,
    getFeedImagesUseCase: GetFeedImagesUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<FeedUiState, FeedPartialState, Nothing, FeedIntent>(
    savedStateHandle,
    FeedUiState(items = emptyList())
) {
    init {
        observeContinuousChanges(
            getFeedImagesUseCase().map { result ->
                result.fold(
                    onSuccess = { items ->
                        FeedPartialState.ItemsFetched(
                            items.map { it.toPresentationModel() }
                        )
                    },
                    onFailure = {
                        FeedPartialState.ErrorFetchingItem(it)
                    }
                )
            }
        )
    }

    override fun mapIntents(intent: FeedIntent): Flow<FeedPartialState> = when (intent) {
        is FeedIntent.Like -> likeItem(intent.id)
        is FeedIntent.Dislike -> dislikeItem(intent.id)
    }

    override fun reduceUiState(
        previousState: FeedUiState,
        partialState: FeedPartialState
    ): FeedUiState = when (partialState) {
        //TODO are these partial states needed if they don't change the state?
        is FeedPartialState.ErrorSavingItem -> previousState
        is FeedPartialState.ErrorDeletingItem -> previousState
        is FeedPartialState.ItemDismissed -> previousState.copy(
            items = previousState.items.map { item ->
                if (item.id == partialState.item.id) {
                    item.copy(isDismissed = true)
                } else {
                    item
                }
            },
        )

        is FeedPartialState.ItemsFetched -> previousState.copy(
            items = (previousState.items + partialState.items).distinctBy { it.id }
        )

        is FeedPartialState.ErrorFetchingItem -> previousState
    }

    private fun likeItem(id: String): Flow<FeedPartialState> = flow {
        val item = uiState.value.items.first { it.id == id }
        emit(FeedPartialState.ItemDismissed(item))
        likeImageUseCase(id)
            .onFailure {
                //TODO show error
                emit(FeedPartialState.ErrorSavingItem(it))
            }
    }

    private fun dislikeItem(id: String): Flow<FeedPartialState> = flow {
        val item = uiState.value.items.first { it.id == id }
        emit(FeedPartialState.ItemDismissed(item))
        deleteFeedImageUseCase(id)
            .onFailure {
                //TODO show error
                emit(FeedPartialState.ErrorDeletingItem(it))
            }
    }
}