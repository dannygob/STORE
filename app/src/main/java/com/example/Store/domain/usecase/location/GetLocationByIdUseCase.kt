package com.example.Store.domain.usecase.location

import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationByIdUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    operator fun invoke(locationId: String): Flow<LocationEntity?> {
        return appRepository.getLocationById(locationId)
    }
}
