package com.isao.yfoo2.presentation.liked

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import br.com.colman.kotest.android.extensions.robolectric.RobolectricTest
import com.isao.yfoo2.core.utils.MainDispatcherExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.junit.Rule

/**
 * The test is launched on Junit5 first, then it is being run on Junit4
 * using @RobolectricTest library. Rules don't work under this setup. A Junit5 extension is required.
 */
@RobolectricTest(sdk = Build.VERSION_CODES.O)
class FailingRobolectricTest : StringSpec() {
    override fun extensions(): List<Extension> = listOf(MainDispatcherExtension())

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    init {
        "Get the Build.VERSION_CODES.O" {
            Build.VERSION.SDK_INT shouldBe Build.VERSION_CODES.O
        }
    }
}