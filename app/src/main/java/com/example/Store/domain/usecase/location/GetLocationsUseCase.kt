package com.example.Store.domain.usecase.location


import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.repository.AppRepository

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Flow<List<LocationEntity>> {
        return appRepository.getAllLocations()
    }
}