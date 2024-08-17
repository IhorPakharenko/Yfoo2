package com.isao.yfoo2.core.utils

import android.annotation.SuppressLint
import androidx.compose.ui.tooling.preview.Devices.FOLDABLE
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview

/**
 * A MultiPreview annotation for displaying a @[Composable] method using the screen sizes of four different reference devices.
 */
@SuppressLint("ComposePreviewNaming") //TODO turn this check off, the rules have changed
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(name = "Phone", device = PHONE, showSystemUi = true)
@Preview(
    name = "Phone - Landscape",
    device = "spec:width = 411dp, height = 891dp, orientation = landscape, dpi = 420",
    showSystemUi = true
)
@Preview(name = "Unfolded Foldable", device = FOLDABLE, showSystemUi = true)
@Preview(name = "Tablet", device = TABLET, showSystemUi = true)
annotation class PreviewMobileScreenSizes