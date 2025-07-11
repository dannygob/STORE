package com.example.store.di

import com.example.store.domain.repository.AuthRepository // Interface from domain
import com.example.store.data.repository.AuthRepositoryImpl // **Corrected: Implementation from data**
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository
}
