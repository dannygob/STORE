package com.example.store.di

import com.example.store.util.NetworkChecker
import com.example.store.util.NetworkCheckerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNetworkChecker(
        networkCheckerImpl: NetworkCheckerImpl,
    ): NetworkChecker
}
