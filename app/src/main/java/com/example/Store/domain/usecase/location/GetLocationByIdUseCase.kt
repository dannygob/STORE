package com.example.Store.domain.usecase.location

import com.example.Store.domain.model.Location
import com.example.Store.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(locationId: String): Flow<Location?> {
        return appRepository.getLocationById(locationId).map { it?.toDomainModel() }
    }
}
