package com.example.Store.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "customers",
    indices = [Index(value = ["email"], unique = true)]
)
data class CustomerEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String?, // Unique, but can be null if not provided
    val phone: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val city: String?,
    val postalCode: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?
)
