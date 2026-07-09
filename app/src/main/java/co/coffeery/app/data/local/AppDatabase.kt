package co.coffeery.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [RecipeEntity::class, CustomEquipmentEntity::class, SettingsEntity::class, BrewLogEntity::class, BeanEntity::class],
    version = 5,
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

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE settings ADD COLUMN completedChapters TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE settings ADD COLUMN ratioMode INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE settings ADD COLUMN manualRatio REAL NOT NULL DEFAULT 16.0")
                db.execSQL("ALTER TABLE brew_logs ADD COLUMN beanName TEXT NOT NULL DEFAULT ''")
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffeery.db",
                ).addMigrations(MIGRATION_4_5).build().also { INSTANCE = it }
            }
    }
}
