@file:OptIn(ExperimentalCoroutinesApi::class)

package com.isao.yfoo2.presentation.liked

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
import com.isao.yfoo2.utils.awaitItemMatching
import com.isao.yfoo2.utils.consumeNonFinalStates
import com.isao.yfoo2.utils.getLatestState
import com.isao.yfoo2.utils.testValue
import com.isao.yfoo2.utils.testsModule
import io.kotest.core.spec.style.BehaviorSpec
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.declare


@OptIn(ExperimentalStdlibApi::class)
class LikedViewModelTest : BehaviorSpec(), KoinTest {

    override fun extensions() = listOf(
        KoinExtension(
            modules = appModule + defaultModule + testsModule,
            mode = KoinLifecycleMode.Root
        ),
        MainDispatcherExtension(),
        TimberConsoleExtension(),
    )

    private val subject: LikedViewModel by inject()

    init {
        Given("Entered the screen") {
            Then("No error is produced") {
                Dispatchers.setMain(StandardTestDispatcher())
                subject.uiState.test {
                    testCoroutineScheduler.advanceUntilIdle()
                    consumeNonFinalStates().forNone { it.isError shouldBe true }
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
                Then("Images are shown from newer to older") {
                    subject.uiState.testValue().apply {
                        items shouldContainExactly listOf(
                            LikedImage2.toPresentationModel(),
                            LikedImage1.toPresentationModel()
                        )
                        shouldSortAscending shouldBe false
                    }
                }
            }
            When("Images are sorted in ascending order") {
                subject.acceptIntent(SetSorting(shouldSortAscending = true))

                Then("Images are shown from older to newer") {
                    getLatestState(subject.uiState).apply {
                        items shouldContainExactly listOf(
                            LikedImage1.toPresentationModel(),
                            LikedImage2.toPresentationModel()
                        )
                        shouldSortAscending shouldBe true
                    }
                }

                And("Sorting order is changed again") {
                    subject.acceptIntent(SetSorting(shouldSortAscending = false))

                    Then("Images are shown from newer to older") {
                        subject.uiState.testValue().apply {
                            items shouldContainExactly listOf(
                                LikedImage2.toPresentationModel(),
                                LikedImage1.toPresentationModel()
                            )
                            shouldSortAscending shouldBe false
                        }
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
