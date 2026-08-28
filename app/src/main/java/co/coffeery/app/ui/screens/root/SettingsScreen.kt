package co.coffeery.app.ui.screens.root

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.coffeery.app.BuildConfig
import co.coffeery.app.R
import co.coffeery.app.data.model.Palette
import co.coffeery.app.data.model.ThemeMode
import co.coffeery.app.ui.components.AppTextField
import co.coffeery.app.ui.components.AppText
import co.coffeery.app.ui.components.CoffeeCard
import co.coffeery.app.ui.components.CoffeeDialog
import co.coffeery.app.ui.components.Glyph
import co.coffeery.app.ui.components.LineIcon
import co.coffeery.app.ui.components.PrimaryButton
import co.coffeery.app.ui.components.ScreenHeader
import co.coffeery.app.ui.components.SecondaryButton
import co.coffeery.app.ui.components.SegmentedControl
import co.coffeery.app.ui.theme.CoffeeShapes
import co.coffeery.app.ui.theme.CoffeeTheme
import co.coffeery.app.ui.theme.coffeeBackground
import co.coffeery.app.ui.theme.paletteColors
import co.coffeery.app.util.CloudBackupManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    val colors = CoffeeTheme.colors
    var showImportDialog by remember { mutableStateOf(false) }
    val cloud = remember { CloudBackupManager(ctx) }
    var cloudSignedIn by remember { mutableStateOf(cloud.isSignedIn()) }
    val cloudEmail = remember(cloudSignedIn) { cloud.getAccountEmail() ?: "" }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!cloud.isPlayServicesAvailable()) {
            android.widget.Toast.makeText(ctx, "Google Play Services not available", android.widget.Toast.LENGTH_LONG).show()
        } else if (!cloud.isSignedIn()) {
            val result = cloud.silentSignIn()
            if (result.isSuccess) cloudSignedIn = true
        }
    }

    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        cloud.handleSignInResult(result.data) { success, msg ->
            cloudSignedIn = success
            if (success) {
                scope.launch {
                    val json = vm.getExportJson()
                    cloud.backupToDrive(ctx as android.app.Activity, json)
                }
            }
        }
    }

    val recoverLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        cloud.clearRecoverableIntent()
        android.widget.Toast.makeText(ctx, "Permission granted — please retry backup", android.widget.Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ScreenHeader(title = stringResource(R.string.settings_title))

        SettingsSection(R.string.settings_appearance) {
            AppText(stringResource(R.string.settings_theme), style = CoffeeTheme.type.body, color = colors.textPrimary)
            Spacer(Modifier.height(8.dp))
            SegmentedControl(
                options = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK),
                selected = state.themeMode,
                label = { stringResource(it.labelRes) },
                onSelect = { vm.setThemeMode(it) },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(12.dp))
            AppText(stringResource(R.string.settings_palette), style = CoffeeTheme.type.body, color = colors.textPrimary)
            Spacer(Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 4.dp),
            ) {
                items(Palette.entries) { palette ->
                    val isSelected = palette == state.palette
                    val swatchColors = remember(palette) { paletteColors(palette, false) }
                    val borderWidth by animateDpAsState(
                        targetValue = if (isSelected) 2.dp else 0.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                        label = "borderWidth",
                    )
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) swatchColors.accent else androidx.compose.ui.graphics.Color.Transparent,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                        label = "borderColor",
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 0.97f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow),
                        label = "scale",
                    )
                    Box(
                        modifier = Modifier
                            .width(148.dp)
                            .height(108.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .clip(CoffeeShapes.small)
                            .border(borderWidth, borderColor, CoffeeShapes.small)
                            .coffeeBackground(swatchColors)
                            .clickable { vm.setPalette(palette) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(92.dp)
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(swatchColors.surfaceElevated)
                                    .border(1.dp, swatchColors.outline, RoundedCornerShape(10.dp))
                                    .padding(7.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.align(Alignment.Center),
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(36.dp)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(swatchColors.accent),
                                    )
                                    Box(
                                        modifier = Modifier
                                            .width(52.dp)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(swatchColors.accentSoft),
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(24.dp)
                                                .height(2.dp)
                                                .clip(RoundedCornerShape(1.dp))
                                                .background(swatchColors.outline),
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clip(CircleShape)
                                                .background(swatchColors.accent),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(swatchColors.onAccent),
                                            )
                                        }
                                    }
                                    Box(
                                        modifier = Modifier
                                            .width(44.dp)
                                            .height(2.dp)
                                            .clip(RoundedCornerShape(1.dp))
                                            .background(swatchColors.textPrimary.copy(alpha = 0.14f)),
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Crossfade(targetState = isSelected, label = "paletteLabel") { selected ->
                                AppText(
                                    stringResource(palette.labelRes),
                                    style = CoffeeTheme.type.caption,
                                    color = if (selected) swatchColors.accent else swatchColors.textPrimary,
                                )
                            }
                        }
                        if (isSelected) {
                            Crossfade(targetState = isSelected, label = "check") { _ ->
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(swatchColors.accent),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(swatchColors.onAccent),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            AppText(stringResource(R.string.settings_temperature), style = CoffeeTheme.type.body, color = colors.textPrimary)
            Spacer(Modifier.height(8.dp))
            SegmentedControl(
                options = listOf("C" to "°C", "F" to "°F"),
                selected = if (state.settings.temperatureUnit == "F") "F" to "°F" else "C" to "°C",
                label = { it.second },
                onSelect = { vm.setTemperatureUnit(it.first) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        SettingsSection(R.string.settings_language) {
            AppText(stringResource(R.string.settings_language_desc), style = CoffeeTheme.type.caption, color = colors.textSecondary)
            Spacer(Modifier.height(8.dp))
            val langOpts = listOf("en" to "English", "tr" to "Türkçe")
            SegmentedControl(
                options = langOpts,
                selected = langOpts.firstOrNull { it.first == state.settings.language } ?: langOpts[0],
                label = { it.second },
                onSelect = { vm.setLanguage(it.first) },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        SettingsSection(R.string.settings_timer) {
            ToggleRow(R.string.settings_timer_pip, state.settings.timerPip) {
                vm.setTimerSetting { it.copy(timerPip = !it.timerPip) }
            }
            ToggleRow(R.string.settings_timer_background, state.settings.timerBackground) {
                vm.setTimerSetting { it.copy(timerBackground = !it.timerBackground) }
            }
            ToggleRow(R.string.settings_timer_sound, state.settings.timerSound) {
                vm.setTimerSetting { it.copy(timerSound = !it.timerSound) }
            }
            ToggleRow(R.string.settings_timer_vibrate, state.settings.timerVibrate) {
                vm.setTimerSetting { it.copy(timerVibrate = !it.timerVibrate) }
            }
            ToggleRow(R.string.settings_timer_show_next, state.settings.timerShowNext) {
                vm.setTimerSetting { it.copy(timerShowNext = !it.timerShowNext) }
            }
            ToggleRow(R.string.settings_timer_merge_weight, state.settings.timerMergeWeight) {
                vm.setTimerSetting { it.copy(timerMergeWeight = !it.timerMergeWeight) }
            }
        }

        SettingsSection(R.string.settings_brew_custom) {
            AppText(stringResource(R.string.settings_step_overrides), style = CoffeeTheme.type.body)
            Spacer(Modifier.height(8.dp))
            DurationRow("Bloom", state.settings.bloomDurationSec) { newVal ->
                vm.setTimerSetting { it.copy(bloomDurationSec = newVal) }
            }
            DurationRow("Pour", state.settings.pourDurationSec) { newVal ->
                vm.setTimerSetting { it.copy(pourDurationSec = newVal) }
            }
            DurationRow("Steep", state.settings.steepDurationSec) { newVal ->
                vm.setTimerSetting { it.copy(steepDurationSec = newVal) }
            }
            DurationRow("Drawdown", state.settings.drawdownDurationSec) { newVal ->
                vm.setTimerSetting { it.copy(drawdownDurationSec = newVal) }
            }
            Spacer(Modifier.height(12.dp))
            ToggleRow(R.string.settings_auto_advance, state.settings.timerAutoAdvance) {
                vm.setTimerSetting { it.copy(timerAutoAdvance = !it.timerAutoAdvance) }
            }
        }

        SettingsSection(R.string.settings_notifications) {
            ToggleRow(R.string.settings_notify_brew_complete, state.settings.notificationsBrewComplete) {
                vm.setTimerSetting { it.copy(notificationsBrewComplete = !it.notificationsBrewComplete) }
            }
            ToggleRow(R.string.settings_notify_step_change, state.settings.notificationsStepChange) {
                vm.setTimerSetting { it.copy(notificationsStepChange = !it.notificationsStepChange) }
            }
        }

        SettingsSection(R.string.settings_my_data) {
            ActionRow(stringResource(R.string.settings_export_data)) {
                vm.exportData(ctx)
            }
            ActionRow(stringResource(R.string.settings_export_csv)) {
                vm.exportCsv(ctx)
            }
            ActionRow(stringResource(R.string.settings_import_paste)) {
                vm.importData(ctx)
            }
            ActionRow(stringResource(R.string.settings_import_manual)) {
                showImportDialog = true
            }
        }

        if (showImportDialog) {
            var importText by remember { mutableStateOf("") }
            CoffeeDialog(onDismiss = { showImportDialog = false }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AppText(stringResource(R.string.settings_import_dialog_title), style = CoffeeTheme.type.title)
                    Spacer(Modifier.height(12.dp))
                    AppTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        hint = "{ ... }",
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        SecondaryButton(stringResource(R.string.action_cancel), Modifier.weight(1f)) { showImportDialog = false }
                        PrimaryButton(stringResource(R.string.action_import), Modifier.weight(1f), enabled = importText.isNotBlank()) {
                            vm.importFromJsonString(ctx, importText)
                            showImportDialog = false
                        }
                    }
                }
            }
        }

        SettingsSection(R.string.settings_backup) {
            var showConfirm by remember { mutableStateOf(false) }
            ActionRow(stringResource(R.string.settings_restore_defaults)) {
                showConfirm = true
            }
            if (showConfirm) {
                CoffeeDialog(onDismiss = { showConfirm = false }) {
                    AppText(stringResource(R.string.settings_restore_confirm), style = CoffeeTheme.type.title)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        SecondaryButton(stringResource(R.string.action_cancel), Modifier.weight(1f)) { showConfirm = false }
                        PrimaryButton(stringResource(R.string.action_reset), Modifier.weight(1f)) {
                            vm.restoreDefaults(ctx)
                            showConfirm = false
                        }
                    }
                }
            }
        }

        SettingsSection(R.string.settings_cloud_title) {
            if (cloudSignedIn) {
                AppText(
                    stringResource(R.string.settings_cloud_signed_as, cloudEmail),
                    style = CoffeeTheme.type.caption,
                    color = colors.textSecondary,
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SecondaryButton(stringResource(R.string.settings_cloud_backup), Modifier.weight(1f)) {
                        scope.launch {
                            val json = vm.getExportJson()
                            val result = cloud.backupToDrive(ctx as android.app.Activity, json)
                            if (result.isSuccess) android.widget.Toast.makeText(ctx, R.string.settings_cloud_backup_done, android.widget.Toast.LENGTH_SHORT).show()
                            else {
                                val ex = result.exceptionOrNull()
                                val recoverIntent = cloud.consumeRecoverableIntent() ?: (ex as? CloudBackupManager.RecoverableAuthException)?.intent
                                if (recoverIntent != null) {
                                    try { recoverLauncher.launch(recoverIntent) } catch (e: Exception) { android.widget.Toast.makeText(ctx, "Authorization required: ${e.message}", android.widget.Toast.LENGTH_LONG).show() }
                                } else {
                                    val msg = ex?.message ?: ctx.getString(R.string.settings_cloud_error)
                                    android.util.Log.e("Coffeery", "Backup failed: $msg", ex)
                                    android.widget.Toast.makeText(ctx, msg, android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SecondaryButton(stringResource(R.string.settings_cloud_restore), Modifier.weight(1f)) {
                        scope.launch {
                            val result = cloud.restoreFromDrive(ctx)
                            if (result.isSuccess) {
                                vm.importFromJsonString(ctx, result.getOrDefault(""))
                                android.widget.Toast.makeText(ctx, R.string.settings_cloud_restore_done, android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                val ex = result.exceptionOrNull()
                                val recoverIntent = cloud.consumeRecoverableIntent() ?: (ex as? CloudBackupManager.RecoverableAuthException)?.intent
                                if (recoverIntent != null) {
                                    try { recoverLauncher.launch(recoverIntent) } catch (e: Exception) { android.widget.Toast.makeText(ctx, "Authorization required: ${e.message}", android.widget.Toast.LENGTH_LONG).show() }
                                } else {
                                    val msg = ex?.message ?: ctx.getString(R.string.settings_cloud_error)
                                    android.util.Log.e("Coffeery", "Restore failed: $msg", ex)
                                    android.widget.Toast.makeText(ctx, msg, android.widget.Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SecondaryButton(stringResource(R.string.settings_cloud_signout), Modifier.weight(1f)) {
                        cloud.signOut(cloud.getSignInClient())
                        cloudSignedIn = false
                    }
                }
            } else {
                PrimaryButton(
                    stringResource(R.string.settings_cloud_signin),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val client = cloud.getSignInClient()
                    signInLauncher.launch(cloud.getSignInIntent(client))
                }
            }
        }

        SettingsSection(R.string.settings_about) {
            AboutRow(R.string.settings_version, BuildConfig.VERSION_NAME)
            AppText(
                text = stringResource(R.string.settings_about_footer),
                style = CoffeeTheme.type.caption,
                color = CoffeeTheme.colors.textSecondary,
            )
            Spacer(Modifier.height(8.dp))
            ActionRow(stringResource(R.string.settings_github)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/omersusin/Coffeery"))
                ctx.startActivity(intent)
            }
            ActionRow(stringResource(R.string.settings_whats_new)) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/omersusin/Coffeery/releases"))
                ctx.startActivity(intent)
            }
        }
    }
}

@Composable
private fun SettingsSection(labelRes: Int, content: @Composable () -> Unit) {
    val colors = CoffeeTheme.colors
    Column {
        AppText(stringResource(labelRes), style = CoffeeTheme.type.label, color = colors.textSecondary)
        Spacer(Modifier.height(8.dp))
        CoffeeCard(modifier = Modifier.fillMaxWidth(), contentPadding = 12) {
            content()
        }
    }
}

@Composable
private fun ToggleRow(labelRes: Int, checked: Boolean, onToggle: () -> Unit) {
    val colors = CoffeeTheme.colors
    val trackColor = if (checked) colors.accent else colors.outline

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(stringResource(labelRes), style = CoffeeTheme.type.body, modifier = Modifier.weight(1f), color = colors.textPrimary)
        Spacer(Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(width = 44.dp, height = 26.dp)
                .clip(CoffeeShapes.pill)
                .background(trackColor)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) { onToggle() },
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(20.dp)
                    .clip(CoffeeShapes.pill)
                    .background(colors.surfaceElevated),
            )
        }
    }
}

@Composable
private fun ActionRow(text: String, onClick: () -> Unit) {
    val colors = CoffeeTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(text, style = CoffeeTheme.type.body, modifier = Modifier.weight(1f), color = colors.textPrimary)
        LineIcon(Glyph.BEAN, colors.textSecondary, Modifier.size(16.dp))
    }
}

@Composable
private fun AboutRow(labelRes: Int, value: String) {
    val colors = CoffeeTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(stringResource(labelRes), style = CoffeeTheme.type.body, modifier = Modifier.weight(1f), color = colors.textPrimary)
        AppText(value, style = CoffeeTheme.type.caption, color = colors.textSecondary)
    }
}

@Composable
private fun DurationRow(label: String, value: Int, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        AppText(label, style = CoffeeTheme.type.body, modifier = Modifier.weight(1f))
        AppText("-", modifier = Modifier.clickable { onChange((value - 5).coerceAtLeast(0)) }.padding(8.dp))
        AppText("${value}s", style = CoffeeTheme.type.title, modifier = Modifier.padding(horizontal = 8.dp))
        AppText("+", modifier = Modifier.clickable { onChange((value + 5).coerceAtMost(600)) }.padding(8.dp))
    }
}
