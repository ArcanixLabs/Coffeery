package co.coffeery.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.coffeery.app.ui.theme.CoffeeTheme

@Composable
fun CremaMascot(mood: String = "happy", modifier: Modifier = Modifier.size(64.dp)) {
    val colors = CoffeeTheme.colors
    val steamDuration = when (mood) {
        "sleepy" -> 1700
        "excited" -> 700
        "curious" -> 900
        else -> 1150
    }
    val bobDuration = when (mood) {
        "sleepy" -> 1400
        "excited" -> 700
        else -> 900
    }
    val t = rememberInfiniteTransition(label = "crema")
    val bob by t.animateFloat(initialValue = -2f, targetValue = 2f, animationSpec = infiniteRepeatable(tween(bobDuration, easing = androidx.compose.animation.core.FastOutSlowInEasing), RepeatMode.Reverse), label = "bob")
    val sway by t.animateFloat(initialValue = -3f, targetValue = 3f, animationSpec = infiniteRepeatable(tween(1100), RepeatMode.Reverse), label = "sway")
    val blink by t.animateFloat(initialValue = 1f, targetValue = 1f, animationSpec = infiniteRepeatable(keyframes { durationMillis = 3200; 1f at 0; 1f at 2600; 0.08f at 2700; 1f at 2800 }, RepeatMode.Restart), label = "blink")
    val steamPhase by t.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(tween(steamDuration), RepeatMode.Restart), label = "steam")
    val eyeScale = if (mood == "sleepy") 1f else blink
    Canvas(modifier = modifier.graphicsLayer { translationY = bob; rotationZ = sway }.size(64.dp)) {
        val w = size.width
        val h = size.height
        val dy = bob * (h / 64f)
        val cw = w * 0.62f
        val ch = h * 0.56f
        val cx = w / 2f
        val cy = h * 0.52f + dy
        drawOval(colors.accentSoft.copy(0.35f), Offset(cx - cw * 0.55f, cy - ch * 0.55f), Size(cw * 1.1f, ch * 1.1f))
        drawOval(colors.cremaDark, Offset(cx - cw / 2, cy - ch / 2), Size(cw, ch))
        drawOval(colors.cremaLight.copy(0.18f), Offset(cx - cw * 0.2f, cy - ch * 0.28f), Size(cw * 0.32f, ch * 0.22f))
        val sw = w * 0.04f
        val slit = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx, cy - ch * 0.32f)
            cubicTo(cx - w * 0.08f, cy - ch * 0.05f, cx + w * 0.08f, cy + ch * 0.05f, cx, cy + ch * 0.32f)
        }
        drawPath(slit, colors.cremaLight.copy(0.9f), style = Stroke(sw))
        val eyeDx = w * 0.11f
        val eyeY = when (mood) {
            "curious" -> cy - ch * 0.10f
            "excited" -> cy - ch * 0.02f
            "sleepy" -> cy - ch * 0.02f
            else -> cy - ch * 0.06f
        }
        val blushAlpha = when (mood) {
            "excited" -> 0.55f
            "sleepy" -> 0.18f
            "curious" -> 0.30f
            else -> 0.42f
        }
        val pupilR = when (mood) {
            "excited" -> w * 0.032f
            "curious" -> w * 0.024f
            else -> w * 0.028f
        }
        val pupilOffsetY = when (mood) {
            "curious" -> -w * 0.01f
            "sleepy" -> w * 0.02f
            "excited" -> w * 0.005f
            else -> 0f
        }
        if (mood == "sleepy") {
            val eyeW = w * 0.06f
            drawLine(colors.textPrimary.copy(0.55f), Offset(cx - eyeDx - eyeW / 2, eyeY), Offset(cx - eyeDx + eyeW / 2, eyeY), strokeWidth = w * 0.018f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(colors.textPrimary.copy(0.55f), Offset(cx + eyeDx - eyeW / 2, eyeY), Offset(cx + eyeDx + eyeW / 2, eyeY), strokeWidth = w * 0.018f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            val mouth = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx - w * 0.025f, eyeY + w * 0.10f)
                cubicTo(cx - w * 0.01f, eyeY + w * 0.13f, cx + w * 0.01f, eyeY + w * 0.13f, cx + w * 0.025f, eyeY + w * 0.10f)
            }
            drawPath(mouth, colors.textPrimary.copy(0.45f), style = Stroke(w * 0.015f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        } else {
            val whiteR = w * 0.055f
            fun eyeAt(x: Float, y: Float) {
                drawOval(Color.White, topLeft = Offset(x - whiteR, y - whiteR * eyeScale), size = Size(whiteR * 2, whiteR * 2 * eyeScale))
                drawCircle(Color.Black.copy(0.85f), pupilR, Offset(x, y + pupilOffsetY))
                drawCircle(Color.White, w * 0.012f, Offset(x + w * 0.015f, y - w * 0.015f))
            }
            val curiousShift = if (mood == "curious") w * 0.02f else 0f
            eyeAt(cx - eyeDx + curiousShift, eyeY)
            eyeAt(cx + eyeDx + curiousShift, eyeY - if (mood == "curious") w * 0.015f else 0f)
            if (mood == "excited") {
                val mouth = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx - w * 0.03f, eyeY + w * 0.11f)
                    cubicTo(cx, eyeY + w * 0.16f, cx + w * 0.04f, eyeY + w * 0.13f, cx + w * 0.015f, eyeY + w * 0.09f)
                }
                drawPath(mouth, colors.textPrimary.copy(0.6f), style = Stroke(w * 0.016f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
                drawCircle(Color.Black.copy(0.35f), w * 0.018f, Offset(cx + w * 0.005f, eyeY + w * 0.12f))
            } else if (mood == "curious") {
                drawCircle(colors.textPrimary.copy(0.5f), w * 0.018f, Offset(cx + w * 0.02f, eyeY + w * 0.12f))
            } else {
                val mouth = androidx.compose.ui.graphics.Path().apply {
                    moveTo(cx - w * 0.02f, eyeY + w * 0.10f)
                    cubicTo(cx, eyeY + w * 0.13f, cx + w * 0.02f, eyeY + w * 0.10f)
                }
                drawPath(mouth, colors.textPrimary.copy(0.45f), style = Stroke(w * 0.014f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            }
        }
        drawCircle(colors.accent.copy(blushAlpha), w * 0.04f, Offset(cx - eyeDx - w * 0.08f, eyeY + w * 0.08f))
        drawCircle(colors.accent.copy(blushAlpha), w * 0.04f, Offset(cx + eyeDx + w * 0.08f, eyeY + w * 0.08f))
        val steamAlpha1 = 0.5f * (0.7f + 0.3f * steamPhase)
        val steamAlpha2 = 0.35f * (0.7f + 0.3f * (1f - steamPhase))
        val steam = Stroke(w * 0.03f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        val swayX = sway * w * 0.004f
        drawPath(androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - w * 0.18f + swayX, cy - ch * 0.62f)
            cubicTo(cx - w * 0.22f + swayX, cy - ch * 0.78f, cx - w * 0.08f + swayX, cy - ch * 0.82f, cx - w * 0.12f + swayX, cy - ch * 0.98f)
        }, colors.accent.copy(steamAlpha1), style = steam)
        drawPath(androidx.compose.ui.graphics.Path().apply {
            moveTo(cx + w * 0.08f - swayX, cy - ch * 0.64f)
            cubicTo(cx + w * 0.14f - swayX, cy - ch * 0.8f, cx - w * 0.02f - swayX, cy - ch * 0.86f, cx + w * 0.04f - swayX, cy - ch * 1.02f)
        }, colors.accent.copy(steamAlpha2), style = steam)
    }
}
