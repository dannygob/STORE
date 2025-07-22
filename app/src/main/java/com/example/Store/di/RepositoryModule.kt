package com.example.Store.di

import com.example.Store.data.local.dao.CustomerDao
import com.example.Store.data.local.dao.LocationDao
import com.example.Store.data.local.dao.OrderDao
import com.example.Store.data.local.dao.OrderItemDao
import com.example.Store.data.local.dao.PreferenceDao
import com.example.Store.data.local.dao.ProductDao
import com.example.Store.data.local.dao.ProductLocationDao
import com.example.Store.data.local.dao.SupplierDao
import com.example.Store.data.local.dao.WarehouseDao
import com.example.Store.data.repository.AppRepository
import com.example.Store.data.repository.AppRepositoryImpl
import com.example.Store.data.repository.FirestoreService
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
}
