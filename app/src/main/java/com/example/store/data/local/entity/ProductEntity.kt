package com.example.store.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@IgnoreExtraProperties // Ignora campos extra en Firestore, evitando errores de deserialización
@Entity(
    tableName = "products",
    indices = [Index(value = ["name"]), Index(value = ["category"])]
)
data class ProductEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val category: String? = null,
    val price: Double,
    val stockQuantity: Int,
    val imageUrl: String? = null,
    val supplierId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val needsSync: Boolean = false,
    val isActive: Boolean = true, // por si decides hacer soft-deletes o filtrar productos ocultos
) : Parcelable