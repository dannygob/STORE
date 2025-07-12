package com.example.Store.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.Store.data.local.entity.UserPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: UserPreferenceEntity)

    // Gets the raw entity. The value is a String.
    @Query("SELECT * FROM user_preferences WHERE `key` = :key") // `key` is a reserved keyword in SQL, so escape it.
    fun getPreferenceEntity(key: String): Flow<UserPreferenceEntity?>

    // Convenience method to directly get the string value.
    @Query("SELECT value FROM user_preferences WHERE `key` = :key")
    fun getPreferenceValue(key: String): Flow<String?>

    @Query("DELETE FROM user_preferences WHERE `key` = :key")
    suspend fun deletePreference(key: String)

    @Query("DELETE FROM user_preferences")
    suspend fun deleteAllPreferences()
}
