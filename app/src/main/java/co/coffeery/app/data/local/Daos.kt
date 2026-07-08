package co.coffeery.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity): Long

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface CustomEquipmentDao {
    @Query("SELECT * FROM custom_equipment ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CustomEquipmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipment: CustomEquipmentEntity)

    @Query("DELETE FROM custom_equipment WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 'app'")
    fun observe(): Flow<SettingsEntity?>

    @androidx.room.Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(settings: SettingsEntity)
}

@Dao
interface BrewLogDao {
    @Query("SELECT * FROM brew_logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<BrewLogEntity>>

    @Insert
    suspend fun insert(log: BrewLogEntity): Long

    @Delete
    suspend fun delete(log: BrewLogEntity)

    @Query("DELETE FROM brew_logs WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface BeanDao {
    @Query("SELECT * FROM beans WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<BeanEntity>>

    @Insert
    suspend fun insert(bean: BeanEntity): Long

    @Query("UPDATE beans SET isArchived = 1 WHERE id = :id")
    suspend fun archiveById(id: Long)
}
