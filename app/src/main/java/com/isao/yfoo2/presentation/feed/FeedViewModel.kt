package com.isao.yfoo2.presentation.feed

import androidx.lifecycle.SavedStateHandle
import com.isao.yfoo2.core.BaseViewModel
import com.isao.yfoo2.domain.usecase.AddRandomFeedImageUseCase
import com.isao.yfoo2.domain.usecase.GetFeedImagesUseCase
import com.isao.yfoo2.domain.usecase.LikeImageUseCase
import com.isao.yfoo2.presentation.mapper.toPresentationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val MIN_ITEM_COUNT = 20

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val likeImageUseCase: LikeImageUseCase,
    private val addRandomFeedImageUseCase: AddRandomFeedImageUseCase,
    getFeedImagesUseCase: GetFeedImagesUseCase,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<FeedUiState, FeedPartialState, Nothing, FeedIntent>(
    savedStateHandle,
    FeedUiState(items = emptyList(), dismissedItems = emptyList())
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
        is FeedPartialState.SavingItem -> previousState.copy(
            items = previousState.items - partialState.item,
            dismissedItems = previousState.dismissedItems + partialState.item
        )

        is FeedPartialState.ItemSaved -> previousState
        is FeedPartialState.ErrorSavingItem -> previousState
        //TODO bugs when multiple items are the equal
        is FeedPartialState.ItemDismissed -> previousState.copy(
            items = previousState.items - partialState.item,
            dismissedItems = previousState.dismissedItems + partialState.item
        )

        is FeedPartialState.ItemsFetched -> previousState.copy(
            items = previousState.items + partialState.items
        )

        is FeedPartialState.ErrorFetchingItem -> previousState
    }

    private fun likeItem(id: String): Flow<FeedPartialState> = flow {
        val item = uiState.value.items.first { it.id == id }
        emit(FeedPartialState.SavingItem(item))
        likeImageUseCase(id)
            .onSuccess {
                emit(FeedPartialState.ItemSaved(item))
            }
            .onFailure {
                //TODO show error
                emit(FeedPartialState.ErrorSavingItem(it))
            }
    }

    private fun dislikeItem(id: String): Flow<FeedPartialState> {
        val item = uiState.value.items.first { it.id == id }
        return flowOf(FeedPartialState.ItemDismissed(item))
    }
}