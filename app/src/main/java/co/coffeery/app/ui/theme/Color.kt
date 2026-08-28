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
    val backgroundEnd: Color,   // slightly lighter/different for subtle gradient
    val surface: Color,
    val surfaceElevated: Color,
    val outline: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accent: Color,
    val accentVintage: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val cremaLight: Color,
    val cremaDark: Color,
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
    background = Color(0xFFF5F0E6),
    backgroundEnd = Color(0xFFFBF7F0),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE7DDCE),
    textPrimary = Color(0xFF201A14),
    textSecondary = Color(0xFF6E6152),
    accent = Color(0xFFC75B3C),
    accentVintage = Color(0xFFB14E33),
    accentSoft = Color(0xFFF3D3C4),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9B98C),
    cremaDark = Color(0xFF3B241A),
    isDark = false,
)

val DarkCoffeeColors = CoffeeColors(
    background = Color(0xFF1A1510),
    backgroundEnd = Color(0xFF1F1914),
    surface = Color(0xFF221C16),
    surfaceElevated = Color(0xFF2C241D),
    outline = Color(0xFF3F342A),
    textPrimary = Color(0xFFF5EDE3),
    textSecondary = Color(0xFFB8A895),
    accent = Color(0xFFE0785B),
    accentVintage = Color(0xFFCA6C52),
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
    Palette.COPPER -> if (isDark) DarkCopperColors else LightCopperColors
    Palette.CINNAMON -> if (isDark) DarkCinnamonColors else LightCinnamonColors
    Palette.CHESTNUT -> if (isDark) DarkChestnutColors else LightChestnutColors
    Palette.FRENCH_ROAST -> if (isDark) DarkFrenchRoastColors else LightFrenchRoastColors
    Palette.VANILLA_LATTE -> if (isDark) DarkVanillaLatteColors else LightVanillaLatteColors
    Palette.PUMPKIN_SPICE -> if (isDark) DarkPumpkinSpiceColors else LightPumpkinSpiceColors
    Palette.NORDIC_LIGHT -> if (isDark) DarkNordicLightColors else LightNordicLightColors
    Palette.KINETIC -> if (isDark) DarkKineticColors else LightKineticColors
    Palette.MIDNIGHT_BLOOM -> if (isDark) DarkMidnightBloomColors else LightMidnightBloomColors
    Palette.AURORA -> if (isDark) DarkAuroraColors else LightAuroraColors
}

val LightEspressoColors = CoffeeColors(
    background = Color(0xFFF0EBE2),
    backgroundEnd = Color(0xFFF5F0E8),
    surface = Color(0xFFFCFAF7),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE3D9CB),
    textPrimary = Color(0xFF1F1812),
    textSecondary = Color(0xFF6B5D4C),
    accent = Color(0xFF6F4E37),
    accentVintage = Color(0xFF644633),
    accentSoft = Color(0xFFE0CFBB),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFC9A07A),
    cremaDark = Color(0xFF3A2317),
    isDark = false,
)

val LightCopperColors = CoffeeColors(
    background = Color(0xFFEDE3D0),
    backgroundEnd = Color(0xFFF3E8CC),
    surface = Color(0xFFFDF9F0),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE6D9C2),
    textPrimary = Color(0xFF271E12),
    textSecondary = Color(0xFF7A6A52),
    accent = Color(0xFFC47A3A),
    accentVintage = Color(0xFFB06A30),
    accentSoft = Color(0xFFF0D9B8),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9B98C),
    cremaDark = Color(0xFF3B241A),
    isDark = false,
)

val DarkCopperColors = CoffeeColors(
    background = Color(0xFF1B1713),
    backgroundEnd = Color(0xFF211D18),
    surface = Color(0xFF251F1A),
    surfaceElevated = Color(0xFF2E261E),
    outline = Color(0xFF3A3228),
    textPrimary = Color(0xFFF0E6D2),
    textSecondary = Color(0xFFB8A890),
    accent = Color(0xFFD48A4A),
    accentVintage = Color(0xFFBC7A42),
    accentSoft = Color(0xFF5A3A1E),
    onAccent = Color(0xFF1A0F08),
    cremaLight = Color(0xFFC9A57A),
    cremaDark = Color(0xFF1C0F09),
    isDark = true,
)

val DarkEspressoColors = CoffeeColors(
    background = Color(0xFF1A1510),
    backgroundEnd = Color(0xFF1F1914),
    surface = Color(0xFF221C16),
    surfaceElevated = Color(0xFF2C241D),
    outline = Color(0xFF362D26),
    textPrimary = Color(0xFFF0E8DD),
    textSecondary = Color(0xFFB0A08C),
    accent = Color(0xFFA67B5B),
    accentVintage = Color(0xFF956E52),
    accentSoft = Color(0xFF5A3D2A),
    onAccent = Color(0xFF0D0805),
    cremaLight = Color(0xFFB8956E),
    cremaDark = Color(0xFF1A0D07),
    isDark = true,
)

val LightMatchaColors = CoffeeColors(
    background = Color(0xFFEEF2E8),
    backgroundEnd = Color(0xFFF4F7F0),
    surface = Color(0xFFFCFAF7),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFDDE8D4),
    textPrimary = Color(0xFF181C16),
    textSecondary = Color(0xFF5E6A52),
    accent = Color(0xFF4A7C59),
    accentVintage = Color(0xFF43704F),
    accentSoft = Color(0xFFCDE4CF),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFC5D8A0),
    cremaDark = Color(0xFF1E2D19),
    isDark = false,
)

val DarkMatchaColors = CoffeeColors(
    background = Color(0xFF161816),
    backgroundEnd = Color(0xFF1B1D1B),
    surface = Color(0xFF1D201C),
    surfaceElevated = Color(0xFF252823),
    outline = Color(0xFF2F362A),
    textPrimary = Color(0xFFEDF2E6),
    textSecondary = Color(0xFFA2AD94),
    accent = Color(0xFF6B9B6F),
    accentVintage = Color(0xFF608C64),
    accentSoft = Color(0xFF2A402C),
    onAccent = Color(0xFF0A0D08),
    cremaLight = Color(0xFFA0B878),
    cremaDark = Color(0xFF141C0C),
    isDark = true,
)

val LightBerryColors = CoffeeColors(
    background = Color(0xFFF2EEF2),
    backgroundEnd = Color(0xFFF8F4F7),
    surface = Color(0xFFFCFAF7),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFEBD8E2),
    textPrimary = Color(0xFF1C141A),
    textSecondary = Color(0xFF6E5262),
    accent = Color(0xFF8B3A62),
    accentVintage = Color(0xFF7D3458),
    accentSoft = Color(0xFFEBCDD9),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9B8C4),
    cremaDark = Color(0xFF2E1A26),
    isDark = false,
)

val DarkBerryColors = CoffeeColors(
    background = Color(0xFF1A1416),
    backgroundEnd = Color(0xFF1F191A),
    surface = Color(0xFF211A1E),
    surfaceElevated = Color(0xFF2A2226),
    outline = Color(0xFF362B30),
    textPrimary = Color(0xFFF2E9EE),
    textSecondary = Color(0xFFAF96A5),
    accent = Color(0xFFC77DB5),
    accentVintage = Color(0xFFB370A3),
    accentSoft = Color(0xFF5A3050),
    onAccent = Color(0xFF0D080C),
    cremaLight = Color(0xFFB88CA0),
    cremaDark = Color(0xFF1C0E17),
    isDark = true,
)

// ---- Crema — golden cream, inspired by espresso crema ----
val LightCremaColors = CoffeeColors(
    background = Color(0xFFF4EFE6),
    backgroundEnd = Color(0xFFFAF6EF),
    surface = Color(0xFFFCFAF7),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE5D9C3),
    textPrimary = Color(0xFF1E1710),
    textSecondary = Color(0xFF6E5E48),
    accent = Color(0xFFC4953C),
    accentVintage = Color(0xFFB08636),
    accentSoft = Color(0xFFF0DDB8),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFE8CC90),
    cremaDark = Color(0xFF3D2810),
    isDark = false,
)

val DarkCremaColors = CoffeeColors(
    background = Color(0xFF1A1610),
    backgroundEnd = Color(0xFF1F1A14),
    surface = Color(0xFF221D16),
    surfaceElevated = Color(0xFF2C251C),
    outline = Color(0xFF362E24),
    textPrimary = Color(0xFFF2ECDE),
    textSecondary = Color(0xFFB0A080),
    accent = Color(0xFFD9B050),
    accentVintage = Color(0xFFC39E48),
    accentSoft = Color(0xFF5A4028),
    onAccent = Color(0xFF0D0803),
    cremaLight = Color(0xFFB89560),
    cremaDark = Color(0xFF1C0E05),
    isDark = true,
)

// ---- Mocha — rich dark chocolate with deep warm browns ----
val LightMochaColors = CoffeeColors(
    background = Color(0xFFEDE8DF),
    backgroundEnd = Color(0xFFF3EFE8),
    surface = Color(0xFFFCFAF7),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFDFD6C8),
    textPrimary = Color(0xFF1D1610),
    textSecondary = Color(0xFF685944),
    accent = Color(0xFF8B5E3C),
    accentVintage = Color(0xFF7D5536),
    accentSoft = Color(0xFFDCC6B0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD4A878),
    cremaDark = Color(0xFF382218),
    isDark = false,
)

val DarkMochaColors = CoffeeColors(
    background = Color(0xFF191510),
    backgroundEnd = Color(0xFF1E1914),
    surface = Color(0xFF211C16),
    surfaceElevated = Color(0xFF2A241C),
    outline = Color(0xFF332922),
    textPrimary = Color(0xFFEFE8DD),
    textSecondary = Color(0xFFAD9A85),
    accent = Color(0xFFB07A4E),
    accentVintage = Color(0xFF9E6D46),
    accentSoft = Color(0xFF543826),
    onAccent = Color(0xFF0B0603),
    cremaLight = Color(0xFFAB8460),
    cremaDark = Color(0xFF190D06),
    isDark = true,
)

// ---- Caramel — warm amber caramel, sweet and golden ----
val LightCaramelColors = CoffeeColors(
    background = Color(0xFFF5EFE4),
    backgroundEnd = Color(0xFFFBF5ED),
    surface = Color(0xFFFCFAF7),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE8D8BE),
    textPrimary = Color(0xFF1F1710),
    textSecondary = Color(0xFF6F5D44),
    accent = Color(0xFFC77D24),
    accentVintage = Color(0xFFB37020),
    accentSoft = Color(0xFFF0D3A5),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFE4BE80),
    cremaDark = Color(0xFF3C2410),
    isDark = false,
)

val DarkCaramelColors = CoffeeColors(
    background = Color(0xFF1A1610),
    backgroundEnd = Color(0xFF1F1A14),
    surface = Color(0xFF221C16),
    surfaceElevated = Color(0xFF2B241C),
    outline = Color(0xFF352B22),
    textPrimary = Color(0xFFF2EADD),
    textSecondary = Color(0xFFAFA080),
    accent = Color(0xFFD4953C),
    accentVintage = Color(0xFFBF8636),
    accentSoft = Color(0xFF5C4024),
    onAccent = Color(0xFF0C0703),
    cremaLight = Color(0xFFB89058),
    cremaDark = Color(0xFF1C0D05),
    isDark = true,
)

// ---- Hazelnut — warm nutty brown, softer than espresso ----
val LightHazelnutColors = CoffeeColors(
    background = Color(0xFFF0EBE3),
    backgroundEnd = Color(0xFFF6F2EB),
    surface = Color(0xFFFCFAF7),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE2D8C8),
    textPrimary = Color(0xFF1E1711),
    textSecondary = Color(0xFF6B5C48),
    accent = Color(0xFF9B7446),
    accentVintage = Color(0xFF8C683F),
    accentSoft = Color(0xFFE2CFB4),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD0B080),
    cremaDark = Color(0xFF392518),
    isDark = false,
)

val DarkHazelnutColors = CoffeeColors(
    background = Color(0xFF191511),
    backgroundEnd = Color(0xFF1E1915),
    surface = Color(0xFF211C17),
    surfaceElevated = Color(0xFF2A241D),
    outline = Color(0xFF342A21),
    textPrimary = Color(0xFFEFE7DD),
    textSecondary = Color(0xFFAE9C85),
    accent = Color(0xFFBB8E5E),
    accentVintage = Color(0xFFA88055),
    accentSoft = Color(0xFF553A27),
    onAccent = Color(0xFF0A0603),
    cremaLight = Color(0xFFA88860),
    cremaDark = Color(0xFF180D06),
    isDark = true,
)

val LightCinnamonColors = CoffeeColors(
    background = Color(0xFFF5E8D9),
    backgroundEnd = Color(0xFFF9EFE6),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE6D4BE),
    textPrimary = Color(0xFF23190F),
    textSecondary = Color(0xFF6E5A42),
    accent = Color(0xFFC07A2E),
    accentVintage = Color(0xFFAD6E2A),
    accentSoft = Color(0xFFF0D8B0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9B98C),
    cremaDark = Color(0xFF3B241A),
    isDark = false,
)

val DarkCinnamonColors = CoffeeColors(
    background = Color(0xFF1C1610),
    backgroundEnd = Color(0xFF221C14),
    surface = Color(0xFF271F15),
    surfaceElevated = Color(0xFF30291D),
    outline = Color(0xFF3D2F20),
    textPrimary = Color(0xFFF2E6D5),
    textSecondary = Color(0xFFB59A7A),
    accent = Color(0xFFDFA065),
    accentVintage = Color(0xFFC78F55),
    accentSoft = Color(0xFF5A3A1E),
    onAccent = Color(0xFF120C05),
    cremaLight = Color(0xFFC9A57A),
    cremaDark = Color(0xFF1C0F09),
    isDark = true,
)

val LightChestnutColors = CoffeeColors(
    background = Color(0xFFF0E6D9),
    backgroundEnd = Color(0xFFF5EDE2),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE0D0BB),
    textPrimary = Color(0xFF241A0F),
    textSecondary = Color(0xFF6B5944),
    accent = Color(0xFF8B5A2B),
    accentVintage = Color(0xFF7D5127),
    accentSoft = Color(0xFFE8D2B8),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD4A878),
    cremaDark = Color(0xFF3A2415),
    isDark = false,
)

val DarkChestnutColors = CoffeeColors(
    background = Color(0xFF1A1410),
    backgroundEnd = Color(0xFF1F1912),
    surface = Color(0xFF241E16),
    surfaceElevated = Color(0xFF2D251C),
    outline = Color(0xFF3A2E20),
    textPrimary = Color(0xFFF0E6D5),
    textSecondary = Color(0xFFB5A090),
    accent = Color(0xFFB87A3A),
    accentVintage = Color(0xFFA66D34),
    accentSoft = Color(0xFF5A3A22),
    onAccent = Color(0xFF120A05),
    cremaLight = Color(0xFFC9A57A),
    cremaDark = Color(0xFF1C0F09),
    isDark = true,
)

val LightFrenchRoastColors = CoffeeColors(
    background = Color(0xFFECE5DC),
    backgroundEnd = Color(0xFFF1EAE2),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFDAD0C2),
    textPrimary = Color(0xFF1E1410),
    textSecondary = Color(0xFF6B5E52),
    accent = Color(0xFF4A2C1D),
    accentVintage = Color(0xFF42281A),
    accentSoft = Color(0xFFDED0C0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFC9A07A),
    cremaDark = Color(0xFF2A1A10),
    isDark = false,
)

val DarkFrenchRoastColors = CoffeeColors(
    background = Color(0xFF14100E),
    backgroundEnd = Color(0xFF1A1512),
    surface = Color(0xFF1E1912),
    surfaceElevated = Color(0xFF262019),
    outline = Color(0xFF332A20),
    textPrimary = Color(0xFFEEE6DA),
    textSecondary = Color(0xFFA89A8A),
    accent = Color(0xFF8A5A3C),
    accentVintage = Color(0xFF7D5136),
    accentSoft = Color(0xFF3A2A1E),
    onAccent = Color(0xFFF5EDE3),
    cremaLight = Color(0xFFB8956E),
    cremaDark = Color(0xFF1A0D07),
    isDark = true,
)

val LightVanillaLatteColors = CoffeeColors(
    background = Color(0xFFF6E8C8),
    backgroundEnd = Color(0xFFFCF2DD),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE8D8B8),
    textPrimary = Color(0xFF241E12),
    textSecondary = Color(0xFF7A6A52),
    accent = Color(0xFFC9A86A),
    accentVintage = Color(0xFFB5975F),
    accentSoft = Color(0xFFF0E0B8),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9C08C),
    cremaDark = Color(0xFF4A3520),
    isDark = false,
)

val DarkVanillaLatteColors = CoffeeColors(
    background = Color(0xFF1C1912),
    backgroundEnd = Color(0xFF221E16),
    surface = Color(0xFF28231A),
    surfaceElevated = Color(0xFF312B1E),
    outline = Color(0xFF3D3526),
    textPrimary = Color(0xFFF5EEDD),
    textSecondary = Color(0xFFB8A890),
    accent = Color(0xFFD8BC86),
    accentVintage = Color(0xFFC2A978),
    accentSoft = Color(0xFF5A4A2E),
    onAccent = Color(0xFF1A1206),
    cremaLight = Color(0xFFC9A57A),
    cremaDark = Color(0xFF1C0F09),
    isDark = true,
)

val LightPumpkinSpiceColors = CoffeeColors(
    background = Color(0xFFF3E6D9),
    backgroundEnd = Color(0xFFF8EDE2),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE8D2B8),
    textPrimary = Color(0xFF2A1A0F),
    textSecondary = Color(0xFF7A5A42),
    accent = Color(0xFFD86C27),
    accentVintage = Color(0xFFC26123),
    accentSoft = Color(0xFFF5D0B0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9A86A),
    cremaDark = Color(0xFF4A2510),
    isDark = false,
)

val DarkPumpkinSpiceColors = CoffeeColors(
    background = Color(0xFF1E1510),
    backgroundEnd = Color(0xFF241A12),
    surface = Color(0xFF2A1F14),
    surfaceElevated = Color(0xFF332618),
    outline = Color(0xFF423022),
    textPrimary = Color(0xFFF2E6D5),
    textSecondary = Color(0xFFB89A80),
    accent = Color(0xFFE8843A),
    accentVintage = Color(0xFFD07734),
    accentSoft = Color(0xFF5A3A20),
    onAccent = Color(0xFF140A03),
    cremaLight = Color(0xFFC9A57A),
    cremaDark = Color(0xFF1C0F09),
    isDark = true,
)

val LightNordicLightColors = CoffeeColors(
    background = Color(0xFFF9F6F0),
    backgroundEnd = Color(0xFFFEFDFB),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE8E0D4),
    textPrimary = Color(0xFF2A251E),
    textSecondary = Color(0xFF7A7064),
    accent = Color(0xFF9AA89E),
    accentVintage = Color(0xFF8A9890),
    accentSoft = Color(0xFFDDE6E0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD9C5A5),
    cremaDark = Color(0xFF3A352E),
    isDark = false,
)

val DarkNordicLightColors = CoffeeColors(
    background = Color(0xFF1E1E1C),
    backgroundEnd = Color(0xFF24221F),
    surface = Color(0xFF2A2825),
    surfaceElevated = Color(0xFF32302C),
    outline = Color(0xFF3D3A35),
    textPrimary = Color(0xFFF0EEE8),
    textSecondary = Color(0xFFB8B2A5),
    accent = Color(0xFFB5C2B5),
    accentVintage = Color(0xFFA3AF9F),
    accentSoft = Color(0xFF3A4038),
    onAccent = Color(0xFF1A1E1A),
    cremaLight = Color(0xFFC9B99A),
    cremaDark = Color(0xFF1C1A15),
    isDark = true,
)

val LightKineticColors = CoffeeColors(
    background = Color(0xFFF0EDF5),
    backgroundEnd = Color(0xFFF6F4FA),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE0D8E8),
    textPrimary = Color(0xFF221E2A),
    textSecondary = Color(0xFF6B6580),
    accent = Color(0xFF7B6B8A),
    accentVintage = Color(0xFF6E607C),
    accentSoft = Color(0xFFDAD4E8),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFC9B5D0),
    cremaDark = Color(0xFF3A2E48),
    isDark = false,
)

val DarkKineticColors = CoffeeColors(
    background = Color(0xFF1C1A22),
    backgroundEnd = Color(0xFF201E28),
    surface = Color(0xFF262430),
    surfaceElevated = Color(0xFF2E2C38),
    outline = Color(0xFF3A3848),
    textPrimary = Color(0xFFEDE8F5),
    textSecondary = Color(0xFFA8A2B8),
    accent = Color(0xFF9A8BB5),
    accentVintage = Color(0xFF8A7DA3),
    accentSoft = Color(0xFF3A3050),
    onAccent = Color(0xFF140F1E),
    cremaLight = Color(0xFFB5A6C8),
    cremaDark = Color(0xFF1A1520),
    isDark = true,
)

val LightMidnightBloomColors = CoffeeColors(
    background = Color(0xFFF2EDF5),
    backgroundEnd = Color(0xFFF8F4FA),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE0D8E8),
    textPrimary = Color(0xFF241E2C),
    textSecondary = Color(0xFF6B5E7A),
    accent = Color(0xFF6B5A7A),
    accentVintage = Color(0xFF60516E),
    accentSoft = Color(0xFFD8D0E0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFD0B8D8),
    cremaDark = Color(0xFF3E2E48),
    isDark = false,
)

val DarkMidnightBloomColors = CoffeeColors(
    background = Color(0xFF181420),
    backgroundEnd = Color(0xFF1E1A28),
    surface = Color(0xFF221E30),
    surfaceElevated = Color(0xFF2A2538),
    outline = Color(0xFF352E48),
    textPrimary = Color(0xFFEEE8F5),
    textSecondary = Color(0xFFA8A0B8),
    accent = Color(0xFF9A82B0),
    accentVintage = Color(0xFF8A759E),
    accentSoft = Color(0xFF3A2E50),
    onAccent = Color(0xFF120E18),
    cremaLight = Color(0xFFB8A6C8),
    cremaDark = Color(0xFF1A1520),
    isDark = true,
)

val LightAuroraColors = CoffeeColors(
    background = Color(0xFFF6EFE8),
    backgroundEnd = Color(0xFFFBF8F2),
    surface = Color(0xFFFDFAF5),
    surfaceElevated = Color(0xFFFFFFFF),
    outline = Color(0xFFE8DED4),
    textPrimary = Color(0xFF2A2018),
    textSecondary = Color(0xFF7A6E64),
    accent = Color(0xFFD9B8A0),
    accentVintage = Color(0xFFC3A690),
    accentSoft = Color(0xFFEDDDD0),
    onAccent = Color(0xFFFFFFFF),
    cremaLight = Color(0xFFE8C9A8),
    cremaDark = Color(0xFF4A3528),
    isDark = false,
)

val DarkAuroraColors = CoffeeColors(
    background = Color(0xFF1E1C1A),
    backgroundEnd = Color(0xFF24211E),
    surface = Color(0xFF2A2622),
    surfaceElevated = Color(0xFF322E28),
    outline = Color(0xFF3D3832),
    textPrimary = Color(0xFFF0E8E0),
    textSecondary = Color(0xFFB8AEA2),
    accent = Color(0xFFE8C9A8),
    accentVintage = Color(0xFFD0B598),
    accentSoft = Color(0xFF4A3A2E),
    onAccent = Color(0xFF1A1612),
    cremaLight = Color(0xFFD9B8A0),
    cremaDark = Color(0xFF1C1510),
    isDark = true,
)
