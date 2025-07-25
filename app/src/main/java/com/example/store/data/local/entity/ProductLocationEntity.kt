package com.example.store.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.store.domain.model.ProductLocation

@Entity(
    tableName = "product_locations",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LocationEntity::class,
            parentColumns = ["locationId"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["productId", "locationId", "aisle", "shelf", "level"], unique = true),
        Index(value = ["locationId"])
    ]
)
data class ProductLocationEntity(
    @PrimaryKey val productLocationId: String = generateId(),
    val productId: String,
    val locationId: String,
    val quantity: Int,
    val aisle: String?,
    val shelf: String?,
    val level: String?,
) {
    companion object {
        fun generateId(
            productId: String = "",
            locationId: String = "",
            aisle: String? = null,
            shelf: String? = null,
            level: String? = null,
        ): String {
            return listOf(
                productId,
                locationId,
                aisle ?: "_",
                shelf ?: "_",
                level ?: "_"
            ).joinToString("_")
        }
    }

    fun toDomainModel() = ProductLocation(
        productLocationId = productLocationId,
        productId = productId,
        locationId = locationId,
        quantity = quantity,
        aisle = aisle,
        shelf = shelf,
        level = level
    )
}