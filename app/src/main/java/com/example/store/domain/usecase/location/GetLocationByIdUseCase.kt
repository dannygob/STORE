package com.example.store.domain.usecase.location

import com.example.store.data.local.entity.LocationEntity
import com.example.store.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(locationId: String): Flow<LocationEntity?> {
        return appRepository.getLocationById(locationId)
    }
}
