package com.isao.yfoo2.presentation.liked

import androidx.compose.runtime.Immutable
import com.isao.yfoo2.presentation.liked.model.LikedImageDisplayable

@Immutable
data class LikedUiState(
    val items: List<LikedImageDisplayable> = emptyList(),
    val shouldSortAscending: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false
)