package com.example.store.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.store.data.local.dao.CustomerDao
import com.example.store.data.local.dao.LocationDao
import com.example.store.data.local.dao.OrderDao
import com.example.store.data.local.dao.OrderItemDao
import com.example.store.data.local.dao.PreferenceDao
import com.example.store.data.local.dao.ProductDao
import com.example.store.data.local.dao.ProductLocationDao
import com.example.store.data.local.dao.SupplierDao
import com.example.store.data.local.dao.UserDao
import com.example.store.data.local.entity.CustomerEntity
import com.example.store.data.local.entity.LocationEntity
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.PreferenceEntity
import com.example.store.data.local.entity.ProductEntity
import com.example.store.data.local.entity.ProductLocationEntity
import com.example.store.data.local.entity.StockAtWarehouseEntity
import com.example.store.data.local.entity.SupplierEntity
import com.example.store.data.local.entity.UserEntity
import com.example.store.data.local.entity.UserPreferenceEntity

@Database(
    entities = [
        ProductEntity::class,
        CustomerEntity::class,
        SupplierEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        PreferenceEntity::class,
        LocationEntity::class,
        ProductLocationEntity::class,
        StockAtWarehouseEntity::class,
        UserPreferenceEntity::class,
        UserEntity::class // ✅ Añadido para soporte de usuario autenticado
    ],
    version = 11,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs existentes
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun locationDao(): LocationDao
    abstract fun productLocationDao(): ProductLocationDao

    // ✅ Nuevo DAO para usuarios
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_5_6 = object : Migration(5, 6) {
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

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE order_items RENAME COLUMN pricePerUnit TO price")
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE products ADD COLUMN description TEXT")
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS users (
                        uid TEXT NOT NULL PRIMARY KEY,
                        email TEXT NOT NULL,
                        passwordHash TEXT NOT NULL DEFAULT '',
                        role TEXT NOT NULL,
                        needsSync INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add lastSyncTimestamp column to users table
                db.execSQL("ALTER TABLE users ADD COLUMN lastSyncTimestamp INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Add lastUpdatedAt and needsSync columns to products table
                db.execSQL("ALTER TABLE products ADD COLUMN lastUpdatedAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE products ADD COLUMN needsSync INTEGER NOT NULL DEFAULT 0")
            }
        }

        // ✅ Método accesible para SyncWorker sin Hilt
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store_app_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO products (id, name, price, description, stockQuantity, createdAt, isActive) VALUES ('1', 'Sample Product', 10.0, 'Sample Description', 0, ${System.currentTimeMillis()}, 1)") // Added isActive
                            db.execSQL("INSERT INTO customers (id, name) VALUES ('1', 'Sample Customer')")
                            db.execSQL("INSERT INTO suppliers (id, name) VALUES ('1', 'Sample Supplier')")
                        }
                    })
                    .addMigrations(
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11
                    )
                    .fallbackToDestructiveMigration() // Add this line to allow destructive migrations during development
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
