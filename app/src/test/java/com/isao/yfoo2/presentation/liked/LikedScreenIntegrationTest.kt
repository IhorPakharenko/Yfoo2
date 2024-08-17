package com.isao.yfoo2.presentation.liked

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.isao.yfoo2.core.MainActivity
import com.isao.yfoo2.core.di.appModule
import com.isao.yfoo2.core.utils.KoinRule
import com.isao.yfoo2.testsModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LikedScreenIntegrationTest {
    @get:Rule
    val testRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val koinRule = KoinRule(appModule + testsModule)

    @Test
    fun `happy path`() {
//        testRule.setContent {
//            LikedRoute()
//        }
//        testRule.
    }
}