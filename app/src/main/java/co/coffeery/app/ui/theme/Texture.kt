package co.coffeery.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.util.Random

fun Modifier.coffeeBackground(colors: CoffeeColors): Modifier = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(colors.background, colors.backgroundEnd),
        ),
    )
    .drawWithCache {
        val grainAlpha = 0.03f
        val rng = Random(42)
        val grainCount = (size.width * size.height * 0.00008f).toInt().coerceIn(40, 140)
        val dots = (0 until grainCount).map {
            val x = rng.nextFloat() * size.width
            val y = rng.nextFloat() * size.height
            val a = grainAlpha * (0.5f + 0.5f * rng.nextFloat())
            val r = 1f + rng.nextFloat() * 1.2f
            Triple(Offset(x, y), r, a)
        }
        onDrawBehind {
            dots.forEach { (center, radius, a) ->
                drawCircle(
                    color = Color.Black.copy(alpha = a),
                    radius = radius,
                    center = center,
                )
            }
        }
    }
