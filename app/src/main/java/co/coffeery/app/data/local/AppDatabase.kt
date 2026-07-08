package co.coffeery.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [RecipeEntity::class, CustomEquipmentEntity::class, SettingsEntity::class, BrewLogEntity::class, BeanEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun customEquipmentDao(): CustomEquipmentDao
    abstract fun settingsDao(): SettingsDao
    abstract fun brewLogDao(): BrewLogDao
    abstract fun beanDao(): BeanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffeery.db",
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}
