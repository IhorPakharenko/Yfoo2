package com.isao.yfoo2.presentation

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.isao.yfoo2.R
import com.isao.yfoo2.core.MainActivity
import com.isao.yfoo2.core.di.appModule
import com.isao.yfoo2.utils.KoinRule
import com.isao.yfoo2.utils.getString
import com.isao.yfoo2.utils.testsModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.ksp.generated.defaultModule
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NavigationTest {
    @get:Rule
    val testRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val koinRule = KoinRule(appModule + defaultModule + testsModule)

    @Test
    fun `first screen is Feed`() {
        testRule.onNodeWithText(getString(R.string.feed)).assertIsSelected()
    }
}
