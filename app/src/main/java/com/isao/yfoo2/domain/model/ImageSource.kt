package com.isao.yfoo2.domain.model

import kotlin.random.Random

enum class ImageSource {
    THIS_WAIFU_DOES_NOT_EXIST,
    THESE_CATS_DO_NOT_EXIST;

    val websiteUrl
        get() = when (this) {
            THIS_WAIFU_DOES_NOT_EXIST -> "https://www.thiswaifudoesnotexist.net/"
            THESE_CATS_DO_NOT_EXIST -> "https://thesecatsdonotexist.com/"
        }

    val websiteName
        get() = when (this) {
            THIS_WAIFU_DOES_NOT_EXIST -> "This Waifu Does Not Exist"
            THESE_CATS_DO_NOT_EXIST -> "These Cats Do Not Exist"
        }

    fun getImageUrl(imageId: String) = when (this) {
        THIS_WAIFU_DOES_NOT_EXIST -> "https://www.thiswaifudoesnotexist.net/example-$imageId.jpg"
        THESE_CATS_DO_NOT_EXIST -> "https://d2ph5fj80uercy.cloudfront.net/01/cat$imageId.jpg"
    }

    fun getRandomImageId() = when (this) {
        THIS_WAIFU_DOES_NOT_EXIST -> Random.nextInt(100000 + 1).toString()
        THESE_CATS_DO_NOT_EXIST -> Random.nextInt(8000 + 1).toString()
    }
}