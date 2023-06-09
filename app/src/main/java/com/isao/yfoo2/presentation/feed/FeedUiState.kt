package com.isao.yfoo2.presentation.feed

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.isao.yfoo2.presentation.model.FeedItemDisplayable
import kotlinx.parcelize.Parcelize

@Immutable
@Parcelize
data class FeedUiState(
    val items: List<FeedItemDisplayable>,
) : Parcelable