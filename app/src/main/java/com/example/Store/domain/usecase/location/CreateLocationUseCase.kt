package com.example.Store.domain.usecase.location

import com.example.Store.data.local.entity.toEntity
import com.example.Store.domain.model.Location
import com.example.Store.data.repository.AppRepository
import javax.inject.Inject

class CreateLocationUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend operator fun invoke(location: Location) {
        appRepository.insertLocation(location.toEntity())
    }
}