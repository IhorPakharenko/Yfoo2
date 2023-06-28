package com.isao.yfoo2.domain.model

import kotlin.random.Random

enum class ImageSource {
    THIS_WAIFU_DOES_NOT_EXIST;

    val websiteUrl
        get() = when (this) {
            THIS_WAIFU_DOES_NOT_EXIST -> "https://www.thiswaifudoesnotexist.net/"
        }

    val websiteName
        get() = when (this) {
            THIS_WAIFU_DOES_NOT_EXIST -> "This Waifu Does Not Exist"
        }

    fun getImageUrl(id: String) = when (this) {
        THIS_WAIFU_DOES_NOT_EXIST -> {
            "https://www.thiswaifudoesnotexist.net/example-$id.jpg"
        }
    }

    fun getRandomImageId() = when (this) {
        THIS_WAIFU_DOES_NOT_EXIST -> Random.nextInt(100000 + 1).toString()
    }
}