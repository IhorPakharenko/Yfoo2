package com.isao.yfoo2.presentation.liked

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class LikedUiState(
    val items: List<LikedImageDisplayable> = emptyList(),
    val shouldSortAscending: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false
) : Parcelable