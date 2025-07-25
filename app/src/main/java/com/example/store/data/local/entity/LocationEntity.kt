package com.example.store.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.store.domain.model.Location
import java.util.UUID

@Entity(tableName = "locations") // Renamed table
data class LocationEntity( // Renamed class
    @PrimaryKey val locationId: String = UUID.randomUUID().toString(), // Renamed field
    val name: String,
    val address: String?,
    // Capacity and notes are generic enough to remain
    val capacity: Double?,
    val notes: String? = null
) {
    fun toDomainModel() = Location(
        locationId = locationId,
        name = name,
        address = address,
        capacity = capacity,
        notes = notes
    )
}