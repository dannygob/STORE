package com.example.Store.data.local.entity


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "product_locations", // Renamed table
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationEntity::class, // Updated FK entity
            parentColumns = ["locationId"], // Updated FK parent column
            childColumns = ["locationId"], // Updated FK child column
            onDelete = ForeignKey.CASCADE
        )
    ],
    // A product can be in multiple spots in the same location, so the unique key is on the exact spot.
    indices = [
        Index(value = ["productId", "locationId", "aisle", "shelf", "level"], unique = true),
        Index(value = ["locationId"]) // Index for querying by location
    ]
)
data class ProductLocationEntity( // Renamed class
    @PrimaryKey val productLocationId: String = UUID.randomUUID().toString(), // Renamed PK
    val productId: String,
    val locationId: String, // Renamed field
    val quantity: Int,
    // New fields for specific location tracking
    val aisle: String?,
    val shelf: String?,
    val level: String?
)