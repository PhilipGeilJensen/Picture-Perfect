package com.example.pictureperfect.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class PixelHandler {

    suspend fun init(threads: Int, imageBitmap: ImageBitmap): List<List<Color>> {
        val colorsLocal = mutableStateListOf<Deferred<List<Color>>>()
        coroutineScope {
            for (i in 0 until threads) {
                colorsLocal.add(
                    async {
                        pixelCounter(
                            imageBitmap = imageBitmap,
                            offsetXStart = i * (imageBitmap.width / threads),
                            offsetXEnd = (i + 1) * (imageBitmap.width / threads),
                            thread = i
                        )
                    }
                )
            }

        }
        return colorsLocal.awaitAll()
    }

    fun pixelCounter(
        imageBitmap: ImageBitmap,
        offsetXStart: Int,
        offsetXEnd: Int,
        thread: Int
    ): List<Color> {
        println("OffsetXStart: $offsetXStart - OffsetEndStart: $offsetXEnd-  Thread: $thread")
        val colors = mutableStateListOf<Color>()
        var x = offsetXStart
        while (x < offsetXEnd && x < imageBitmap.width) {
            var y = 0
            while (y < imageBitmap.height) {
                try {
                    val pixel: Int =
                        imageBitmap
                            .asAndroidBitmap()
                            .getPixel(x, y)


                    val red = android.graphics.Color.red(pixel)
                    val green = android.graphics.Color.green(pixel)
                    val blue = android.graphics.Color.blue(pixel)
                    if (!isBlackOrWhite(red, green, blue)) {
                        colors.add(Color(red, green, blue))
                    }
                } catch (e: Exception) {
                    println("Exception e: ${e.message}")
                    throw e
                }
                y++
            }
            x++
        }
        return colors
    }

    fun getTopFive(colors: List<Color>): List<Pair<Color, Int>> {
        val list = mutableStateListOf<Pair<Color, Int>>()
        val numbersByElement = colors.groupingBy { it }.eachCount()
        val res =
            numbersByElement.toList()
                .sortedByDescending { (_, value) -> value }
                .take(5)
        for (entry in res) {
            list.add(entry)
        }
        return list
    }

    private fun isBlackOrWhite(red: Int, green: Int, blue: Int): Boolean {
        return if (red == 255 && green == 255 && blue == 255) {
            true
        } else (red == 0 && green == 0 && blue == 0)
    }
}