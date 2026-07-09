package co.coffeery.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun Modifier.coffeeBackground(): Modifier = this.drawBehind {
    val rng = Random(42)
    val cellSize = 6f
    var y = 0f
    while (y < size.height) {
        var x = 0f
        while (x < size.width) {
            val alpha = rng.nextFloat() * 0.025f
            if (alpha > 0.005f) {
                drawRect(
                    color = Color.Black.copy(alpha = alpha),
                    topLeft = Offset(x, y),
                    size = Size(cellSize, cellSize),
                )
            }
            x += cellSize
        }
        y += cellSize
    }
}

