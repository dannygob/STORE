package com.example.Store.di

import com.example.Store.data.repository.AppRepository
import com.example.Store.data.repository.AuthRepositoryImpl
import com.example.Store.domain.repository.AuthRepository
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

    @Binds
    @Singleton
    abstract fun bindAppRepository(
        impl: AppRepositoryImpl,
    ): AppRepository
}
