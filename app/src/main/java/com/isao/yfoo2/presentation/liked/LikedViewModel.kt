package com.isao.yfoo2.presentation.liked

import androidx.lifecycle.SavedStateHandle
import com.isao.yfoo2.core.MviViewModel
import com.isao.yfoo2.domain.usecase.DeleteLikedImageUseCase
import com.isao.yfoo2.domain.usecase.GetLikedImagesUseCase
import com.isao.yfoo2.presentation.mapper.toPresentationModel
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LikedViewModel @Inject constructor(
    private val getLikedImagesUseCase: GetLikedImagesUseCase,
    private val deleteLikedImageUseCase: DeleteLikedImageUseCase,
    savedStateHandle: SavedStateHandle,
) : MviViewModel<LikedUiState, LikedPartialState, LikedEvent, LikedIntent>(
    savedStateHandle,
    LikedUiState(
        items = emptyList(),
        shouldSortAscending = false,
        isLoading = false,
        isError = false
    )
) {

    init {
        observeContinuousChanges(
            uiState
                .map { it.shouldSortAscending }
                .distinctUntilChanged()
                .flatMapLatest { sortAscending ->
                    getImages(sortAscending)
                }
        )
    }

    override fun mapIntents(intent: LikedIntent): Flow<LikedPartialState> = when (intent) {
        is LikedIntent.SetSorting -> flowOf(LikedPartialState.Sorted(shouldSortAscending = intent.shouldSortAscending))
        is LikedIntent.ImageClicked -> itemClicked(intent.item)
        is LikedIntent.DeleteImageClicked -> deleteImageClicked(intent.item)
        is LikedIntent.ViewImageSourceClicked -> viewImageSourceClicked(intent.item)
    }

    override fun reduceUiState(
        previousState: LikedUiState,
        partialState: LikedPartialState
    ): LikedUiState = when (partialState) {
        LikedPartialState.Loading -> previousState.copy(
            isLoading = true,
            isError = false,
        )

        is LikedPartialState.Fetched -> previousState.copy(
            items = partialState.items,
            isLoading = false,
            isError = false,
        )

        is LikedPartialState.Error -> previousState.copy(
            isLoading = false,
            isError = true,
        )

        is LikedPartialState.Sorted -> previousState.copy(
            shouldSortAscending = partialState.shouldSortAscending
        )
    }

    private fun getImages(
        sortAscending: Boolean,
    ): Flow<LikedPartialState> = getLikedImagesUseCase(
        shouldSortAscending = sortAscending,
        limit = -1,
        offset = 0
    )
        .map { result ->
            result.fold(
                onSuccess = { items ->
                    LikedPartialState.Fetched(items.map { it.toPresentationModel() })
                },
                onFailure = {
                    LikedPartialState.Error(it)
                }
            )
        }
        .onStart {
            emit(LikedPartialState.Loading)
        }

    private fun itemClicked(item: LikedImageDisplayable): Flow<LikedPartialState> {
        publishEvent(LikedEvent.OpenWebBrowser(item.imageUrl))
        return emptyFlow()
    }

    private fun deleteImageClicked(item: LikedImageDisplayable): Flow<LikedPartialState> = flow {
        deleteLikedImageUseCase(item.id)
    }

    private fun viewImageSourceClicked(item: LikedImageDisplayable): Flow<LikedPartialState> {
        publishEvent(LikedEvent.OpenWebBrowser(item.source.websiteUrl))
        return emptyFlow()
    }
}