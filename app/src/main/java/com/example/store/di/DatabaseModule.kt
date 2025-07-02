package com.example.store.di

import android.content.Context
import com.example.store.data.local.AppDatabase
import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.dao.OrderDao // New
import com.example.store.data.local.dao.OrderItemDao // New
import com.example.store.data.repository.AppRepository
import com.example.store.data.repository.AppRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideCustomerDao(appDatabase: AppDatabase): CustomerDao {
        return appDatabase.customerDao()
    }

    @Provides
    @Singleton
    fun provideSupplierDao(appDatabase: AppDatabase): SupplierDao {
        return appDatabase.supplierDao()
    }

    @Provides
    @Singleton
    fun provideOrderDao(appDatabase: AppDatabase): OrderDao { // New provider
        return appDatabase.orderDao()
    }

    @Provides
    @Singleton
    fun provideOrderItemDao(appDatabase: AppDatabase): OrderItemDao { // New provider
        return appDatabase.orderItemDao()
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        productDao: ProductDao,
        customerDao: CustomerDao,
        supplierDao: SupplierDao,
        orderDao: OrderDao,             // Added OrderDao
        orderItemDao: OrderItemDao      // Added OrderItemDao
    ): AppRepository {
        return AppRepositoryImpl(productDao, customerDao, supplierDao, orderDao, orderItemDao)
    }
}
