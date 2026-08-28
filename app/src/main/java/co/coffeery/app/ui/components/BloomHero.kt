package co.coffeery.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.coffeery.app.R
import co.coffeery.app.data.model.Equipment
import co.coffeery.app.ui.haptic.rememberAppHaptics
import co.coffeery.app.ui.theme.CoffeeMotion
import co.coffeery.app.ui.theme.CoffeeShapes
import co.coffeery.app.ui.theme.CoffeeTheme
import co.coffeery.app.ui.theme.LocalPrefersReducedMotion
import co.coffeery.app.util.BrewResult
import kotlin.random.Random

@Composable
fun BloomHero(
    equipment: Equipment,
    result: BrewResult,
    modifier: Modifier = Modifier,
) {
    val colors = CoffeeTheme.colors
    val haptics = rememberAppHaptics()
    var phase by remember { mutableIntStateOf(0) }
    val reducedPref = LocalPrefersReducedMotion.current
    val progress by animateFloatAsState(
        targetValue = when (phase) {
            0 -> 0.25f
            1 -> 0.6f
            else -> 1f
        },
        animationSpec = if (reducedPref) tween(0) else tween(CoffeeMotion.slow, easing = CoffeeMotion.emphasized),
        label = "bloomProgress",
    )
    val phaseLabel = when (phase) {
        0 -> stringResource(R.string.bloom_phase_rinse)
        1 -> stringResource(R.string.bloom_phase_bloom)
        else -> stringResource(R.string.bloom_phase_pour)
    }
    val infinite = rememberInfiniteTransition(label = "bubbleDrift")
    val driftAnim by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Restart),
        label = "drift",
    )
    val drift = if (reducedPref || phase != 1) 0f else driftAnim
    val bloomInteraction = remember { MutableInteractionSource() }
    val isBloomPressed by bloomInteraction.collectIsPressedAsState()
    val bloomSx by animateFloatAsState(targetValue = if (isBloomPressed) 0.96f else 1f, animationSpec = CoffeeMotion.press, label = "bloomPressX")
    val bloomSy by animateFloatAsState(targetValue = if (isBloomPressed) 0.98f else 1f, animationSpec = CoffeeMotion.press, label = "bloomPressY")
    CoffeeCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppText(phaseLabel, style = CoffeeTheme.type.label, color = colors.accent)
            Spacer(Modifier.weight(1f))
            AppText(stringResource(R.string.bloom_tap_hint), style = CoffeeTheme.type.caption, color = colors.textSecondary)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .graphicsLayer(scaleX = bloomSx, scaleY = bloomSy)
                .clip(CoffeeShapes.medium)
                .background(colors.surface)
                .clickable(interactionSource = bloomInteraction, indication = androidx.compose.foundation.LocalIndication.current) {
                    haptics.tap()
                    phase = (phase + 1) % 3
                },
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(112.dp)) {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val coneTop = h * 0.14f
                val coneBottom = h * 0.86f
                val topHalf = w * 0.42f
                val botHalf = w * 0.16f
                val path = Path().apply {
                    moveTo(cx - topHalf, coneTop)
                    lineTo(cx + topHalf, coneTop)
                    lineTo(cx + botHalf, coneBottom)
                    lineTo(cx - botHalf, coneBottom)
                    close()
                }
                drawPath(path, color = colors.outline, style = Stroke(width = size.minDimension * 0.018f))
                val ribCount = 6
                for (i in 1 until ribCount) {
                    val t = i / ribCount.toFloat()
                    val y = coneTop + (coneBottom - coneTop) * t
                    val hw = topHalf + (botHalf - topHalf) * t
                    drawLine(
                        color = colors.outline.copy(alpha = 0.35f),
                        start = Offset(cx - hw * 0.92f, y),
                        end = Offset(cx + hw * 0.92f, y),
                        strokeWidth = 1.2f,
                    )
                }
                val fillH = (coneBottom - coneTop) * progress
                val fillTop = coneBottom - fillH
                val tTop = (fillTop - coneTop) / (coneBottom - coneTop).coerceAtLeast(1f)
                val hwTop = topHalf + (botHalf - topHalf) * tTop
                val fillPath = Path().apply {
                    moveTo(cx - hwTop, fillTop)
                    lineTo(cx + hwTop, fillTop)
                    lineTo(cx + botHalf, coneBottom)
                    lineTo(cx - botHalf, coneBottom)
                    close()
                }
                drawPath(fillPath, color = colors.accent.copy(alpha = 0.14f))
                drawPath(
                    fillPath,
                    color = colors.accent.copy(alpha = 0.9f),
                    style = Stroke(width = 2f),
                )
                if (phase == 1) {
                    val rng = Random(42)
                    for (i in 0 until 7) {
                        val baseX = cx + (rng.nextFloat() - 0.5f) * hwTop * 1.2f
                        val baseY = fillTop + rng.nextFloat() * fillH * 0.6f
                        val r = 1.5f + rng.nextFloat() * 2.5f
                        val sway = kotlin.math.sin((drift * 6.28 + i).toDouble()).toFloat() * 3f
                        val dy = (drift * fillH * 0.3f) % (fillH * 0.6f).coerceAtLeast(1f)
                        val bx = baseX + sway
                        val by = (baseY - dy).coerceIn(fillTop, fillTop + fillH)
                        val r2 = r * (1f + drift * 0.1f)
                        drawCircle(
                            color = colors.accent.copy(alpha = 0.55f),
                            radius = r2,
                            center = Offset(bx, by),
                        )
                        drawCircle(
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                            radius = r2 * 0.45f,
                            center = Offset(bx - r2 * 0.3f, by - r2 * 0.3f),
                        )
                    }
                }
                val barW = w * 0.88f
                val barH = 6.dp.toPx()
                val barY = h - 10.dp.toPx()
                val barX = (w - barW) / 2f
                drawRoundRect(
                    color = colors.outline,
                    topLeft = Offset(barX, barY),
                    size = Size(barW, barH),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
                drawRoundRect(
                    color = colors.accent,
                    topLeft = Offset(barX, barY),
                    size = Size(barW * progress, barH),
                    cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip(
                text = "${result.coffeeGrams.let { co.coffeery.app.util.Format.grams(it) }} g",
                background = colors.accentSoft,
                textColor = colors.accent,
            )
            Chip(
                text = "${result.waterMl} ml",
                background = colors.accentSoft,
                textColor = colors.accent,
            )
            Chip(
                text = if (result.tempCelsius > 0) "${result.tempCelsius}°C" else result.grind.name.lowercase(),
                background = colors.surface,
                textColor = colors.textSecondary,
            )
        }
    }
}
