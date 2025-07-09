package com.example.store.di

import android.content.Context
import com.example.store.data.local.AppDatabase
import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.dao.OrderDao
import com.example.store.data.local.dao.OrderItemDao
import com.example.store.data.local.dao.UserPreferenceDao
import com.example.store.data.local.dao.WarehouseDao
import com.example.store.data.local.dao.StockAtWarehouseDao // New
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
    fun provideOrderItemDao(appDatabase: AppDatabase): OrderItemDao {
        return appDatabase.orderItemDao()
    }

    @Provides
    @Singleton
    fun provideUserPreferenceDao(appDatabase: AppDatabase): UserPreferenceDao {
        return appDatabase.userPreferenceDao()
    }

    @Provides
    @Singleton
    fun provideWarehouseDao(appDatabase: AppDatabase): WarehouseDao {
        return appDatabase.warehouseDao()
    }

    @Provides
    @Singleton
    fun provideStockAtWarehouseDao(appDatabase: AppDatabase): StockAtWarehouseDao { // New provider
        return appDatabase.stockAtWarehouseDao()
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        productDao: ProductDao,
        customerDao: CustomerDao,
        supplierDao: SupplierDao,
        orderDao: OrderDao,
        orderItemDao: OrderItemDao,
        userPreferenceDao: UserPreferenceDao,
        warehouseDao: WarehouseDao,
        stockAtWarehouseDao: StockAtWarehouseDao,
        appDatabase: AppDatabase // Added AppDatabase
    ): AppRepository {
        return AppRepositoryImpl(appDatabase, productDao, customerDao, supplierDao, orderDao, orderItemDao, userPreferenceDao, warehouseDao, stockAtWarehouseDao)
    }
}
