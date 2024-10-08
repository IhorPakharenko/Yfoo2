package com.isao.yfoo2.presentation.liked

import androidx.compose.ui.test.assertAny
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import com.isao.yfoo2.R
import com.isao.yfoo2.presentation.liked.composable.LikedScreen
import com.isao.yfoo2.presentation.liked.model.LikedImageDisplayableDummies.generateLikedImageDisplayables
import com.isao.yfoo2.utils.KoinRule
import com.isao.yfoo2.utils.getString
import com.isao.yfoo2.utils.hasNoText
import io.kotest.matchers.collections.shouldHaveSingleElement
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LikedScreenTest {
    @get:Rule
    val testRule = createComposeRule()

    @get:Rule
    val koinRule = KoinRule(emptyList())

    private val intents = mutableListOf<LikedIntent>()

    // A screenshot test already covers this case, so this test is likely unnecessary
    @Test
    fun `when loading, show loading placeholder`() {
        testRule.setUpComposable(state = LikedUiState(isLoading = true), onIntent = {})
        testRule.onNodeWithContentDescription(getString(R.string.loading)).assertExists()
    }

    // A screenshot test already covers this case, so this test is likely unnecessary
    @Test
    fun `when error, show error bar`() {
        testRule.setUpComposable(state = LikedUiState(isError = true), onIntent = {})
        testRule.onNodeWithText(getString(R.string.something_went_wrong)).assertExists()
    }

    // A screenshot test already covers this case, so this test is likely unnecessary
    @Test
    fun `when content available, show all content`() {
        val content = generateLikedImageDisplayables(4)
        testRule.setUpComposable(state = LikedUiState(items = content), onIntent = {})

        testRule.onNode(hasScrollAction())
            .onChildren()
            .filter(hasClickAction())
            // Sorting button
            .assertAny(hasText(getString(R.string.added)))
            // Items + Sorting button
            .assertCountEquals(content.size + 1)
    }

    @Test
    fun `when content available, sorting button is available`() {
        val content = generateLikedImageDisplayables(4)
        testRule.setUpComposable(state = LikedUiState(items = content))

        // Sorting button
        testRule.onNode(hasClickAction() and hasText(getString(R.string.added))).performClick()

        intents shouldHaveSingleElement { it is LikedIntent.SetSorting }
    }

    @Test
    fun `when long clicking item, dropdown appears`() {
        val content = generateLikedImageDisplayables(4)
        testRule.setUpComposable(state = LikedUiState(items = content))

        testRule.onAllNodes(hasClickAction() and hasNoText())[0].performTouchInput { longClick() }

        testRule.onNode(
            hasText(getString(R.string.image_by, content[0].source.websiteName))
        ).assertExists()
        testRule.onNode(
            hasClickAction()
                    and hasText(getString(R.string.delete))
        ).assertExists()
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