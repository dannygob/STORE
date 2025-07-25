package com.example.store.di

import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.LocationDao
import com.example.store.data.local.dao.OrderDao
import com.example.store.data.local.dao.OrderItemDao
import com.example.store.data.local.dao.PreferenceDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.ProductLocationDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.repository.AppRepository
import com.example.store.data.repository.AppRepositoryImpl
import com.example.store.data.repository.FirestoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideAppRepository(
        productDao: ProductDao,
        customerDao: CustomerDao,
        supplierDao: SupplierDao,
        orderDao: OrderDao,
        orderItemDao: OrderItemDao,
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
            locationDao,
            productLocationDao,
            preferenceDao,
            firestoreService,
            externalScope,
        )
    }
}
