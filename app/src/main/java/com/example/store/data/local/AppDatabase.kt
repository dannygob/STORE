package com.example.store.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.dao.OrderDao // New import
import com.example.store.data.local.dao.OrderItemDao // New import
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.dao.OrderDao
import com.example.store.data.local.dao.OrderItemDao
import com.example.store.data.local.dao.UserPreferenceDao
import com.example.store.data.local.dao.WarehouseDao
import com.example.store.data.local.dao.StockAtWarehouseDao // New import
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.UserPreferenceEntity
import com.example.store.data.local.entity.WarehouseEntity
import com.example.store.data.local.entity.StockAtWarehouseEntity // New import


@Database(
    entities = [
        ProductEntity::class,
        CustomerEntity::class,
        SupplierEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        UserPreferenceEntity::class,
        WarehouseEntity::class,
        StockAtWarehouseEntity::class // Added StockAtWarehouseEntity
    ],
    version = 5, // Incremented version
    exportSchema = false // For now, we'll keep schema export off. Can be enabled for complex migrations.
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun warehouseDao(): WarehouseDao
    abstract fun stockAtWarehouseDao(): StockAtWarehouseDao // Added StockAtWarehouseDao accessor

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store_app_database" // Name of the database file
                )
                // Add migrations here if/when schema changes
                // .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // Simple for now, replace with proper migrations in production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
