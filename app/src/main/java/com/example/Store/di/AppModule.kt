package com.example.Store.di

import com.example.Store.util.NetworkChecker
import com.example.Store.util.NetworkCheckerImpl
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
