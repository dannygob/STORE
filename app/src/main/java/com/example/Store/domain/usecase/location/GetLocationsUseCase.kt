package com.example.Store.domain.usecase.location

import com.example.Store.domain.model.Location
import com.example.Store.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(): Flow<List<Location>> {
        return appRepository.getAllLocations().map { locations ->
            locations.map { it.toDomainModel() }
        }
    }
}