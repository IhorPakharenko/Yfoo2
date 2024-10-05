package com.isao.yfoo2.presentation.transformations

import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.domain.model.ImageSource.THESE_CATS_DO_NOT_EXIST
import com.isao.yfoo2.domain.model.ImageSource.THIS_WAIFU_DOES_NOT_EXIST

object BitmapTransformations {
    fun getFor(source: ImageSource) = when (source) {
        THIS_WAIFU_DOES_NOT_EXIST -> listOf(BorderCropTransformation())
        THESE_CATS_DO_NOT_EXIST -> emptyList()
    }
}