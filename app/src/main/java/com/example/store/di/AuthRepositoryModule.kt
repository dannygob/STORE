package com.example.store.di

import com.example.store.data.repository.AuthRepositoryImpl
import com.example.store.data.repository.FirestoreService
import com.example.store.data.repository.FirestoreServiceImpl
import com.example.store.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFirestoreService(
        impl: FirestoreServiceImpl,
    ): FirestoreService
}
