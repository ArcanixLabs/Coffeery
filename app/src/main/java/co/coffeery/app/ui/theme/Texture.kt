package co.coffeery.app.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush

fun Modifier.coffeeBackground(colors: CoffeeColors): Modifier = this.drawBehind {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(colors.background, colors.backgroundEnd),
        ),
        size = size,
    )
}
