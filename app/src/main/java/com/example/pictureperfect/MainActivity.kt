package com.example.pictureperfect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.ColorUtils
import com.example.pictureperfect.ui.theme.PicturePerfectTheme
import com.example.pictureperfect.utils.PixelHandler
import kotlinx.coroutines.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PicturePerfectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ImageHandling()
                }
            }
        }
    }
}

@Composable
fun ImageHandling() {
    val imageBitmap: ImageBitmap =
        ImageBitmap.imageResource(id = R.drawable.illustration_confused)
    val colors = remember { mutableStateListOf<Color>() }
    val topFive = remember { mutableStateListOf<Pair<Color, Int>>() }
    var loading by remember { mutableStateOf(false) }
    val pixelHandler = PixelHandler()

//    Get Image width from Modifier.onSizeChanged{imageSize = it.toSize()}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Tester"
        )
        Button(onClick = {
            val begin = System.nanoTime()
            colors.clear()
            topFive.clear()
            loading = true
            GlobalScope.async {
                colors.addAll(
                    pixelHandler.init(4, imageBitmap = imageBitmap).flatten()
                )
                topFive.addAll(pixelHandler.getTopFive(colors))
                loading = false

                val end = System.nanoTime()
                println("Elapsed time in seconds: ${((end - begin) / 1000000000)}")
            }
        }, enabled = !loading) {
            if (loading) {
                CircularProgressIndicator(color = Color(0xffffffff))
            } else {
                Text(text = "DO THE MAGIC")
                Icon(Icons.Default.AutoAwesome, "magic")
            }
        }
        Text(text = "Total pixels: ${colors.count()}")
        Column() {
            for (c in topFive) {
                ColorRow(c.first, c.second.toString())
            }
        }
    }
}

fun getTextColor(color: Color): Color {
    return if (ColorUtils.calculateLuminance(color.hashCode()) > 0.5) {
        Color(0xff000000)
    } else {
        Color(0xffffffff)
    }
}

@Composable
fun ColorRow(color: Color, text: String) {
    Row(
        Modifier
            .background(color)
            .fillMaxWidth()
            .padding(0.dp, 10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = text, fontSize = 24.sp, color = getTextColor(color))
    }
}

@Preview
@Composable
fun ColorRowPreview() {
    ColorRow(color = Color(0xfffee9cc), text = "White")
}