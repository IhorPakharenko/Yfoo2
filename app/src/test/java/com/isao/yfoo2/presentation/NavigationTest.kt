package com.isao.yfoo2.presentation

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.isao.yfoo2.R
import com.isao.yfoo2.core.MainActivity
import com.isao.yfoo2.core.di.appModule
import com.isao.yfoo2.core.utils.KoinRule
import com.isao.yfoo2.core.utils.getString
import com.isao.yfoo2.testsModule
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

//    @Test(expected = NoActivityResumedException::class)
//    fun `given home destination and non-empty backstack, when back pressed, quit app`() {
//        testRule.onNodeWithText(getString(R.string.feed)).assertIsSelected()
//        testRule.onNodeWithText(getString(R.string.liked)).performClick()
//        testRule.onNodeWithText(getString(R.string.liked)).assertIsSelected()
//        testRule.onNodeWithText(getString(R.string.feed)).performClick()
//        Espresso.pressBack()
//    }
}
