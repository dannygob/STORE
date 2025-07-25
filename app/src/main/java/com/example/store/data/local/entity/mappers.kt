package com.example.store.data.local.entity

import com.example.store.domain.model.Location

fun Location.toEntity() = LocationEntity(
    locationId = locationId,
    name = name,
    address = address,
    capacity = capacity,
    notes = notes
)
