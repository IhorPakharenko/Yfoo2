package com.isao.yfoo2.presentation.liked

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
//TODO or @RunWith(AndroidJUnit4::class)
class LikedScreenTestRobolectric2 {
    //    @get:Rule
//    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test() {
        val app = getApplicationContext<Application>()
        app.packageName shouldBe "a"
    }
}