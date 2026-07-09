package co.coffeery.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import co.coffeery.app.data.model.Palette

/**
 * Coffeery's own colour tokens — deliberately not Material's ColorScheme.
 * A warm, low-chroma base with a single terracotta accent, so light and dark
 * share one identity rather than being a plain inversion.
 */
@Immutable
data class CoffeeColors(
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val outline: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val cremaLight: Color,   // slider fill at min strength
    val cremaDark: Color,    // slider fill at max strength
    val isDark: Boolean,
) {
    /** Coffee shade for the strength slider fill; darkens as strength rises. */
    fun coffeeFor(strength: Float): Color =
        lerp(cremaLight, cremaDark, strength.coerceIn(0f, 1f))

    /** Text colour that stays readable over [coffeeFor] at any strength. */
    fun coffeeTextFor(strength: Float): Color =
        if (strength.coerceIn(0f, 1f) > 0.45f) {
            if (isDark) lerp(cremaLight, textPrimary, strength.coerceIn(0f, 1f))
            else onAccent
        } else textPrimary
}

val LightCoffeeColors = CoffeeColors(
    background = Color(0xFFFBF7F0),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE7DDCE),
    textPrimary = Color(0xFF201A14),
    textSecondary = Color(0xFF6E6152),
    accent = Color(0xFFC75B3C),
    accentSoft = Color(0xFFF3D3C4),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9B98C),
    cremaDark = Color(0xFF3B241A),
    isDark = false,
)

val DarkCoffeeColors = CoffeeColors(
    background = Color(0xFF14100D),
    surface = Color(0xFF1E1813),
    surfaceElevated = Color(0xFF2A221B),
    outline = Color(0xFF3A2F26),
    textPrimary = Color(0xFFF5EDE3),
    textSecondary = Color(0xFFB8A895),
    accent = Color(0xFFE0785B),
    accentSoft = Color(0xFF6B3628),
    onAccent = Color(0xFF1A0F0A),
    cremaLight = Color(0xFFC9A57A),
    cremaDark = Color(0xFF1C0F09),
    isDark = true,
)

val LocalCoffeeColors = staticCompositionLocalOf { LightCoffeeColors }

fun paletteColors(palette: Palette, isDark: Boolean): CoffeeColors = when (palette) {
    Palette.TERRACOTTA -> if (isDark) DarkCoffeeColors else LightCoffeeColors
    Palette.ESPRESSO -> if (isDark) DarkEspressoColors else LightEspressoColors
    Palette.MATCHA -> if (isDark) DarkMatchaColors else LightMatchaColors
    Palette.BERRY -> if (isDark) DarkBerryColors else LightBerryColors
    Palette.CREMA -> if (isDark) DarkCremaColors else LightCremaColors
    Palette.MOCHA -> if (isDark) DarkMochaColors else LightMochaColors
    Palette.CARAMEL -> if (isDark) DarkCaramelColors else LightCaramelColors
    Palette.HAZELNUT -> if (isDark) DarkHazelnutColors else LightHazelnutColors
}

val LightEspressoColors = CoffeeColors(
    background = Color(0xFFF5F0E8),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE3D9CB),
    textPrimary = Color(0xFF1F1812),
    textSecondary = Color(0xFF6B5D4C),
    accent = Color(0xFF6F4E37),
    accentSoft = Color(0xFFE0CFBB),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFC9A07A),
    cremaDark = Color(0xFF3A2317),
    isDark = false,
)

val DarkEspressoColors = CoffeeColors(
    background = Color(0xFF0D0A08),
    surface = Color(0xFF1A1410),
    surfaceElevated = Color(0xFF241C17),
    outline = Color(0xFF362D26),
    textPrimary = Color(0xFFF0E8DD),
    textSecondary = Color(0xFFB0A08C),
    accent = Color(0xFFA67B5B),
    accentSoft = Color(0xFF5A3D2A),
    onAccent = Color(0xFF0D0805),
    cremaLight = Color(0xFFB8956E),
    cremaDark = Color(0xFF1A0D07),
    isDark = true,
)

val LightMatchaColors = CoffeeColors(
    background = Color(0xFFF4F7F0),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFDDE8D4),
    textPrimary = Color(0xFF181C16),
    textSecondary = Color(0xFF5E6A52),
    accent = Color(0xFF4A7C59),
    accentSoft = Color(0xFFCDE4CF),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFC5D8A0),
    cremaDark = Color(0xFF1E2D19),
    isDark = false,
)

val DarkMatchaColors = CoffeeColors(
    background = Color(0xFF0D100C),
    surface = Color(0xFF161A14),
    surfaceElevated = Color(0xFF1E241B),
    outline = Color(0xFF2F362A),
    textPrimary = Color(0xFFEDF2E6),
    textSecondary = Color(0xFFA2AD94),
    accent = Color(0xFF6B9B6F),
    accentSoft = Color(0xFF2A402C),
    onAccent = Color(0xFF0A0D08),
    cremaLight = Color(0xFFA0B878),
    cremaDark = Color(0xFF141C0C),
    isDark = true,
)

val LightBerryColors = CoffeeColors(
    background = Color(0xFFF8F4F7),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFEBD8E2),
    textPrimary = Color(0xFF1C141A),
    textSecondary = Color(0xFF6E5262),
    accent = Color(0xFF8B3A62),
    accentSoft = Color(0xFFEBCDD9),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9B8C4),
    cremaDark = Color(0xFF2E1A26),
    isDark = false,
)

val DarkBerryColors = CoffeeColors(
    background = Color(0xFF100C0F),
    surface = Color(0xFF1A1418),
    surfaceElevated = Color(0xFF241C20),
    outline = Color(0xFF362B30),
    textPrimary = Color(0xFFF2E9EE),
    textSecondary = Color(0xFFAF96A5),
    accent = Color(0xFFC77DB5),
    accentSoft = Color(0xFF5A3050),
    onAccent = Color(0xFF0D080C),
    cremaLight = Color(0xFFB88CA0),
    cremaDark = Color(0xFF1C0E17),
    isDark = true,
)

// ---- Crema — golden cream, inspired by espresso crema ----
val LightCremaColors = CoffeeColors(
    background = Color(0xFFFAF6EF),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE5D9C3),
    textPrimary = Color(0xFF1E1710),
    textSecondary = Color(0xFF6E5E48),
    accent = Color(0xFFC4953C),
    accentSoft = Color(0xFFF0DDB8),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFE8CC90),
    cremaDark = Color(0xFF3D2810),
    isDark = false,
)

val DarkCremaColors = CoffeeColors(
    background = Color(0xFF100D08),
    surface = Color(0xFF1A1611),
    surfaceElevated = Color(0xFF241F18),
    outline = Color(0xFF362E24),
    textPrimary = Color(0xFFF2ECDE),
    textSecondary = Color(0xFFB0A080),
    accent = Color(0xFFD9B050),
    accentSoft = Color(0xFF5A4028),
    onAccent = Color(0xFF0D0803),
    cremaLight = Color(0xFFB89560),
    cremaDark = Color(0xFF1C0E05),
    isDark = true,
)

// ---- Mocha — rich dark chocolate with deep warm browns ----
val LightMochaColors = CoffeeColors(
    background = Color(0xFFF3EFE8),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFDFD6C8),
    textPrimary = Color(0xFF1D1610),
    textSecondary = Color(0xFF685944),
    accent = Color(0xFF8B5E3C),
    accentSoft = Color(0xFFDCC6B0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD4A878),
    cremaDark = Color(0xFF382218),
    isDark = false,
)

val DarkMochaColors = CoffeeColors(
    background = Color(0xFF0C0907),
    surface = Color(0xFF181311),
    surfaceElevated = Color(0xFF201A16),
    outline = Color(0xFF332922),
    textPrimary = Color(0xFFEFE8DD),
    textSecondary = Color(0xFFAD9A85),
    accent = Color(0xFFB07A4E),
    accentSoft = Color(0xFF543826),
    onAccent = Color(0xFF0B0603),
    cremaLight = Color(0xFFAB8460),
    cremaDark = Color(0xFF190D06),
    isDark = true,
)

// ---- Caramel — warm amber caramel, sweet and golden ----
val LightCaramelColors = CoffeeColors(
    background = Color(0xFFFBF5ED),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE8D8BE),
    textPrimary = Color(0xFF1F1710),
    textSecondary = Color(0xFF6F5D44),
    accent = Color(0xFFC77D24),
    accentSoft = Color(0xFFF0D3A5),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFE4BE80),
    cremaDark = Color(0xFF3C2410),
    isDark = false,
)

val DarkCaramelColors = CoffeeColors(
    background = Color(0xFF0F0C07),
    surface = Color(0xFF191410),
    surfaceElevated = Color(0xFF221B15),
    outline = Color(0xFF352B22),
    textPrimary = Color(0xFFF2EADD),
    textSecondary = Color(0xFFAFA080),
    accent = Color(0xFFD4953C),
    accentSoft = Color(0xFF5C4024),
    onAccent = Color(0xFF0C0703),
    cremaLight = Color(0xFFB89058),
    cremaDark = Color(0xFF1C0D05),
    isDark = true,
)

// ---- Hazelnut — warm nutty brown, softer than espresso ----
val LightHazelnutColors = CoffeeColors(
    background = Color(0xFFF6F2EB),
    surface = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE2D8C8),
    textPrimary = Color(0xFF1E1711),
    textSecondary = Color(0xFF6B5C48),
    accent = Color(0xFF9B7446),
    accentSoft = Color(0xFFE2CFB4),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD0B080),
    cremaDark = Color(0xFF392518),
    isDark = false,
)

val DarkHazelnutColors = CoffeeColors(
    background = Color(0xFF0C0A07),
    surface = Color(0xFF181411),
    surfaceElevated = Color(0xFF201A16),
    outline = Color(0xFF342A21),
    textPrimary = Color(0xFFEFE7DD),
    textSecondary = Color(0xFFAE9C85),
    accent = Color(0xFFBB8E5E),
    accentSoft = Color(0xFF553A27),
    onAccent = Color(0xFF0A0603),
    cremaLight = Color(0xFFA88860),
    cremaDark = Color(0xFF180D06),
    isDark = true,
)
