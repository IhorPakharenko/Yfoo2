package com.isao.yfoo2.presentation

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import com.isao.yfoo2.R
import com.isao.yfoo2.core.MainActivity
import com.isao.yfoo2.core.di.appModule
import com.isao.yfoo2.presentation.core.utils.contextModule
import com.isao.yfoo2.utils.KoinRule
import com.isao.yfoo2.utils.getString
import com.isao.yfoo2.utils.testsModule
import org.junit.Rule
import org.junit.Test
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NavigationTest : KoinTest {
    @get:Rule(order = 0)
    val koinRule = KoinRule(appModule + defaultModule + testsModule + contextModule)

    @get:Rule(order = 1)
    val testRule = createAndroidComposeRule<MainActivity>()

    @Test(expected = NoActivityResumedException::class)
    fun givenHomeDestinationAndEmptyBackstack_whenBackPressed_quitApp() {
        testRule.onNodeWithText(getString(R.string.feed)).assertIsSelected()
        Espresso.pressBack()
    }

    @Test(expected = NoActivityResumedException::class)
    fun givenHomeDestinationAndNonEmptyBackstack_whenBackPressed_quitApp() {
        testRule.onNodeWithText(getString(R.string.feed)).assertIsSelected()
        testRule.onNodeWithText(getString(R.string.liked)).performClick()
        testRule.onNodeWithText(getString(R.string.liked)).assertIsSelected()
        testRule.onNodeWithText(getString(R.string.feed)).performClick()
        Espresso.pressBack()
    }


    @Test(expected = NoActivityResumedException::class)
    fun givenOtherDestinationAndNonEmptyBackstack_whenBackPressedMultipleTimes_quitApp() {
        testRule.onNodeWithText(getString(R.string.feed)).assertIsSelected()
        testRule.onNodeWithText(getString(R.string.liked)).performClick()
        testRule.onNodeWithText(getString(R.string.liked)).assertIsSelected()
        Espresso.pressBack()
        Espresso.pressBack()
    }
}