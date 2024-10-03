package com.isao.yfoo2.presentation.liked

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.isao.yfoo2.presentation.liked.composable.LikedScreen
import com.isao.yfoo2.utils.KoinRule
import com.isao.yfoo2.utils.printSemantics
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w800dp-h1500dp-xxxhdpi")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LikedScreenTest {
    @get:Rule
    val testRule = createComposeRule()

    @get:Rule
    val koinRule = KoinRule(emptyList())

    private val intents = mutableListOf<LikedIntent>()

    @Test
    fun `1`() {
        testRule.setContent {
            LazyColumn(Modifier.fillMaxSize()) {
                items(10) {
                    Text("Text number $it")
                }
            }
        }
        testRule.printSemantics()
    }

    @Test
    fun `2`() {
        testRule.setContent {
            LazyColumn(Modifier.fillMaxSize()) {
                items(10) {
                    Text("Text number $it")
                }
            }
        }
        testRule.printSemantics()
    }

    @Test
    fun `3`() {
        testRule.setContent {
            LazyColumn(Modifier.fillMaxSize()) {
                items(10) {
                    Text("Text number $it")
                }
            }
        }
        testRule.printSemantics()
    }

    private fun ComposeContentTestRule.setUpComposable(
        state: LikedUiState = LikedUiState(),
        onIntent: (LikedIntent) -> Unit = {}
    ) = setContent {
        LikedScreen(
            uiState = state,
            onIntent = {
                intents += it
                onIntent(it)
            }
        )
    }
}