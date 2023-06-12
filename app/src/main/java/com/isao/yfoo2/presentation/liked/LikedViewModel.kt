package com.isao.yfoo2.presentation.liked

import androidx.lifecycle.SavedStateHandle
import com.isao.yfoo2.core.BaseViewModel
import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class LikedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<LikedUiState, LikedPartialState, Nothing, LikedIntent>(
    savedStateHandle,
    LikedUiState(
        items = List(50) {
            LikedImageDisplayable(
                id = it.toString(),
                source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST,
                imageUrl = "https://www.thiswaifudoesnotexist.net/example-${it}.jpg",
                sourceUrl = "https://www.thiswaifudoesnotexist.net/example-18592.jpg",
                dateAdded = LocalDateTime.now()
            )
        },
        sortAscending = false
    )
) {
    override fun mapIntents(intent: LikedIntent): Flow<LikedPartialState> {
        TODO("Not yet implemented")
    }

    override fun reduceUiState(
        previousState: LikedUiState,
        partialState: LikedPartialState
    ): LikedUiState {
        TODO("Not yet implemented")
    }
}