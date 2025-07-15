package com.example.store.domain.usecase.location

import com.example.store.data.local.entity.LocationEntity
import com.example.store.data.repository.AppRepository
import javax.inject.Inject

class CreateLocationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(location: LocationEntity) {
        appRepository.insertLocation(location)
    }
}
