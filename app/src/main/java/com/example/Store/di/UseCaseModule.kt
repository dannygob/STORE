package com.example.Store.di


import com.example.Store.domain.repository.AuthRepository
import com.example.Store.domain.usecase.LoginUseCase
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

    // Aquí podrías agregar más use cases si deseas:
    // @Provides
    // fun provideLogoutUseCase(...)
}
