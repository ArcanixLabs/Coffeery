package co.coffeery.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import co.coffeery.app.data.model.Palette
import co.coffeery.app.data.model.ThemeMode

/** Entry point for the design system. Provides colours, type and spacing and
 *  keeps a single identity across light/dark rather than a bare inversion. */
@Composable
fun CoffeeTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    palette: Palette = Palette.TERRACOTTA,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val base = if (darkTheme) DarkCoffeeColors else LightCoffeeColors
    val accent = paletteAccent(palette, darkTheme)
    val accentSoft = if (darkTheme) accent.copy(alpha = 0.25f) else accent.copy(alpha = 0.15f)
    val colors = base.copy(
        accent = accent,
        onAccent = if (darkTheme) Color(0xFF1A0F0A) else Color.White,
        accentSoft = accentSoft,
        cremaLight = accent.copy(alpha = 0.4f),
    )
    CompositionLocalProvider(
        LocalCoffeeColors provides colors,
        LocalCoffeeTypography provides DefaultCoffeeTypography,
        LocalCoffeeSpacing provides CoffeeSpacing(),
        content = content,
    )
}

/** Ambient accessors, mirroring how MaterialTheme exposes its tokens. */
object CoffeeTheme {
    val colors: CoffeeColors
        @Composable @ReadOnlyComposable get() = LocalCoffeeColors.current
    val type: CoffeeTypography
        @Composable @ReadOnlyComposable get() = LocalCoffeeTypography.current
    val spacing: CoffeeSpacing
        @Composable @ReadOnlyComposable get() = LocalCoffeeSpacing.current
}

/** Default text style for the app (used by [co.coffeery.app.ui.components.AppText]). */
val DefaultTextStyle: TextStyle = DefaultCoffeeTypography.body
