package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey val key: String, // The preference key, e.g., "user_theme", "notifications_enabled"
    val value: String,            // The preference value, stored as a String. Type conversion handled elsewhere.
)
