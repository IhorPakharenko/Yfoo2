package com.isao.yfoo2

import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.isao.yfoo2.core.utils.PreviewMobileScreenSizes

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@PreviewMobileScreenSizes
@PreviewLightDark
@PreviewDynamicColors
annotation class ScreenshotTestPreviews