package com.example.Store.domain.model

data class Location(
    val locationId: String,
    val name: String,
    val address: String?,
    val capacity: Double?,
    val notes: String?
)
