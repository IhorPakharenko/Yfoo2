package com.isao.yfoo2.core.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that represents various device sizes. Add this annotation to a composable
 * to render various devices.
 */
@Preview(name = "PHONE", device = Devices.PHONE, showBackground = true)
@Preview(
    name = "PHONE",
    device = Devices.PHONE,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class DevicePreviews