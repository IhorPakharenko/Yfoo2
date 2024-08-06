package com.isao.yfoo2.presentation.mapper

import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.domain.model.LikedImage
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant

class LikedImageMapperTest : FunSpec({

    val domainModel = LikedImage(
        id = "id",
        imageId = "imageId",
        source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST,
        dateAdded = Instant.now()
    )

    val presentationModel = LikedImageDisplayable(
        id = domainModel.id,
        imageUrl = "https://www.thiswaifudoesnotexist.net/example-${domainModel.imageId}.jpg",
        source = domainModel.source
    )

    test("domain to displayable") {
        domainModel.toPresentationModel() shouldBe presentationModel
    }
})