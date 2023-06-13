package com.isao.yfoo2.presentation.transformations

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import coil.size.Size
import coil.transform.Transformation

/**
 * A [Transformation] that removes blackish paddings from an image.
 *
 * @param borderColorThreshold The value from 0 to 255
 * that defines how close to black the border color is.
 * Closer to 0 means darker color.
 *
 * Designed specifically for the images from [This Waifu Does Not Exist](https://thiswaifudoesnotexist.net).
 */
class BorderCropTransformation(
    private val borderColorThreshold: Int = 60
) : Transformation {

    override val cacheKey: String = "${javaClass.name}-$borderColorThreshold"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        return cropBitmap(input)
    }

    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        var left = 0
        var top = 0
        var right = width - 1
        var bottom = height - 1

        // Find left border
        while (left < width && isColumnEmpty(bitmap, left)) {
            left++
        }

        // Find top border
        while (top < height && isRowEmpty(bitmap, top)) {
            top++
        }

        // Find right border
        while (right >= 0 && isColumnEmpty(bitmap, right)) {
            right--
        }

        // Find bottom border
        while (bottom >= 0 && isRowEmpty(bitmap, bottom)) {
            bottom--
        }

        // Calculate the new dimensions
        val croppedWidth = right - left + 1
        val croppedHeight = bottom - top + 1

        // Create a new bitmap with the cropped dimensions
        val croppedBitmap =
            Bitmap.createBitmap(croppedWidth, croppedHeight, Bitmap.Config.ARGB_8888)

        // Adjust the coordinates to account for the cropped region
        Canvas(croppedBitmap).drawBitmap(bitmap, (-left).toFloat(), (-top).toFloat(), null)

        return croppedBitmap
    }

    private fun isColumnEmpty(bitmap: Bitmap, column: Int): Boolean {
        for (y in 0 until bitmap.height) {
            if (isPixelEmpty(bitmap.getPixel(column, y))) {
                return false
            }
        }
        return true
    }

    private fun isRowEmpty(bitmap: Bitmap, row: Int): Boolean {
        for (x in 0 until bitmap.width) {
            if (isPixelEmpty(bitmap.getPixel(x, row))) {
                return false
            }
        }
        return true
    }

    private fun isPixelEmpty(pixel: Int): Boolean {
        val red = Color.red(pixel)
        val green = Color.green(pixel)
        val blue = Color.blue(pixel)

        return red > borderColorThreshold || green > borderColorThreshold || blue > borderColorThreshold
    }
}