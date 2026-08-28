package co.coffeery.app.ui.screens.root

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.coffeery.app.data.local.AppDatabase
import co.coffeery.app.R
import co.coffeery.app.data.local.BeanEntity
import co.coffeery.app.data.local.BrewLogEntity
import co.coffeery.app.data.local.CustomEquipmentEntity
import co.coffeery.app.data.local.RecipeEntity
import co.coffeery.app.data.local.SettingsEntity
import co.coffeery.app.data.model.BrewCategory
import co.coffeery.app.data.model.Equipment
import co.coffeery.app.data.model.Palette
import co.coffeery.app.data.model.RoastLevel
import co.coffeery.app.data.model.ThemeMode
import co.coffeery.app.data.repo.CoffeeRepository
import co.coffeery.app.ui.screens.log.Achievement
import co.coffeery.app.ui.screens.log.checkAchievements
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.util.UUID
import androidx.lifecycle.SavedStateHandle

/** Immutable UI state for the whole app (unidirectional data flow). */
data class AppUiState(
    val tab: NavTab = NavTab.BREW,
    val route: Route = Route.Tabs,
    val backStack: List<Route> = emptyList(),
    val equipment: List<Equipment> = emptyList(),
    val recipes: List<RecipeEntity> = emptyList(),
    val selectedEquipmentId: String? = null,
    val strength: Float = 0.5f,
    val roast: RoastLevel = RoastLevel.MEDIUM,
    val byCups: Boolean = true,
    val cups: Int = 2,
    val waterMl: Int = 500,
    val ratioMode: Boolean = false,
    val manualRatio: Double = 16.0,
    val coffeeGrams: Double = 0.0,
    val manualWaterMl: Int = 0,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val palette: Palette = Palette.TERRACOTTA,
    val hasCompletedOnboarding: Boolean = false,
    val settings: SettingsEntity = SettingsEntity(),
    val brewLogs: List<BrewLogEntity> = emptyList(),
    val beans: List<BeanEntity> = emptyList(),
    val completedChapters: Set<Int> = emptySet(),
    val stepWaterOverrides: Map<Int, Float> = emptyMap(),
    val learnScrollOffset: Int = 0,
    val achievements: List<Achievement> = emptyList(),
) {
    val selectedEquipment: Equipment?
        get() = equipment.firstOrNull { it.id == selectedEquipmentId }
            ?: equipment.firstOrNull()
}

class AppViewModel(app: Application, private val savedStateHandle: SavedStateHandle = SavedStateHandle()) : AndroidViewModel(app) {

    private val repo = CoffeeRepository(app, AppDatabase.get(app))

    private val _state = MutableStateFlow(
        run {
            val restored = savedStateHandle.get<String>("nav_route")?.let { parseRoute(it) }
            val stack = (savedStateHandle.get<ArrayList<String>>("nav_backstack") ?: savedStateHandle.get<List<String>>("nav_backstack"))?.mapNotNull { parseRoute(it) } ?: emptyList()
            val tabKey = savedStateHandle.get<String>("nav_tab")
            val tab = tabKey?.let { runCatching { NavTab.valueOf(it) }.getOrNull() }
            var s = AppUiState()
            if (tab != null) s = s.copy(tab = tab)
            if (restored != null) s = s.copy(route = restored, backStack = stack)
            else if (stack.isNotEmpty()) s = s.copy(backStack = stack)
            s
        }
    )
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<Route>(extraBufferCapacity = 1)
    val navEvents: SharedFlow<Route> = _navEvents.asSharedFlow()

    val tabFlow: Flow<NavTab> = state.map { it.tab }.distinctUntilChanged()
    val routeFlow: Flow<Route> = state.map { it.route }.distinctUntilChanged()
    val equipmentFlow: Flow<List<Equipment>> = state.map { it.equipment }.distinctUntilChanged()
    val brewLogsFlow: Flow<List<BrewLogEntity>> = state.map { it.brewLogs }.distinctUntilChanged()
    val beansFlow: Flow<List<BeanEntity>> = state.map { it.beans }.distinctUntilChanged()
    val settingsFlow: Flow<SettingsEntity> = state.map { it.settings }.distinctUntilChanged()
    val achievementsFlow: Flow<List<Achievement>> = state.map { it.achievements }.distinctUntilChanged()

    private fun persistNav(s: AppUiState) {
        savedStateHandle["nav_route"] = routeToKey(s.route)
        savedStateHandle["nav_backstack"] = ArrayList(s.backStack.map { routeToKey(it) })
        savedStateHandle["nav_tab"] = s.tab.name
    }

    init {
        viewModelScope.launch {
            repo.allEquipment.collectSafely { list ->
                _state.update { s ->
                    val selected = s.selectedEquipmentId
                        ?: list.firstOrNull()?.id
                    val strength = if (s.selectedEquipmentId == null) {
                        list.firstOrNull()?.defaultStrength ?: 0.5f
                    } else s.strength
                    s.copy(equipment = list, selectedEquipmentId = selected, strength = strength)
                }
            }
        }
        viewModelScope.launch {
            repo.recipes.collectSafely { list -> _state.update { it.copy(recipes = list) } }
        }
        viewModelScope.launch {
            repo.brewLogs.collectSafely { list -> _state.update { it.copy(brewLogs = list, achievements = checkAchievements(list, it.beans, it.completedChapters)) } }
        }
        viewModelScope.launch {
            repo.beans.collectSafely { list -> _state.update { it.copy(beans = list, achievements = checkAchievements(it.brewLogs, list, it.completedChapters)) } }
        }
        viewModelScope.launch {
            repo.settings.collectSafely { entity ->
                val s = entity ?: SettingsEntity()
                if (entity != null) {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(s.language))
                }
                _state.update {
                    val chapters = s.completedChapters.split(",").filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() }.toSet()
                    it.copy(
                        themeMode = ThemeMode.fromKey(s.themeMode),
                        palette = Palette.fromKey(s.paletteKey),
                        hasCompletedOnboarding = s.hasCompletedOnboarding,
                        settings = s,
                        completedChapters = chapters,
                        ratioMode = s.ratioMode,
                        manualRatio = s.manualRatio.coerceAtLeast(1.0),
                    )
                }
            }
        }
    }

    // --- Settings ---
    fun setThemeMode(mode: ThemeMode) {
        _state.update { it.copy(themeMode = mode) }
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity())
                .copy(themeMode = mode.name)
            repo.upsertSettings(cur)
        }
    }

    fun setPalette(palette: Palette) {
        _state.update { it.copy(palette = palette) }
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity())
                .copy(paletteKey = palette.name)
            repo.upsertSettings(cur)
        }
    }

    fun setLanguage(lang: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity())
                .copy(language = lang)
            repo.upsertSettings(cur)
        }
    }

    fun setTemperatureUnit(unit: String) {
        viewModelScope.launch {
            val cur = repo.settings.first() ?: SettingsEntity()
            repo.upsertSettings(cur.copy(temperatureUnit = unit))
        }
    }

    fun setTimerSetting(update: (SettingsEntity) -> SettingsEntity) {
        viewModelScope.launch {
            val cur = repo.settings.first() ?: SettingsEntity()
            repo.upsertSettings(update(cur))
        }
    }

    // --- Navigation ---
    fun selectTab(tab: NavTab) {
        _state.update { s -> s.copy(tab = tab, route = Route.Tabs, backStack = emptyList()).also { persistNav(it) } }
    }
    fun openRoute(route: Route) {
        _state.update { s ->
            if (s.route == route) s else s.copy(backStack = s.backStack + s.route, route = route).also { persistNav(it) }
        }
        _navEvents.tryEmit(route)
    }
    fun back() {
        _state.update { s ->
            if (s.backStack.isNotEmpty()) s.copy(route = s.backStack.last(), backStack = s.backStack.dropLast(1)).also { persistNav(it) }
            else s.copy(route = Route.Tabs, backStack = emptyList()).also { persistNav(it) }
        }
    }
    fun canGoBack(): Boolean = _state.value.backStack.isNotEmpty() || _state.value.route !is Route.Tabs
    fun handleDeepLink(routeKey: String?) {
        val r = routeKey?.let { parseRoute(it) } ?: return
        openRoute(r)
    }
    fun handleIntent(intent: Intent?) {
        val key = intent?.getStringExtra("route") ?: intent?.getStringExtra("deeplink_route") ?: return
        handleDeepLink(key)
    }

    // --- Brew parameters (explicit navigation) ---
    fun selectEquipmentOnly(id: String) = _state.update { s ->
        val eq = s.equipment.firstOrNull { it.id == id }
        s.copy(selectedEquipmentId = id, strength = eq?.defaultStrength ?: s.strength)
    }
    fun navigateToBrew() = _state.update { s -> s.copy(tab = NavTab.BREW, route = Route.Tabs, backStack = emptyList()).also { persistNav(it) } }
    fun selectEquipment(id: String) {
        selectEquipmentOnly(id)
        navigateToBrew()
    }
    fun selectEquipmentAndNavigateToBrew(id: String) = selectEquipment(id)
    fun selectCategoryEquipmentOnly(category: BrewCategory) = _state.update { s ->
        val eq = s.equipment.firstOrNull { it.category == category }
        if (eq != null) s.copy(selectedEquipmentId = eq.id, strength = eq.defaultStrength) else s
    }
    fun selectCategoryEquipment(category: BrewCategory) {
        selectCategoryEquipmentOnly(category)
        navigateToBrew()
    }

    fun setStrength(v: Float) = _state.update { it.copy(strength = v) }
    fun setRoast(r: RoastLevel) = _state.update { it.copy(roast = r) }
    fun setByCups(byCups: Boolean) = _state.update { it.copy(byCups = byCups) }
    fun setCups(c: Int) = _state.update { it.copy(cups = c.coerceIn(1, 12)) }
    fun setWater(ml: Int) = _state.update { it.copy(waterMl = ml.coerceIn(0, 4000)) }

    fun toggleRatioMode(mode: Boolean) {
        _state.update { it.copy(ratioMode = mode) }
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity())
                .copy(ratioMode = mode)
            repo.upsertSettings(cur)
        }
    }

    fun setManualRatio(ratio: Double) = _state.update {
        val updated = it.copy(manualRatio = ratio, manualWaterMl = (it.coffeeGrams * ratio).roundToInt())
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity())
                .copy(manualRatio = ratio)
            repo.upsertSettings(cur)
        }
        updated
    }

    fun setCoffeeGrams(grams: Double) = _state.update {
        it.copy(coffeeGrams = grams, manualWaterMl = (grams * it.manualRatio).roundToInt())
    }

    fun setManualWaterMl(ml: Int) = _state.update {
        it.copy(manualWaterMl = ml, coffeeGrams = if (it.manualRatio > 0) ml / it.manualRatio else 0.0)
    }

    // --- Recipes ---
    fun saveRecipe(name: String) {
        val s = _state.value
        val eq = s.selectedEquipment ?: return
        viewModelScope.launch {
            repo.saveRecipe(
                RecipeEntity(
                    name = name.trim().ifBlank { "—" },
                    equipmentId = eq.id,
                    strength = s.strength,
                    roast = s.roast.name,
                    inputByCups = s.byCups,
                    cups = s.cups,
                    waterMl = s.waterMl,
                )
            )
        }
    }

    fun deleteRecipe(id: Long) = viewModelScope.launch { repo.deleteRecipe(id) }

    fun loadRecipe(recipe: RecipeEntity) = _state.update {
        it.copy(
            selectedEquipmentId = recipe.equipmentId,
            strength = recipe.strength,
            roast = RoastLevel.entries.find { r -> r.name == recipe.roast } ?: RoastLevel.MEDIUM,
            byCups = recipe.inputByCups,
            cups = recipe.cups,
            waterMl = recipe.waterMl,
        )
    }

    fun applyRecipe(r: RecipeEntity) = _state.update {
        it.copy(
            selectedEquipmentId = r.equipmentId,
            strength = r.strength,
            roast = runCatching { RoastLevel.valueOf(r.roast) }.getOrDefault(RoastLevel.MEDIUM),
            byCups = r.inputByCups,
            cups = r.cups,
            waterMl = r.waterMl,
            tab = NavTab.BREW,
            route = Route.Tabs,
            backStack = emptyList(),
        ).also { persistNav(it) }
    }

    // --- Custom equipment ---
    fun addCustomEquipment(name: String, category: BrewCategory, iconKey: String = "icon_mug") {
        val defaults = CoffeeRepository.defaultsFor(category)
        val id = "custom_${iconKey}_" + UUID.randomUUID().toString().take(8)
        viewModelScope.launch {
            repo.addCustomEquipment(
                CustomEquipmentEntity(
                    id = id,
                    name = name.trim(),
                    category = category.name,
                    ratioMin = defaults.ratioMin,
                    ratioMax = defaults.ratioMax,
                    ratioDefault = defaults.ratioDefault,
                    tempMode = defaults.tempMode.name,
                    tempMin = defaults.tempMin,
                    tempMax = defaults.tempMax,
                    grind = defaults.grind.name,
                    cupMl = defaults.cupMl,
                    hasBloom = defaults.hasBloom,
                )
            )
            _state.update { it.copy(route = Route.Tabs, tab = NavTab.GEAR, backStack = emptyList()).also { persistNav(it) } }
        }
    }

    fun deleteCustomEquipment(id: String) = viewModelScope.launch { repo.deleteCustomEquipment(id) }

    // --- Brew logs ---
    fun saveBrewLog(log: BrewLogEntity) = viewModelScope.launch { repo.saveBrewLog(log) }

    fun deleteBrewLog(id: Long) = viewModelScope.launch { repo.deleteBrewLog(id) }

    fun addBean(
        name: String, origin: String, roaster: String, roastDate: Long?, notes: String,
        processMethod: String = "", varietal: String = "", altitude: String = "",
        scaScore: Float? = null, purchaseDate: Long? = null,
    ) =
        viewModelScope.launch { repo.addBean(BeanEntity(
            name = name, origin = origin, roaster = roaster,
            roastDate = roastDate, notes = notes,
            processMethod = processMethod, varietal = varietal,
            altitude = altitude, scaScore = scaScore, purchaseDate = purchaseDate,
        )) }

    fun archiveBean(id: Long) = viewModelScope.launch { repo.archiveBean(id) }

    fun getBean(beanId: Long): BeanEntity? = _state.value.beans.firstOrNull { it.id == beanId }

    fun setLearnScrollOffset(offset: Int) {
        _state.update { it.copy(learnScrollOffset = offset) }
    }

    fun markLearnCardRead(chapterRes: Int) {
        val newSet = _state.value.completedChapters + chapterRes
        _state.update { it.copy(completedChapters = newSet) }
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity())
                .copy(completedChapters = newSet.joinToString(","))
            repo.upsertSettings(cur)
        }
    }

    fun setStepWaterOverride(stepIndex: Int, pct: Float) = _state.update {
        it.copy(stepWaterOverrides = it.stepWaterOverrides + (stepIndex to pct.coerceIn(0f, 1f)))
    }
    fun clearStepWaterOverrides() = _state.update { it.copy(stepWaterOverrides = emptyMap()) }

    fun completeOnboarding() {
        _state.update { it.copy(hasCompletedOnboarding = true) }
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity())
                .copy(hasCompletedOnboarding = true)
            repo.upsertSettings(cur)
        }
    }

    fun restoreDefaults(ctx: Context) {
        viewModelScope.launch {
            repo.clearAll()
            Toast.makeText(ctx, ctx.getString(R.string.settings_restored), Toast.LENGTH_SHORT).show()
        }
    }

    // --- Export / Import ---
    suspend fun getExportJson(): String = repo.exportAllToJson()

    fun exportData(ctx: Context) {
        viewModelScope.launch {
            try {
                val json = repo.exportAllToJson()
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_TEXT, json)
                    putExtra(Intent.EXTRA_SUBJECT, "Coffeery backup")
                }
                ctx.startActivity(Intent.createChooser(sendIntent, "Export Coffeery data"))
            } catch (e: Exception) {
                Toast.makeText(ctx, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun importData(ctx: Context) {
        viewModelScope.launch {
            try {
                val clipboard = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val text = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                if (text != null) {
                    val trimmed = text.trimStart()
                    try {
                        if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                            repo.importFromJson(text)
                        } else if (trimmed.startsWith("Date,") || trimmed.contains("Equipment") && trimmed.contains(",")) {
                            repo.importLogsFromCsv(text)
                        } else {
                            Toast.makeText(ctx, ctx.getString(co.coffeery.app.R.string.settings_import_invalid), Toast.LENGTH_SHORT).show()
                            return@launch
                        }
                        Toast.makeText(ctx, "Data imported. Restart the app.", Toast.LENGTH_SHORT).show()
                    } catch (e: org.json.JSONException) {
                        Toast.makeText(ctx, ctx.getString(co.coffeery.app.R.string.settings_import_invalid), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(ctx, "Copy JSON backup to clipboard first, then tap Import.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(ctx, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun importFromJsonString(ctx: Context, jsonString: String) {
        viewModelScope.launch {
            try {
                val trimmed = jsonString.trimStart()
                if (trimmed.startsWith("{") || trimmed.startsWith("[")) repo.importFromJson(jsonString)
                else if (trimmed.startsWith("Date,") || trimmed.contains("Equipment") && trimmed.contains(",")) repo.importLogsFromCsv(jsonString)
                else repo.importFromJson(jsonString)
                Toast.makeText(ctx, "Data imported. Restart the app.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(ctx, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun exportCsv(ctx: Context) {
        viewModelScope.launch {
            try {
                val csv = repo.exportLogsAsCsv()
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_TEXT, csv)
                    putExtra(Intent.EXTRA_SUBJECT, "Coffeery brew logs")
                }
                ctx.startActivity(Intent.createChooser(sendIntent, "Export brew logs"))
            } catch (e: Exception) {
                Toast.makeText(ctx, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun applyBrewLog(log: BrewLogEntity) = _state.update {
        it.copy(
            selectedEquipmentId = log.equipmentId,
            strength = log.strength,
            roast = runCatching { RoastLevel.valueOf(log.roast) }.getOrDefault(RoastLevel.MEDIUM),
            byCups = false,
            waterMl = log.waterMl,
            tab = NavTab.BREW,
            route = Route.Tabs,
            backStack = emptyList(),
        ).also { persistNav(it) }
    }

    fun applyBestRecipe(best: co.coffeery.app.ui.screens.log.BestRecipeSuggestion) {
        val eq = _state.value.equipment.firstOrNull { it.id == best.equipmentId }
            ?: _state.value.equipment.firstOrNull { it.id.equals(best.equipmentName, ignoreCase = true) }
            ?: _state.value.equipment.firstOrNull { it.customName.equals(best.equipmentName, ignoreCase = true) }
            ?: return
        _state.update { s ->
            s.copy(
                selectedEquipmentId = eq.id,
                manualRatio = best.ratioDenominator.coerceIn(1.0, 30.0),
                ratioMode = true,
                tab = NavTab.BREW,
                route = Route.Tabs,
                backStack = emptyList(),
            ).also { persistNav(it) }
        }
        viewModelScope.launch {
            val cur = (repo.settings.first() ?: SettingsEntity()).copy(ratioMode = true, manualRatio = best.ratioDenominator.coerceIn(1.0, 30.0))
            repo.upsertSettings(cur)
        }
    }

    companion object {
        fun factory(app: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
                    AppViewModel(app, SavedStateHandle()) as T
            }
    }
}

private fun routeToKey(route: Route): String = when (route) {
    is Route.Tabs -> "Tabs"
    is Route.AddEquipment -> "AddEquipment"
    is Route.Timer -> "Timer"
    is Route.Settings -> "Settings"
    is Route.LearnDetail -> "LearnDetail/${route.cardIndex}"
    is Route.DrinkDetail -> "DrinkDetail/${route.index}"
    is Route.BeanDetail -> "BeanDetail/${route.beanId}"
}

private fun parseRoute(key: String): Route? = when {
    key == "Tabs" -> Route.Tabs
    key == "AddEquipment" -> Route.AddEquipment
    key == "Timer" -> Route.Timer
    key == "Settings" -> Route.Settings
    key.startsWith("LearnDetail/") -> key.substringAfter("/").toIntOrNull()?.let { Route.LearnDetail(it) }
    key.startsWith("DrinkDetail/") -> key.substringAfter("/").toIntOrNull()?.let { Route.DrinkDetail(it) }
    key.startsWith("BeanDetail/") -> key.substringAfter("/").toLongOrNull()?.let { Route.BeanDetail(it) }
    else -> null
}

private suspend fun <T> Flow<T>.collectSafely(action: suspend (T) -> Unit) {
    try {
        collect { action(it) }
    } catch (e: Exception) {
        android.util.Log.e("Coffeery", "Flow collection failed", e)
    }
}
