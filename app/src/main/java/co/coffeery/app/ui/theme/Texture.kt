package co.coffeery.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import kotlin.random.Random

fun Modifier.coffeeBackground(): Modifier = this.drawWithCache {
    val w = size.width.toInt().coerceAtLeast(1)
    val h = size.height.toInt().coerceAtLeast(1)
    val bitmap = ImageBitmap(w, h)
    val canvas = Canvas(bitmap)
    val paint = Paint().apply { alpha = 0f }
    val rng = Random(42)
    val cell = 6
    var y = 0
    while (y < h) {
        var x = 0
        while (x < w) {
            val alpha = rng.nextFloat() * 0.025f
            if (alpha > 0.005f) {
                paint.alpha = alpha
                canvas.drawRect(
                    Offset(x.toFloat(), y.toFloat()),
                    Size(cell.toFloat(), cell.toFloat()),
                    paint,
                )
            }
            x += cell
        }
        y += cell
    }
    onDrawWithContent {
        drawContent()
        drawImage(bitmap)
    }
}
