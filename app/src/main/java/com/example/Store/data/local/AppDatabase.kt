package com.example.Store.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.Store.data.local.dao.CustomerDao
import com.example.Store.data.local.dao.LocationDao
import com.example.Store.data.local.dao.OrderDao
import com.example.Store.data.local.dao.OrderItemDao
import com.example.Store.data.local.dao.PreferenceDao
import com.example.Store.data.local.dao.ProductDao
import com.example.Store.data.local.dao.ProductLocationDao
import com.example.Store.data.local.dao.StockAtWarehouseDao
import com.example.Store.data.local.dao.SupplierDao
import com.example.Store.data.local.dao.WarehouseDao
import com.example.Store.data.local.entity.CustomerEntity
import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.local.entity.OrderEntity
import com.example.Store.data.local.entity.OrderItemEntity
import com.example.Store.data.local.entity.PreferenceEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity
import com.example.Store.data.local.entity.StockAtWarehouseEntity
import com.example.Store.data.local.entity.SupplierEntity
import com.example.Store.data.local.entity.WarehouseEntity

@Database(
    entities = [
        ProductEntity::class,
        CustomerEntity::class,
        SupplierEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        PreferenceEntity::class,
        WarehouseEntity::class,
        LocationEntity::class,
        ProductLocationEntity::class,
        StockAtWarehouseEntity::class // ✅ Añadido para compatibilidad
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs existentes
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun warehouseDao(): WarehouseDao
    abstract fun locationDao(): LocationDao
    abstract fun productLocationDao(): ProductLocationDao

    // ✅ Restaurado correctamente
    abstract fun stockAtWarehouseDao(): StockAtWarehouseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS product_locations (
                        productLocationId TEXT NOT NULL,
                        productId TEXT NOT NULL,
                        locationId TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        aisle TEXT,
                        shelf TEXT,
                        level TEXT,
                        PRIMARY KEY(productLocationId),
                        FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE,
                        FOREIGN KEY(locationId) REFERENCES locations(locationId) ON DELETE CASCADE
                    )
                """.trimIndent()
                )

                db.execSQL(
                    """
                    INSERT INTO product_locations (productLocationId, productId, locationId, quantity)
                    SELECT stockId, productId, warehouseId, quantity FROM stock_at_warehouse
                """.trimIndent()
                )

                db.execSQL("DROP TABLE IF EXISTS stock_at_warehouse")

                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS index_product_locations_productId_locationId_aisle_shelf_level
                    ON product_locations (productId, locationId, aisle, shelf, level)
                """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS index_product_locations_locationId
                    ON product_locations (locationId)
                """.trimIndent()
                )

                db.execSQL("ALTER TABLE warehouses ADD COLUMN notes TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store_app_database"
                )
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration(false) // ⚠️ Para desarrollo únicamente
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
