package com.isao.yfoo2.presentation.liked

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
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
import androidx.test.espresso.intent.rule.IntentsRule
import com.isao.yfoo2.R
import com.isao.yfoo2.core.utils.KoinRule
import com.isao.yfoo2.core.utils.getString
import com.isao.yfoo2.core.utils.printSemantics
import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.presentation.liked.composable.LikedScreen
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import io.kotest.matchers.collections.shouldHaveSingleElement
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
//TODO or @RunWith(AndroidJUnit4::class)
class LikedScreenTestRobolectric {
    @get:Rule
    val testRule = createComposeRule()

    @get:Rule
    val koinRule = KoinRule(emptyList()) //TODO is it needed?

    @get:Rule
    val intentsRule = IntentsRule()

    val intents = mutableListOf<LikedIntent>()

    @Test
    fun `when loading, show loading placeholder`() {
        testRule.setContent {
            LikedScreen(uiState = LikedUiState(isLoading = true), onIntent = {})
        }
        testRule.onNodeWithContentDescription(getString(R.string.loading)).assertExists()
    }

    @Test
    fun `when error, show error bar`() {
        testRule.setContent {
            LikedScreen(uiState = LikedUiState(isError = true), onIntent = {})
        }
        testRule.onNodeWithText(getString(R.string.something_went_wrong)).assertExists()
    }

    @Test
    fun `when content available, show all content`() {
        val content = Dummies.generateLikedImageDisplayables(4)
        testRule.setContent {
            LikedScreen(uiState = LikedUiState(items = content), onIntent = {})
        }

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
        val content = Dummies.generateLikedImageDisplayables(4)
        testRule.setUpComposable(state = LikedUiState(items = content))
        // Sorting button
        testRule.onNode(hasClickAction() and hasText(getString(R.string.added))).performClick()

        intents shouldHaveSingleElement { it is LikedIntent.SetSorting }
    }

    @Test
    fun `when clicking item, open browser with image`() {
        val content = Dummies.generateLikedImageDisplayables(4)
        testRule.setUpComposable(state = LikedUiState(items = content))
        testRule.onAllNodes(hasClickAction()).filter(hasNoText())[0].performClick()

        intents shouldHaveSingleElement LikedIntent.ImageClicked(content[0])
    }

    @Test
    fun `when long clicking item, dropdown appears`() {
        val content = Dummies.generateLikedImageDisplayables(4)
        testRule.setContent {
            LikedScreen(uiState = LikedUiState(items = content), onIntent = {})
        }
        testRule.onAllNodes(hasClickAction())
            .filter(hasNoText())[0].performTouchInput { longClick() }
        testRule.printSemantics()
        testRule.onNode(
            hasText(getString(R.string.image_by, content[0].source.websiteName))
        ).assertExists()
        testRule.onNode(
            hasClickAction()
                    and hasText(getString(R.string.delete))
        ).assertExists()
    }

    @Test
    fun `given dropdown open, when 'image by' clicked, open browser with image source`() {
        val content = Dummies.generateLikedImageDisplayables(4)
        testRule.setUpComposable(state = LikedUiState(items = content))
        testRule.onAllNodes(hasClickAction())
            .filter(hasNoText())[0].performTouchInput { longClick() }
        testRule.onNode(
            hasText(getString(R.string.image_by, content[0].source.websiteName))
        ).performClick()

        intents shouldHaveSingleElement LikedIntent.ViewImageSourceClicked(content[0])

        //TODO consider testing actual intents too
        //TODO these tests are probably too much. Remember 80 / 20 rule?
//        Intents.intended(AllOf(listOf(hasAction(Intent.ACTION_VIEW), hasData(content[0].imageUrl))))
    }

    @Test
    fun `given dropdown open, when delete clicked, delete item`() {
        val content = Dummies.generateLikedImageDisplayables(4)
        testRule.setUpComposable(state = LikedUiState(items = content))
        testRule.onAllNodes(hasClickAction())
            .filter(hasNoText())[0].performTouchInput { longClick() }
        testRule.onNode(hasText(getString(R.string.delete))).performClick()

        intents shouldHaveSingleElement LikedIntent.DeleteImageClicked(content[0])
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

    private object Dummies {
        fun generateLikedImageDisplayables(size: Int) = List(size) { index ->
            LikedImageDisplayable(
                id = "${index}",
                imageUrl = "https://example.com/${index}",
                source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST
            )
        }
    }
}

fun hasNoText(): SemanticsMatcher {
    val propertyName = "${SemanticsProperties.Text.name} + ${SemanticsProperties.EditableText.name}"
    return SemanticsMatcher("$propertyName contains no text") {
        val editableText = it.config.getOrNull(SemanticsProperties.EditableText)
        val text = it.config.getOrNull(SemanticsProperties.Text)
        editableText == null && text == null
    }
}