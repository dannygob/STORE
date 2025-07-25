package com.example.store.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.store.data.local.entity.UserPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(preference: UserPreferenceEntity)

    @Query("SELECT value FROM user_preferences WHERE `key` = :key")
    fun get(key: String): Flow<String?>

    @Query("DELETE FROM user_preferences WHERE `key` = :key")
    suspend fun delete(key: String)

    @Query("DELETE FROM user_preferences")
    suspend fun clearAll()
}
