package com.isao.yfoo2.presentation.feed

import androidx.lifecycle.SavedStateHandle
import com.isao.yfoo2.core.MviViewModel
import com.isao.yfoo2.domain.usecase.DeleteFeedImageUseCase
import com.isao.yfoo2.domain.usecase.GetFeedImagesUseCase
import com.isao.yfoo2.domain.usecase.LikeImageUseCase
import com.isao.yfoo2.presentation.feed.mapper.toPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val likeImageUseCase: LikeImageUseCase,
    private val deleteFeedImageUseCase: DeleteFeedImageUseCase,
    private val getFeedImagesUseCase: GetFeedImagesUseCase,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<FeedUiState, FeedPartialState, Nothing, FeedIntent>(
    savedStateHandle,
    FeedUiState()
) {
    init {
        observeContinuousChanges(getItems())
    }

    override fun mapIntents(intent: FeedIntent): Flow<FeedPartialState> = when (intent) {
        is FeedIntent.Like -> likeItem(intent.id)
        is FeedIntent.Dislike -> dislikeItem(intent.id)
    }

    override fun reduceUiState(
        previousState: FeedUiState,
        partialState: FeedPartialState
    ): FeedUiState = when (partialState) {
        is FeedPartialState.ItemsFetched -> previousState.copy(
            items = (previousState.items + partialState.items).distinctBy { it.id },
            isLoading = false,
            isError = false
        )

        FeedPartialState.ItemsLoading -> previousState.copy(
            isLoading = true
        )

        is FeedPartialState.ItemDismissed -> previousState.copy(
            items = previousState.items.map { item ->
                if (item.id == partialState.item.id) {
                    item.copy(isDismissed = true)
                } else {
                    item
                }
            },
            isLoading = false,
            isError = false
        )

        is FeedPartialState.Error -> previousState.copy(
            isError = true,
        )
    }

    private fun getItems(): Flow<FeedPartialState> =
        getFeedImagesUseCase()
            .map { result ->
                result.fold(
                    onSuccess = { items ->
                        FeedPartialState.ItemsFetched(
                            items.map { it.toPresentationModel() }
                        )
                    },
                    onFailure = {
                        FeedPartialState.Error(it)
                    }
                )
            }
            .onStart {
                emit(FeedPartialState.ItemsLoading)
            }

    private fun likeItem(id: String): Flow<FeedPartialState> = flow {
        val item = uiState.value.items.first { it.id == id }
        emit(FeedPartialState.ItemDismissed(item))
        likeImageUseCase(id)
            .onFailure {
                emit(FeedPartialState.Error(it))
            }
    }

    private fun dislikeItem(id: String): Flow<FeedPartialState> = flow {
        val item = uiState.value.items.first { it.id == id }
        emit(FeedPartialState.ItemDismissed(item))
        deleteFeedImageUseCase(id)
            .onFailure {
                emit(FeedPartialState.Error(it))
            }
    }
}