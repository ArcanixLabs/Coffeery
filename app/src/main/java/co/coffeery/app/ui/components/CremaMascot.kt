package co.coffeery.app.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
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
    val bob by rememberInfiniteTransition(label = "cremaBob").animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "bob"
    )
    Canvas(modifier = modifier.graphicsLayer { translationY = bob }.size(64.dp)) {
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
        val eyeY = cy - ch * 0.06f
        val eyeDx = w * 0.11f
        val isHappy = mood != "sleepy"
        drawCircle(Color.White, w * 0.055f, Offset(cx - eyeDx, eyeY))
        drawCircle(Color.White, w * 0.055f, Offset(cx + eyeDx, eyeY))
        drawCircle(Color.Black.copy(0.85f), w * 0.028f, Offset(cx - eyeDx, eyeY + if (isHappy) 0f else w * 0.02f))
        drawCircle(Color.Black.copy(0.85f), w * 0.028f, Offset(cx + eyeDx, eyeY + if (isHappy) 0f else w * 0.02f))
        drawCircle(Color.White, w * 0.012f, Offset(cx - eyeDx + w * 0.015f, eyeY - w * 0.015f))
        drawCircle(Color.White, w * 0.012f, Offset(cx + eyeDx + w * 0.015f, eyeY - w * 0.015f))
        drawCircle(colors.accent.copy(0.45f), w * 0.04f, Offset(cx - eyeDx - w * 0.08f, eyeY + w * 0.08f))
        drawCircle(colors.accent.copy(0.45f), w * 0.04f, Offset(cx + eyeDx + w * 0.08f, eyeY + w * 0.08f))
        val steam = Stroke(w * 0.03f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        drawPath(androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - w * 0.18f, cy - ch * 0.62f)
            cubicTo(cx - w * 0.22f, cy - ch * 0.78f, cx - w * 0.08f, cy - ch * 0.82f, cx - w * 0.12f, cy - ch * 0.98f)
        }, colors.accent.copy(0.5f), style = steam)
        drawPath(androidx.compose.ui.graphics.Path().apply {
            moveTo(cx + w * 0.08f, cy - ch * 0.64f)
            cubicTo(cx + w * 0.14f, cy - ch * 0.8f, cx - w * 0.02f, cy - ch * 0.86f, cx + w * 0.04f, cy - ch * 1.02f)
        }, colors.accent.copy(0.35f), style = steam)
    }
}
