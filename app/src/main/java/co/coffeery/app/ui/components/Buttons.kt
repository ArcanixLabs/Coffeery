package co.coffeery.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import co.coffeery.app.ui.haptic.rememberAppHaptics
import co.coffeery.app.ui.theme.CoffeeMotion
import co.coffeery.app.ui.theme.CoffeeShapes
import co.coffeery.app.ui.theme.CoffeeTheme

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = CoffeeShapes.pill,
    onClick: () -> Unit,
) {
    val colors = CoffeeTheme.colors
    val haptics = rememberAppHaptics()
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val sx by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, animationSpec = CoffeeMotion.press, label = "pressX")
    val sy by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, animationSpec = CoffeeMotion.press, label = "pressY")
    Box(
        modifier = modifier
            .graphicsLayer(scaleX = sx, scaleY = sy)
            .defaultMinSize(minWidth = 120.dp)
            .clip(shape)
            .background(if (enabled) colors.accent else colors.outline)
            .clickable(enabled = enabled, interactionSource = interactionSource, indication = null) {
                haptics.tap()
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 24.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = text,
            style = CoffeeTheme.type.headline,
            color = if (enabled) colors.onAccent else colors.textSecondary,
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val colors = CoffeeTheme.colors
    val haptics = rememberAppHaptics()
    val hapticFeedback = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val sx by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, animationSpec = CoffeeMotion.press, label = "pressX")
    val sy by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, animationSpec = CoffeeMotion.press, label = "pressY")
    Box(
        modifier = modifier
            .graphicsLayer(scaleX = sx, scaleY = sy)
            .defaultMinSize(minWidth = 120.dp)
            .clip(CoffeeShapes.pill)
            .background(colors.surface)
            .border(1.5.dp, colors.outline, CoffeeShapes.pill)
            .clickable(enabled = enabled, interactionSource = interactionSource, indication = null) {
                haptics.tap()
                hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 24.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = text,
            style = CoffeeTheme.type.headline,
            color = if (enabled) colors.textPrimary else colors.textSecondary,
        )
    }
}
