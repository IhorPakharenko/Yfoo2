package com.isao.yfoo2.domain.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkObject
import kotlin.random.Random

class ImageSourceTest : FunSpec({
    context("Source: This waifu does not exist") {
        val subject = ImageSource.THIS_WAIFU_DOES_NOT_EXIST

        test("websiteUrl") {
            subject.websiteUrl shouldBe "https://www.thiswaifudoesnotexist.net/"
        }

        test("websiteName") {
            subject.websiteName shouldBe "This Waifu Does Not Exist"
        }

        test("getImageUrl") {
            val id = "abc"
            subject.getImageUrl(id) shouldBe "https://www.thiswaifudoesnotexist.net/example-abc.jpg"
        }

        test("getRandomImageId") {
            mockkObject(Random)
            every { Random.nextInt(100000 + 1) } returns Int.MIN_VALUE
            subject.getRandomImageId() shouldBe Int.MIN_VALUE.toString()
        }
    }
})