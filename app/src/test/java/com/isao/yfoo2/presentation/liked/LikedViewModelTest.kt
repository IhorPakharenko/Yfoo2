package com.isao.yfoo2.presentation.liked

import app.cash.turbine.Event
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.isao.yfoo2.core.di.appModule
import com.isao.yfoo2.data.local.dao.LikedImageDao
import com.isao.yfoo2.data.local.mapper.toEntityModel
import com.isao.yfoo2.data.testdoubles.FakeLikedImageDao
import com.isao.yfoo2.domain.model.dummy.LikedImageDummies.LikedImage1
import com.isao.yfoo2.domain.model.dummy.LikedImageDummies.LikedImage2
import com.isao.yfoo2.presentation.liked.LikedEvent.OpenWebBrowser
import com.isao.yfoo2.presentation.liked.LikedIntent.DeleteImageClicked
import com.isao.yfoo2.presentation.liked.LikedIntent.ImageClicked
import com.isao.yfoo2.presentation.liked.LikedIntent.SetSorting
import com.isao.yfoo2.presentation.liked.LikedIntent.ViewImageSourceClicked
import com.isao.yfoo2.presentation.liked.mapper.toPresentationModel
import com.isao.yfoo2.utils.MainDispatcherExtension
import com.isao.yfoo2.utils.TimberConsoleExtension
import com.isao.yfoo2.utils.WhenWithData
import com.isao.yfoo2.utils.testsModule
import io.kotest.core.coroutines.backgroundScope
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestScope
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.inspectors.forNone
import io.kotest.koin.KoinExtension
import io.kotest.koin.KoinLifecycleMode
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.declare


@OptIn(ExperimentalStdlibApi::class)
class LikedViewModelTest : BehaviorSpec(), KoinTest {

    //TODO tests might be run via robolectric
    // (RobolectricExtension.runTest, RobolectricExtension.intercept)
    // Or is this normal?

    override fun extensions() = listOf(
        // TODO missing fake dependencies when passing a single combined module
        //  containing these 3 modules. Investigate
        KoinExtension(
            modules = appModule + defaultModule + testsModule,
            mode = KoinLifecycleMode.Root
        ),
        MainDispatcherExtension(),
        TimberConsoleExtension(),
    )

    init {
        val subject: LikedViewModel by inject()
        Given("Entered the screen") {
            Then("No error is produced") {
                Dispatchers.setMain(StandardTestDispatcher())
                subject.uiState.test {
                    testCoroutineScheduler.advanceUntilIdle()
                    val states = consumeNonFinalStates()
                    states.forNone { it.isError shouldBe true }
                }
            }
            Then("Loading is in progress and then finished") {
                Dispatchers.setMain(StandardTestDispatcher())
                subject.uiState.test {
                    awaitItemMatching { it.isLoading }
                    testCoroutineScheduler.advanceUntilIdle()
                    consumeNonFinalStates().last().isLoading shouldBe false
                }
            }
        }

        Given("Existing liked images") {
            val existingImages = listOf(
                LikedImage2,
                LikedImage1,
            )
            val existingImagesPresentation = existingImages.map { it.toPresentationModel() }
            get<FakeLikedImageDao>().apply {
                existingImages.forEach {
                    saveLikedImage(it.toEntityModel())
                }
            }

            Then("They are displayed") {
                subject.uiState.test {
                    subject.uiState.value.items shouldContainExactlyInAnyOrder existingImagesPresentation
                    cancelAndConsumeRemainingEvents()
                }
            }
            When("Images are sorted by default") {
                //TODO test toggle state
                Then("Images are shown from newer to older") {
                    subject.uiState.testValue().items shouldContainExactly listOf(
                        LikedImage2.toPresentationModel(),
                        LikedImage1.toPresentationModel()
                    )
                }
            }
            When("Images are sorted in ascending order") {
                subject.acceptIntent(SetSorting(shouldSortAscending = true))

                Then("Images are shown from older to newer") {
                    getLatestState(subject.uiState).items shouldContainExactly listOf(
                        LikedImage1.toPresentationModel(),
                        LikedImage2.toPresentationModel()
                    )
                }

                And("Sorting order is changed again") {
                    subject.acceptIntent(SetSorting(shouldSortAscending = false))

                    Then("Images are shown from newer to older") {
                        subject.uiState.testValue().items shouldContainExactly listOf(
                            LikedImage2.toPresentationModel(),
                            LikedImage1.toPresentationModel()
                        )
                    }
                }
            }
            WhenWithData(
                name = { "Clicking image ${it.id}" },
                data = existingImagesPresentation
            ) { image ->
                subject.acceptIntent(ImageClicked(image))

                Then("Open web browser with the according url") {
                    subject.uiState.testValue()
                    subject.event.test {
                        awaitItem() shouldBe OpenWebBrowser(image.imageUrl)
                    }
                }
            }
            WhenWithData(
                name = { "Clicking view image source for image ${it.id}" },
                data = existingImagesPresentation
            ) { image ->
                subject.acceptIntent(ViewImageSourceClicked(image))

                Then("Open web browser with the according url") {
                    subject.uiState.testValue()
                    subject.event.test {
                        awaitItem() shouldBe OpenWebBrowser(image.source.websiteUrl)
                    }
                }
            }
            WhenWithData(
                name = { "Deleting image ${it.id}" },
                data = existingImagesPresentation
            ) { image ->
                subject.acceptIntent(DeleteImageClicked(image))

                Then("This image is deleted, no error or loading is displayed") {
                    val state = subject.uiState.testValue()
                    state.items shouldBe existingImagesPresentation - image
                    state.isError shouldBe false
                    state.isLoading shouldBe false
                }
            }
            When("Deleting all images") {
                subject.uiState.testValue() // Create the subject and let it load images

                existingImagesPresentation.forEach {
                    subject.acceptIntent(DeleteImageClicked(it))
                }

                Then("Images are empty, no error or loading is displayed") {
                    val state = subject.uiState.testValue()
                    state.items shouldBe emptyList()
                    state.isError shouldBe false
                    state.isLoading shouldBe false
                }
            }
            When("Deleting image results in exception") {
                declare {
                    spyk<LikedImageDao>(get<FakeLikedImageDao>()) {
                        coEvery { deleteLikedImage(any()) } throws IllegalStateException()
                    }
                }

                subject.acceptIntent(DeleteImageClicked(LikedImage1.toPresentationModel()))

                Then("The image is not deleted and the error is ignored") {
                    val state = subject.uiState.testValue()
                    state.items shouldBe existingImagesPresentation
                    state.isError shouldBe false
                    state.isLoading shouldBe false
                }
            }
        }
        Given("Exception while getting images") {
            declare {
                spyk<LikedImageDao>(get<FakeLikedImageDao>()) {
                    coEvery {
                        getLikedImages(any(), any(), any())
                    } returns flow { throw IllegalStateException() }
                }
            }
            Then("Error is shown, no loading and images are shown") {
                val state = subject.uiState.testValue()
                state.items shouldBe emptyList()
                state.isError shouldBe true
                state.isLoading shouldBe false
            }
        }
    }
}

//TODO extract functions below to utils

suspend fun <T> StateFlow<T>.testValue(): T {
    test { cancelAndIgnoreRemainingEvents() }
    return value
}

@OptIn(ExperimentalStdlibApi::class)
fun <T> TestScope.getLatestState(flow: StateFlow<T>): T {
    backgroundScope.launch(UnconfinedTestDispatcher(testCoroutineScheduler)) {
        flow.collect()
    }
    return flow.value
}

/**
 * Awaits for an item in the flow that matches the given [condition].
 * Discards all other items. If the flow completes before finding the item,
 * throws an AssertionError.
 *
 * @param condition The condition to match against the items in the flow.
 * @return The first item that matches the condition.
 * @throws AssertionError if the flow completes without finding a matching item.
 */
suspend fun <T> ReceiveTurbine<T>.awaitItemMatching(condition: (T) -> Boolean): T {
    while (true) {
        when (val event = awaitEvent()) {
            is Event.Item -> {
                if (condition(event.value)) {
                    return event.value
                }
            }

            is Event.Complete -> {
                throw AssertionError("Flow completed without finding a matching item.")
            }

            is Event.Error -> {
                throw event.throwable
            }
        }
    }
}

/**
 * Cancels the subscription and consumes all remaining events.
 * Returns a list of all non-final states (items) encountered.
 * Throws an error if any of the events is an error.
 *
 * @return A list of all non-final states (items).
 * @throws Throwable if any of the events is an error.
 */
suspend fun <T> ReceiveTurbine<T>.consumeNonFinalStates(): List<T> {
    val states = mutableListOf<T>()

    // Cancel the turbine and consume all remaining events
    val events = cancelAndConsumeRemainingEvents()
    for (event in events) {
        when (event) {
            is Event.Item -> states.add(event.value)
            is Event.Error -> throw event.throwable
            // We don't need to handle ReceiveTurbine.Event.Complete as it's final
            Event.Complete -> {}
        }
    }

    return states
}
