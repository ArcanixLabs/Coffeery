package co.coffeery.app.ui.theme

import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

val LocalPrefersReducedMotion = staticCompositionLocalOf { false }

@Composable
fun rememberPrefersReducedMotion(): Boolean {
    val context = LocalContext.current
    return try {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    } catch (_: Exception) {
        false
    }
}
