package co.coffeery.app.data.repo

import android.content.Context
import co.coffeery.app.R
import co.coffeery.app.data.local.AppDatabase
import co.coffeery.app.data.local.BeanEntity
import co.coffeery.app.data.local.BrewLogEntity
import co.coffeery.app.data.local.CustomEquipmentEntity
import co.coffeery.app.data.local.PresetLoader
import co.coffeery.app.data.local.RecipeEntity
import co.coffeery.app.data.local.SettingsEntity
import co.coffeery.app.data.model.BrewCategory
import co.coffeery.app.data.model.BrewStepDef
import co.coffeery.app.data.model.Equipment
import co.coffeery.app.data.model.Grind
import co.coffeery.app.data.model.StepKind
import co.coffeery.app.data.model.TempMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

/** Suggested tuning defaults for a category when creating custom gear. */
data class CategoryDefaults(
    val ratioMin: Double,
    val ratioMax: Double,
    val ratioDefault: Double,
    val tempMode: TempMode,
    val tempMin: Int,
    val tempMax: Int,
    val grind: Grind,
    val cupMl: Int,
    val hasBloom: Boolean,
)

class CoffeeRepository(context: Context, private val db: AppDatabase) {

    private val builtIns: List<Equipment> = PresetLoader.loadBuiltIns(context)

    val recipes: Flow<List<RecipeEntity>> = db.recipeDao().observeAll()

    private val customEquipment: Flow<List<Equipment>> =
        db.customEquipmentDao().observeAll().map { list -> list.map { it.toEquipment() } }

    /** Built-ins first, then user gear. */
    val allEquipment: Flow<List<Equipment>> =
        customEquipment.map { custom -> builtIns + custom }

    fun builtInEquipment(): List<Equipment> = builtIns

    suspend fun saveRecipe(recipe: RecipeEntity) = db.recipeDao().insert(recipe)

    suspend fun deleteRecipe(id: Long) = db.recipeDao().deleteById(id)

    suspend fun addCustomEquipment(entity: CustomEquipmentEntity) =
        db.customEquipmentDao().insert(entity)

    suspend fun deleteCustomEquipment(id: String) =
        db.customEquipmentDao().deleteById(id)

    val settings: Flow<SettingsEntity?> = db.settingsDao().observe()

    val brewLogs: Flow<List<BrewLogEntity>> = db.brewLogDao().observeAll()

    val beans: Flow<List<BeanEntity>> = db.beanDao().observeAll()

    suspend fun upsertSettings(settings: SettingsEntity) = db.settingsDao().upsert(settings)

    suspend fun saveBrewLog(log: BrewLogEntity) = db.brewLogDao().insert(log)

    suspend fun deleteBrewLog(id: Long) = db.brewLogDao().deleteById(id)

    suspend fun addBean(bean: BeanEntity) = db.beanDao().insert(bean)

    suspend fun archiveBean(id: Long) = db.beanDao().archiveById(id)

    // --- Export / Import ---
    suspend fun exportAllToJson(): String {
        val recipes = db.recipeDao().observeAll().first()
        val custom = db.customEquipmentDao().observeAll().first()
        val logs = db.brewLogDao().observeAll().first()
        val settings = db.settingsDao().observe().first()
        val json = JSONObject()
        json.put("version", 2)
        val recipeArr = JSONArray()
        for (r in recipes) {
            val o = JSONObject()
            o.put("name", r.name); o.put("equipmentId", r.equipmentId)
            o.put("strength", r.strength.toDouble()); o.put("roast", r.roast)
            o.put("inputByCups", r.inputByCups); o.put("cups", r.cups)
            o.put("waterMl", r.waterMl); o.put("createdAt", r.createdAt)
            recipeArr.put(o)
        }
        json.put("recipes", recipeArr)
        val customArr = JSONArray()
        for (c in custom) {
            val o = JSONObject()
            o.put("id", c.id); o.put("name", c.name); o.put("category", c.category)
            o.put("ratioMin", c.ratioMin); o.put("ratioMax", c.ratioMax)
            o.put("ratioDefault", c.ratioDefault); o.put("tempMode", c.tempMode)
            o.put("tempMin", c.tempMin); o.put("tempMax", c.tempMax)
            o.put("grind", c.grind); o.put("cupMl", c.cupMl)
            o.put("hasBloom", c.hasBloom); o.put("createdAt", c.createdAt)
            customArr.put(o)
        }
        json.put("customEquipment", customArr)
        if (settings != null) {
            val o = JSONObject()
            o.put("themeMode", settings.themeMode); o.put("paletteKey", settings.paletteKey)
            o.put("language", settings.language)
            json.put("settings", o)
        }
        val logArr = JSONArray()
        for (l in logs) {
            val o = JSONObject()
            o.put("equipmentId", l.equipmentId); o.put("equipmentName", l.equipmentName)
            o.put("strength", l.strength.toDouble()); o.put("roast", l.roast)
            o.put("ratioDenominator", l.ratioDenominator); o.put("coffeeGrams", l.coffeeGrams)
            o.put("waterMl", l.waterMl); o.put("grind", l.grind)
            o.put("customGrindSize", l.customGrindSize); o.put("tempCelsius", l.tempCelsius)
            o.put("totalDurationSec", l.totalDurationSec); o.put("rating", l.rating)
            o.put("tastingNotes", l.tastingNotes); o.put("timestamp", l.timestamp)
            logArr.put(o)
        }
        json.put("brewLogs", logArr)
        return json.toString(2)
    }

    suspend fun importFromJson(jsonStr: String) {
        val json = JSONObject(jsonStr)
        if (json.has("recipes")) {
            val arr = json.getJSONArray("recipes")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                db.recipeDao().insert(RecipeEntity(
                    name = o.getString("name"), equipmentId = o.getString("equipmentId"),
                    strength = o.getDouble("strength").toFloat(), roast = o.getString("roast"),
                    inputByCups = o.getBoolean("inputByCups"), cups = o.getInt("cups"),
                    waterMl = o.getInt("waterMl"), createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                ))
            }
        }
        if (json.has("customEquipment")) {
            val arr = json.getJSONArray("customEquipment")
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                db.customEquipmentDao().insert(CustomEquipmentEntity(
                    id = o.getString("id"), name = o.getString("name"), category = o.getString("category"),
                    ratioMin = o.getDouble("ratioMin"), ratioMax = o.getDouble("ratioMax"),
                    ratioDefault = o.getDouble("ratioDefault"), tempMode = o.getString("tempMode"),
                    tempMin = o.getInt("tempMin"), tempMax = o.getInt("tempMax"),
                    grind = o.getString("grind"), cupMl = o.getInt("cupMl"),
                    hasBloom = o.getBoolean("hasBloom"), createdAt = o.optLong("createdAt", System.currentTimeMillis()),
                ))
            }
        }
        if (json.has("settings")) {
            val o = json.getJSONObject("settings")
            db.settingsDao().upsert(SettingsEntity(
                themeMode = o.optString("themeMode", "system"),
                paletteKey = o.optString("paletteKey", "TERRACOTTA"),
                language = o.optString("language", "en"),
            ))
        }
    }

    private fun CustomEquipmentEntity.toEquipment(): Equipment {
        val cat = BrewCategory.fromKey(category)
        return Equipment(
            id = id,
            nameRes = 0,
            tagRes = 0,
            category = cat,
            ratioMin = ratioMin,
            ratioMax = ratioMax,
            ratioDefault = ratioDefault,
            tempMode = TempMode.fromKey(tempMode),
            tempMin = tempMin,
            tempMax = tempMax,
            grind = Grind.fromKey(grind),
            cupMl = cupMl,
            timeLabel = "",
            hasBloom = hasBloom,
            steps = defaultStepsFor(cat, hasBloom),
            isCustom = true,
            customName = name,
        )
    }

    companion object {
        fun defaultsFor(category: BrewCategory): CategoryDefaults = when (category) {
            BrewCategory.POUR_OVER -> CategoryDefaults(
                15.0, 17.0, 16.5, TempMode.RANGE, 90, 96, Grind.MEDIUM, 250, true,
            )
            BrewCategory.IMMERSION -> CategoryDefaults(
                14.0, 17.0, 15.0, TempMode.RANGE, 92, 96, Grind.COARSE, 250, false,
            )
            BrewCategory.PRESSURE -> CategoryDefaults(
                8.0, 12.0, 10.0, TempMode.RANGE, 88, 95, Grind.MED_FINE, 60, false,
            )
            BrewCategory.OTHER -> CategoryDefaults(
                10.0, 16.0, 13.0, TempMode.RANGE, 90, 96, Grind.MEDIUM, 120, false,
            )
        }

        /** Generic timed routine synthesized for user-created gear. */
        fun defaultStepsFor(category: BrewCategory, hasBloom: Boolean): List<BrewStepDef> {
            val steps = ArrayList<BrewStepDef>()
            when (category) {
                BrewCategory.POUR_OVER -> {
                    if (hasBloom) steps += BrewStepDef(R.string.step_bloom, StepKind.BLOOM, 40, 0.18f)
                    steps += BrewStepDef(R.string.step_pour, StepKind.POUR, 45, 0.6f)
                    steps += BrewStepDef(R.string.step_pour, StepKind.POUR, 45, 1.0f)
                    steps += BrewStepDef(R.string.step_drawdown, StepKind.DRAWDOWN, 55, -1f)
                }
                BrewCategory.IMMERSION -> {
                    if (hasBloom) steps += BrewStepDef(R.string.step_bloom, StepKind.BLOOM, 30, 0.13f)
                    steps += BrewStepDef(R.string.step_add_coffee, StepKind.POUR, 15, 1.0f)
                    steps += BrewStepDef(R.string.step_steep, StepKind.STEEP, 240, -1f)
                    steps += BrewStepDef(R.string.step_serve, StepKind.SERVE, 10, -1f)
                }
                BrewCategory.PRESSURE -> {
                    steps += BrewStepDef(R.string.step_add_coffee, StepKind.POUR, 15, 1.0f)
                    steps += BrewStepDef(R.string.step_steep, StepKind.STEEP, 120, -1f)
                    steps += BrewStepDef(R.string.step_press, StepKind.PRESS, 30, -1f)
                }
                BrewCategory.OTHER -> {
                    steps += BrewStepDef(R.string.step_add_coffee, StepKind.STIR, 20, 1.0f)
                    steps += BrewStepDef(R.string.step_steep, StepKind.STEEP, 120, -1f)
                    steps += BrewStepDef(R.string.step_serve, StepKind.SERVE, 20, -1f)
                }
            }
            return steps
        }
    }
}
