package com.isao.yfoo2

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.isao.yfoo2.core.theme.Yfoo2Theme
import com.isao.yfoo2.core.utils.PreviewMobileScreenSizes
import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.presentation.liked.LikedUiState
import com.isao.yfoo2.presentation.liked.composable.LikedScreen
import com.isao.yfoo2.presentation.liked.model.LikedImageDisplayable

@SuppressLint("ComposePreviewPublic") //TODO turn this check off for this source set
class LikedScreenScreenshotTest {

    @PreviewMobileScreenSizes
    @Composable
    fun LoadingState() {
        Yfoo2Theme {
            LikedScreen(
                uiState = LikedUiState(isLoading = true),
                onIntent = {}
            )
        }
    }

    @PreviewMobileScreenSizes
    @Composable
    fun ErrorState() {
        Yfoo2Theme {
            LikedScreen(
                uiState = LikedUiState(isError = true),
                onIntent = {}
            )
        }
    }

    @PreviewMobileScreenSizes
    @Composable
    fun EmptyState() {
        Yfoo2Theme {
            LikedScreen(
                uiState = LikedUiState(),
                onIntent = {}
            )
        }
    }

    @ScreenshotTestPreviews
    @Composable
    fun PopulatedState() {
        Yfoo2Theme {
            LikedScreen(
                uiState = LikedUiState(
                    items = List(20) {
                        LikedImageDisplayable(
                            id = "$it",
                            imageUrl = "$it",
                            source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST
                        )
                    }
                ),
                onIntent = {}
            )
        }
    }

    @ScreenshotTestPreviews
    @Composable
    fun SortAscendingState() {
        Yfoo2Theme {
            LikedScreen(
                uiState = LikedUiState(
                    items = List(20) {
                        LikedImageDisplayable(
                            id = "$it",
                            imageUrl = "$it",
                            source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST
                        )
                    },
                    shouldSortAscending = true
                ),
                onIntent = {}
            )
        }
    }
}