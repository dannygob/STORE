package com.example.store.di

import com.example.store.data.repository.AppRepository
import com.example.store.domain.repository.AuthRepository
import com.example.store.domain.usecase.LoginUseCase
import com.example.store.domain.usecase.inventory.GeneratePickListUseCase
import com.example.store.util.NetworkChecker // Import NetworkChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository,
        networkChecker: NetworkChecker, // Corrected to NetworkChecker
    ): LoginUseCase {
        return LoginUseCase(authRepository, networkChecker)
    }

    @Provides
    @Singleton
    fun provideGeneratePickListUseCase(
        appRepository: AppRepository,
    ): GeneratePickListUseCase {
        return GeneratePickListUseCase(appRepository)
    }

    // Aquí podrías agregar más use cases si deseas:
    // @Provides
    // fun provideLogoutUseCase(...)
}
