package co.coffeery.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Random

fun Modifier.coffeeBackground(colors: CoffeeColors): Modifier = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(colors.background, colors.backgroundEnd),
        ),
    )
    .drawWithCache {
        val grainAlpha = 0.04f
        val rng = Random(42)
        val grainCount = (size.width * size.height * 0.00006f).toInt().coerceIn(30, 90)
        val linenColor = Color(0xFF5C4A32)
        val dots = (0 until grainCount).map {
            val x = rng.nextFloat() * size.width
            val y = rng.nextFloat() * size.height
            val a = grainAlpha * (0.5f + 0.5f * rng.nextFloat())
            val r = 1f + rng.nextFloat() * 1.1f
            Triple(Offset(x, y), r, a)
        }
        onDrawBehind {
            val lineAlpha = 0.03f
            val step = 18.dp.toPx()
            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = linenColor.copy(alpha = lineAlpha),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 0.6.dp.toPx(),
                )
                y += step
            }
            dots.forEach { (center, radius, a) ->
                drawCircle(
                    color = linenColor.copy(alpha = a),
                    radius = radius,
                    center = center,
                )
            }
        }
    }
