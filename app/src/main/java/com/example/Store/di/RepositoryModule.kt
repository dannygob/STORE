package com.example.Store.di

import com.example.Store.data.repository.AppRepository
import com.example.Store.data.repository.AppRepositoryImpl
import com.example.Store.data.repository.AuthRepositoryImpl
import com.example.Store.data.repository.FirestoreService
import com.example.Store.data.repository.FirestoreServiceImpl
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

    @Provides
    @Singleton
    fun provideAppRepository(
        productDao: ProductDao,
        customerDao: CustomerDao,
        supplierDao: SupplierDao,
        orderDao: OrderDao,
        orderItemDao: OrderItemDao,
        warehouseDao: WarehouseDao,
        locationDao: LocationDao,
        productLocationDao: ProductLocationDao,
        preferenceDao: PreferenceDao,
        firestoreService: FirestoreService,
        externalScope: CoroutineScope,
    ): AppRepository {
        return AppRepositoryImpl(
            productDao,
            customerDao,
            supplierDao,
            orderDao,
            orderItemDao,
            warehouseDao,
            locationDao,
            productLocationDao,
            preferenceDao,
            firestoreService,
            externalScope,
        )
    }

    @Binds
    @Singleton
    abstract fun bindFirestoreService(
        impl: FirestoreServiceImpl,
    ): FirestoreService
}
