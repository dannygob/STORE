package com.example.Store.di

import android.content.Context
import com.example.Store.data.local.AppDatabase
import com.example.Store.data.local.dao.CustomerDao
import com.example.Store.data.local.dao.OrderDao
import com.example.Store.data.local.dao.OrderItemDao
import com.example.Store.data.local.dao.PreferenceDao
import com.example.Store.data.local.dao.ProductDao
import com.example.Store.data.local.dao.StockAtWarehouseDao
import com.example.Store.data.local.dao.SupplierDao
import com.example.Store.data.local.dao.WarehouseDao
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    fun providePreferenceDao(appDatabase: AppDatabase): PreferenceDao {
        return appDatabase.preferenceDao()
    }

    @Provides
    @Singleton
    fun provideWarehouseDao(appDatabase: AppDatabase): WarehouseDao {
        return appDatabase.warehouseDao()
    }

    @Provides
    @Singleton
    fun provideStockAtWarehouseDao(appDatabase: AppDatabase): StockAtWarehouseDao {
        return appDatabase.stockAtWarehouseDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore { // New provider
        return Firebase.firestore
    }
}
