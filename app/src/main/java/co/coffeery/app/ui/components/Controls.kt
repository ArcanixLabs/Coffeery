package co.coffeery.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.coffeery.app.ui.haptic.rememberAppHaptics
import co.coffeery.app.ui.theme.CoffeeMotion
import co.coffeery.app.ui.theme.CoffeeShapes
import co.coffeery.app.ui.theme.CoffeeTheme
import co.coffeery.app.ui.theme.LocalPrefersReducedMotion

@Composable
fun <T> SegmentedControl(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    subtitle: (@Composable (T) -> String)? = null,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = CoffeeTheme.colors
    val haptics = rememberAppHaptics()
    val reduced = LocalPrefersReducedMotion.current
    Row(
        modifier = modifier
            .clip(CoffeeShapes.pill)
            .background(colors.surface)
            .border(1.dp, colors.outline, CoffeeShapes.pill)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val animatedBg by animateColorAsState(targetValue = if (isSelected) colors.accent else colors.surface, animationSpec = if (reduced) tween(0) else tween(CoffeeMotion.normal), label = "segBg")
            val animatedTextColor by animateColorAsState(targetValue = if (isSelected) colors.onAccent else colors.textSecondary, animationSpec = if (reduced) tween(0) else tween(CoffeeMotion.normal), label = "segText")
            val animatedCaptionColor by animateColorAsState(targetValue = if (isSelected) colors.onAccent.copy(alpha = 0.7f) else colors.textSecondary.copy(alpha = 0.7f), animationSpec = if (reduced) tween(0) else tween(CoffeeMotion.normal), label = "segCaption")
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val pressSx by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, animationSpec = CoffeeMotion.press, label = "pressX")
            val pressSy by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, animationSpec = CoffeeMotion.press, label = "pressY")
            Box(
                modifier = Modifier
                    .weight(1f)
                    .graphicsLayer(scaleX = pressSx, scaleY = pressSy)
                    .clip(CoffeeShapes.pill)
                    .background(animatedBg)
                    .clickable(interactionSource = interactionSource, indication = null) {
                        haptics.segment()
                        onSelect(option)
                    }
                    .padding(vertical = if (subtitle != null) 10.dp else 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (subtitle != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AppText(
                            text = label(option),
                            style = CoffeeTheme.type.label,
                            color = animatedTextColor,
                            align = TextAlign.Center,
                        )
                        AppText(
                            text = subtitle(option),
                            style = CoffeeTheme.type.caption,
                            color = animatedCaptionColor,
                            align = TextAlign.Center,
                        )
                    }
                } else {
                    AppText(
                        text = label(option),
                        style = CoffeeTheme.type.label,
                        color = animatedTextColor,
                        align = TextAlign.Center,
                    )
                }
            }
        }
    }
}

/** Rounded +/- stepper for cup count. */
@Composable
fun Stepper(
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 1,
    max: Int = 12,
) {
    val colors = CoffeeTheme.colors
    val haptics = rememberAppHaptics()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        StepButton("–", enabled = value > min) {
            if (value <= min) {
                haptics.reject()
            } else {
                haptics.tick()
                onChange((value - 1).coerceAtLeast(min))
            }
        }
        AppText(
            text = value.toString(),
            style = CoffeeTheme.type.title,
            color = colors.textPrimary,
        )
        StepButton("+", enabled = value < max) {
            if (value >= max) {
                haptics.reject()
            } else {
                haptics.tick()
                onChange((value + 1).coerceAtMost(max))
            }
        }
    }
}

@Composable
private fun RowScope.StepButton(symbol: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = CoffeeTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val sx by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, animationSpec = CoffeeMotion.press, label = "pressX")
    val sy by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, animationSpec = CoffeeMotion.press, label = "pressY")
    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer(scaleX = sx, scaleY = sy)
            .clip(CoffeeShapes.pill)
            .border(1.5.dp, if (enabled) colors.accent else colors.outline, CoffeeShapes.pill)
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = symbol,
            style = CoffeeTheme.type.title,
            color = if (enabled) colors.accent else colors.outline,
        )
    }
}

/** BasicTextField styled to the design system, with a placeholder. */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    val colors = CoffeeTheme.colors
    val reduced = LocalPrefersReducedMotion.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor by animateColorAsState(targetValue = if (isFocused) colors.accent else colors.outline, animationSpec = if (reduced) tween(0) else tween(CoffeeMotion.normal), label = "borderColor")
    val borderWidth by animateDpAsState(targetValue = if (isFocused) 1.5.dp else 1.dp, animationSpec = if (reduced) tween(0) else tween(CoffeeMotion.normal), label = "borderWidth")
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(CoffeeShapes.small)
            .background(colors.surface)
            .border(borderWidth, borderColor, CoffeeShapes.small)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        singleLine = singleLine,
        textStyle = CoffeeTheme.type.body.merge(androidx.compose.ui.text.TextStyle(color = colors.textPrimary)),
        cursorBrush = SolidColor(colors.accent),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        interactionSource = interactionSource,
        decorationBox = { inner ->
            if (value.isEmpty()) {
                AppText(text = hint, style = CoffeeTheme.type.body, color = colors.textSecondary)
            }
            inner()
        },
    )
}
