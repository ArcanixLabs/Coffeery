package co.coffeery.app.ui.screens.equipment

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.coffeery.app.ui.haptic.rememberAppHaptics
import co.coffeery.app.R
import co.coffeery.app.data.model.Equipment
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.AppTextField
import co.coffeery.app.ui.components.CoffeeCard
import co.coffeery.app.ui.components.CremaMascot
import co.coffeery.app.ui.components.EquipmentIcon
import co.coffeery.app.ui.components.PrimaryButton
import co.coffeery.app.ui.components.ScreenHeader
import co.coffeery.app.ui.components.displayName
import co.coffeery.app.ui.components.displayTag
import co.coffeery.app.ui.screens.root.AppUiState
import co.coffeery.app.ui.screens.root.AppViewModel
import co.coffeery.app.ui.screens.root.Route
import co.coffeery.app.ui.theme.CoffeeShapes
import co.coffeery.app.ui.theme.CoffeeTheme
import java.util.Locale

@Composable
fun EquipmentScreen(state: AppUiState, vm: AppViewModel) {
    val colors = CoffeeTheme.colors
    val ctx = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val query = searchQuery.lowercase(Locale.ROOT)
    val searchActive = query.isNotBlank()
    val filteredEquipment = if (!searchActive) state.equipment else state.equipment.filter { eq ->
        val name = eq.displayName().lowercase(Locale.ROOT)
        val tag = eq.displayTag()?.lowercase(Locale.ROOT) ?: ""
        val cat = stringResource(eq.category.labelRes).lowercase(Locale.ROOT)
        val id = eq.id.lowercase(Locale.ROOT)
        name.contains(query) || tag.contains(query) || cat.contains(query) || id.contains(query)
    }
    val filteredBuiltIns = filteredEquipment.filter { !it.isCustom }
    val filteredCustom = filteredEquipment.filter { it.isCustom }
    val popularIds = listOf("v60", "chemex", "frenchpress", "aeropress", "espresso", "moka", "coldbrew", "turkish")
    val quickItems = filteredEquipment.filter { it.id in popularIds }.sortedBy { popularIds.indexOf(it.id) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(4.dp)) }
        item(span = { GridItemSpan(maxLineSpan) }) { ScreenHeader(title = stringResource(R.string.equipment_title)) }
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AppTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    hint = stringResource(R.string.search_hint_equipment),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (searchQuery.isNotEmpty()) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { searchQuery = "" },
                    ) { AppText("✕", style = CoffeeTheme.type.headline, color = colors.textSecondary) }
                }
            }
        }
        if (quickItems.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) { AppText(stringResource(R.string.equipment_quick_methods), style = CoffeeTheme.type.headline, color = colors.textSecondary) }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    quickItems.forEach { eq ->
                        val selected = eq.id == state.selectedEquipmentId
                        CoffeeCard(onClick = { vm.selectEquipment(eq.id) }, modifier = if (selected) Modifier.border(2.dp, colors.accent, CoffeeShapes.medium) else Modifier) {
                            EquipmentIcon(eq, colors.accent, Modifier.size(32.dp))
                            Spacer(Modifier.height(4.dp))
                            AppText(eq.displayName(), style = CoffeeTheme.type.caption, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                            val vUrl1 = eq.videoUrl()
                            if (vUrl1 != null) {
                                Spacer(Modifier.height(2.dp))
                                AppText(stringResource(R.string.calc_watch_video), style = CoffeeTheme.type.caption, color = colors.accent, modifier = Modifier.clip(CoffeeShapes.pill).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(vUrl1))) }.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) { PrimaryButton(text = stringResource(R.string.equipment_add), modifier = Modifier.fillMaxWidth()) { vm.openRoute(Route.AddEquipment) } }
        item(span = { GridItemSpan(maxLineSpan) }) { AppText(stringResource(R.string.equipment_builtin), style = CoffeeTheme.type.headline, color = colors.textSecondary) }
        if (filteredBuiltIns.isEmpty() && searchActive) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CremaMascot(mood = "curious", modifier = Modifier.size(96.dp))
                    Spacer(Modifier.height(10.dp))
                    AppText(stringResource(R.string.search_no_results), style = CoffeeTheme.type.body, color = colors.textSecondary, modifier = Modifier.fillMaxWidth(), align = TextAlign.Center)
                }
            }
        } else {
            items(filteredBuiltIns, key = { it.id }) { eq ->
                GearTile(eq, selected = eq.id == state.selectedEquipmentId, modifier = Modifier.animateItem()) { vm.selectEquipment(eq.id) }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) { AppText(stringResource(R.string.equipment_your), style = CoffeeTheme.type.headline, color = colors.textSecondary) }
        if (filteredCustom.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    CremaMascot(mood = if (searchActive) "curious" else "sleepy", modifier = Modifier.size(96.dp))
                    Spacer(Modifier.height(10.dp))
                    if (searchActive) {
                        AppText(stringResource(R.string.search_no_results), style = CoffeeTheme.type.body, color = colors.textSecondary, modifier = Modifier.fillMaxWidth(), align = TextAlign.Center)
                    } else {
                        AppText(stringResource(R.string.equipment_your), style = CoffeeTheme.type.headline, color = colors.textSecondary, modifier = Modifier.fillMaxWidth(), align = TextAlign.Center)
                        Spacer(Modifier.height(4.dp))
                        AppText(stringResource(R.string.search_no_results), style = CoffeeTheme.type.body, color = colors.textSecondary, modifier = Modifier.fillMaxWidth(), align = TextAlign.Center)
                        Spacer(Modifier.height(12.dp))
                        PrimaryButton(text = stringResource(R.string.equipment_add), modifier = Modifier.fillMaxWidth()) { vm.openRoute(Route.AddEquipment) }
                    }
                }
            }
        } else {
            items(filteredCustom, key = { it.id }, span = { GridItemSpan(maxLineSpan) }) { eq ->
                CustomGearRow(eq = eq, selected = eq.id == state.selectedEquipmentId, modifier = Modifier.animateItem(), onClick = { vm.selectEquipment(eq.id) }, onDelete = { vm.deleteCustomEquipment(eq.id) })
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(88.dp)) }
    }
}

@Composable
private fun GearTile(eq: Equipment, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = CoffeeTheme.colors
    val ctx = LocalContext.current
    val outer = if (selected) modifier.border(2.dp, colors.accent, CoffeeShapes.medium) else modifier
    CoffeeCard(onClick = onClick, modifier = outer) {
        EquipmentIcon(eq, colors.accent, Modifier.size(30.dp))
        Spacer(Modifier.height(10.dp))
        AppText(eq.displayName(), style = CoffeeTheme.type.headline, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        val tag = eq.displayTag()
        if (tag != null) AppText(tag, style = CoffeeTheme.type.caption, color = colors.textSecondary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis) else AppText(stringResource(eq.category.labelRes), style = CoffeeTheme.type.caption, color = colors.textSecondary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
        val vUrl2 = eq.videoUrl()
        if (vUrl2 != null) {
            Spacer(Modifier.height(4.dp))
            AppText(stringResource(R.string.equip_watch_video), style = CoffeeTheme.type.label, color = colors.accent, modifier = Modifier.clip(CoffeeShapes.pill).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(vUrl2))) }.padding(horizontal = 6.dp, vertical = 4.dp))
        }
    }
}

@Composable
private fun CustomGearRow(eq: Equipment, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit, onDelete: () -> Unit) {
    val colors = CoffeeTheme.colors
    val haptics = rememberAppHaptics()
    val outerBase = if (selected) modifier.fillMaxWidth().border(2.dp, colors.accent, CoffeeShapes.medium) else modifier.fillMaxWidth()
    CoffeeCard(onClick = onClick, modifier = outerBase) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            EquipmentIcon(eq, colors.accent, Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                AppText(eq.displayName(), style = CoffeeTheme.type.headline, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                AppText(stringResource(eq.category.labelRes), style = CoffeeTheme.type.caption, color = colors.textSecondary, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
            }
            Box(modifier = Modifier.size(38.dp).clip(CoffeeShapes.pill).clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { haptics.tap(); onDelete() }, contentAlignment = Alignment.Center) {
                AppText(text = "✕", style = CoffeeTheme.type.headline, color = colors.textSecondary)
            }
        }
    }
}
