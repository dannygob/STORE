package com.example.store.domain.usecase.location

import com.example.store.data.repository.AppRepository
import com.example.store.domain.model.Location
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