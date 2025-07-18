package com.example.Store.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "locations") // Renamed table
data class LocationEntity( // Renamed class
    @PrimaryKey val locationId: String = UUID.randomUUID().toString(), // Renamed field
    val name: String,
    val address: String?,
    // Capacity and notes are generic enough to remain
    val capacity: Double?,
    val notes: String? = null
)