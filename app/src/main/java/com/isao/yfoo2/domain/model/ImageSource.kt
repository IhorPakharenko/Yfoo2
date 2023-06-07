package com.isao.yfoo2.domain.model

enum class ImageSource {
    THIS_WAIFU_DOES_NOT_EXIST;

    val websiteUrl
        get() = when (this) {
            THIS_WAIFU_DOES_NOT_EXIST -> "https://www.thiswaifudoesnotexist.net/"
        }

    fun getImageUrl(id: String) = when (this) {
        THIS_WAIFU_DOES_NOT_EXIST -> {
            "https://www.thiswaifudoesnotexist.net/example-$id.jpg"
        }
    }
}