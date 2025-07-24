package com.example.Store.data.local.entity

import com.example.Store.domain.model.Location

fun Location.toEntity() = LocationEntity(
    locationId = locationId,
    name = name,
    address = address,
    capacity = capacity,
    notes = notes
)
