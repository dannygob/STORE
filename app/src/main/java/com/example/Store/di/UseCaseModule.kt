package com.example.Store.di


import com.example.Store.data.repository.AppRepository
import com.example.Store.domain.repository.AuthRepository
import com.example.Store.domain.usecase.LoginUseCase
import com.example.Store.domain.usecase.inventory.GeneratePickListUseCase
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
    ): LoginUseCase {
        return LoginUseCase(authRepository)
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
