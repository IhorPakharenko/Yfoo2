package com.isao.yfoo2.presentation.liked

import com.isao.yfoo2.BaseBehaviorSpec
import io.kotest.matchers.shouldBe
import org.koin.test.inject


class LikedViewModelTest : BaseBehaviorSpec() {
    //TODO tests might be run via robolectric
    // (RobolectricExtension.runTest, RobolectricExtension.intercept)
    // Or is this normal?
    init {
        val subject: LikedViewModel by inject()

        Given("a")
        {
            When("B") {
                Then("C") {
                    subject.uiState.value shouldBe null

                }
            }
        }
    }
}
