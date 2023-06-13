package com.isao.yfoo2.presentation.liked

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class LikedUiState(
    val items: List<LikedImageDisplayable>,
    val shouldSortAscending: Boolean,
    val isLoading: Boolean,
    val isError: Boolean
) : Parcelable