package co.coffeery.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import kotlin.random.Random

fun Modifier.coffeeBackground(): Modifier = composed {
    val bg = CoffeeTheme.colors.background
    this.drawBehind {
        drawRect(color = bg, size = size)
        // subtle noise grid at ~2% opacity
        val rng = Random(42)
        val step = 4f
        var y = 0f
        while (y < size.height) {
            var x = 0f
            while (x < size.width) {
                val a = rng.nextFloat() * 0.02f
                if (a > 0.003f) {
                    drawRect(
                        color = bg.copy(alpha = 1f - a),
                        topLeft = androidx.compose.ui.geometry.Offset(x, y),
                        size = Size(step, step),
                    )
                }
                x += step
            }
            y += step
        }
    }
}
