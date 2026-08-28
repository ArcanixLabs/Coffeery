package co.coffeery.app.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import co.coffeery.app.data.model.Equipment

/** Resolves a localized name for built-in gear, or the raw name for custom gear. */
@Composable
fun Equipment.displayName(): String =
    customName?.takeIf { it.isNotBlank() } ?: if (nameRes != 0) stringResource(nameRes) else "Deleted brewer"

@Composable
fun Equipment.displayTag(): String? =
    if (isCustom || tagRes == 0) null else stringResource(tagRes)

@Composable
fun resolveEquipmentName(equipment: List<Equipment>, id: String?): String =
    equipment.firstOrNull { it.id == id }?.displayName() ?: "Deleted brewer"

fun resolveEquipmentName(context: android.content.Context, equipment: List<Equipment>, id: String?): String {
    val eq = equipment.firstOrNull { it.id == id } ?: return "Deleted brewer"
    val custom = eq.customName?.takeIf { it.isNotBlank() }
    if (custom != null) return custom
    return if (eq.nameRes != 0) try { context.getString(eq.nameRes) } catch (_: Exception) { "Deleted brewer" } else "Deleted brewer"
}
