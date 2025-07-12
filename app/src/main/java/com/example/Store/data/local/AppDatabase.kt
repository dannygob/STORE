package com.example.Store.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.Store.data.local.dao.CustomerDao
import com.example.Store.data.local.dao.OrderDao
import com.example.Store.data.local.dao.OrderItemDao
import com.example.Store.data.local.dao.ProductDao
import com.example.Store.data.local.dao.StockAtWarehouseDao
import com.example.Store.data.local.dao.SupplierDao
import com.example.Store.data.local.dao.UserPreferenceDao
import com.example.Store.data.local.dao.WarehouseDao
import com.example.Store.data.local.entity.CustomerEntity
import com.example.Store.data.local.entity.OrderEntity
import com.example.Store.data.local.entity.OrderItemEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.StockAtWarehouseEntity
import com.example.Store.data.local.entity.SupplierEntity
import com.example.Store.data.local.entity.UserPreferenceEntity
import com.example.Store.data.local.entity.WarehouseEntity


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
    version = 6, // Incremented version to 6
    exportSchema = true // Enabled schema export
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
        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE warehouses ADD COLUMN notes TEXT")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store_app_database" // Name of the database file
                )
                .addMigrations(MIGRATION_5_6) // Added specific migration
                // .fallbackToDestructiveMigration() // Removed for versions covered by migrations
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
