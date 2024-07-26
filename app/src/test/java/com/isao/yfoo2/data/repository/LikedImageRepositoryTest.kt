package com.isao.yfoo2.data.repository

import com.isao.yfoo2.data.local.mapper.toEntityModel
import com.isao.yfoo2.data.testdoubles.FakeLikedImageDao
import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.domain.model.LikedImage
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.collections.shouldNotContain
import kotlinx.coroutines.flow.first
import java.time.Instant

class LikedImageRepositoryTest : FunSpec({

//    beforeTest {
//    }
//
//    afterTest { (testCase, result) ->
//    }

    val likedImageDao = FakeLikedImageDao()

    val subject = LikedImageRepositoryImpl(likedImageDao)

    context("Given no images") {
        context("When image deleted") {
            shouldNotThrowAny { subject.deleteImage("id") }
            test("Then throw no exceptions") {}
        }
    }

    context("Given existing images") {
        likedImageDao.saveLikedImage(Dummies.LikedImage1)
        likedImageDao.saveLikedImage(Dummies.LikedImage2)

        context("When image deleted") {
            subject.deleteImage("1")
            val likedImages = likedImageDao.getLikedImages().first()

            test("Then it is deleted by id") {
                likedImages shouldNotContain Dummies.LikedImage1
            }

            test("Then only one image is deleted") {
                likedImages shouldContainOnly listOf(
                    Dummies.LikedImage2
                )
            }
        }
    }
})

private object Dummies {
    val LikedImage1 = LikedImage(
        id = "1",
        imageId = "graeci",
        source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST,
        dateAdded = Instant.now()
    ).toEntityModel()
    val LikedImage2 = LikedImage(
        id = "2",
        imageId = "graeci",
        source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST,
        dateAdded = Instant.now()
    ).toEntityModel()
}
