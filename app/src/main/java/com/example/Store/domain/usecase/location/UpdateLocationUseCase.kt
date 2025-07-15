package com.example.Store.domain.usecase.location

import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.repository.AppRepository
import javax.inject.Inject


class UpdateLocationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(location: LocationEntity) {
        appRepository.updateLocation(location)
    }
}