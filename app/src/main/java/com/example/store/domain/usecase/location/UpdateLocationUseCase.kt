package com.example.store.domain.usecase.location

import com.example.store.data.local.entity.toEntity
import com.example.store.data.repository.AppRepository
import com.example.store.domain.model.Location
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(location: Location) {
        appRepository.updateLocation(location.toEntity())
    }
}