package com.isao.yfoo2.presentation.liked

import android.app.Application
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import br.com.colman.kotest.android.extensions.robolectric.RobolectricTest
import de.mannodermaus.junit5.compose.createComposeExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalTestApi::class)
@RobolectricTest(sdk = 21)
class LikedScreenTestRobolectricKotest : FunSpec() {

//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

//    @get:Rule
//    val composeTestRule = createComposeRule()

    @Suppress("JUnitMalformedDeclaration")
    @JvmField
    @RegisterExtension
    val extension = createComposeExtension()

    init {
        test("testkotest") {
            extension.use {
                this.density.toString()
            }
//            composeTestRule.setContent { Text("A") }
            val app = getApplicationContext<Application>()
            app.packageName shouldBe "a"
        }
    }
}