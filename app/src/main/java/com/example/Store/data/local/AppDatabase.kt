package com.example.Store.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.Store.data.local.dao.CustomerDao
import com.example.Store.data.local.dao.LocationDao
import com.example.Store.data.local.dao.OrderDao
import com.example.Store.data.local.dao.OrderItemDao
import com.example.Store.data.local.dao.ProductDao
import com.example.Store.data.local.dao.ProductLocationDao
import com.example.Store.data.local.dao.SupplierDao
import com.example.Store.data.local.dao.UserPreferenceDao
import com.example.Store.data.local.dao.WarehouseDao
import com.example.Store.data.local.entity.CustomerEntity
import com.example.Store.data.local.entity.LocationEntity
import com.example.Store.data.local.entity.OrderEntity
import com.example.Store.data.local.entity.OrderItemEntity
import com.example.Store.data.local.entity.ProductEntity
import com.example.Store.data.local.entity.ProductLocationEntity
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
        LocationEntity::class,              // ✅ AÑADIDO
        ProductLocationEntity::class        // ✅ Ya está bien
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun warehouseDao(): WarehouseDao
    abstract fun locationDao(): LocationDao
    abstract fun productLocationDao(): ProductLocationDao

    // ✅ Eliminado: abstract fun stockAtWarehouseDao()

    // Transacción: Añadir stock
    @Transaction
    open suspend fun addStockToLocation(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?,
        amount: Int
    ) {
        val dao = productLocationDao()
        val existingLocation = dao.findProductLocation(productId, locationId, aisle, shelf, level)

        if (existingLocation != null) {
            val newQuantity = existingLocation.quantity + amount
            dao.updateQuantityForProductLocation(existingLocation.productLocationId, newQuantity)
        } else {
            val newProductLocation = ProductLocationEntity(
                productId = productId,
                locationId = locationId,
                quantity = amount,
                aisle = aisle,
                shelf = shelf,
                level = level
            )
            dao.insertProductLocation(newProductLocation)
        }
    }

    // Transacción: Transferencia de stock
    @Transaction
    open suspend fun transferStock(
        productId: String,
        fromLocationId: String, fromAisle: String?, fromShelf: String?, fromLevel: String?,
        toLocationId: String, toAisle: String?, toShelf: String?, toLevel: String?,
        amount: Int
    ) {
        val dao = productLocationDao()

        val sourceLocation =
            dao.findProductLocation(productId, fromLocationId, fromAisle, fromShelf, fromLevel)
        require(sourceLocation != null && sourceLocation.quantity >= amount) {
            "Insufficient stock at source location."
        }

        val newSourceQuantity = sourceLocation.quantity - amount
        dao.updateQuantityForProductLocation(sourceLocation.productLocationId, newSourceQuantity)

        val destLocation =
            dao.findProductLocation(productId, toLocationId, toAisle, toShelf, toLevel)
        if (destLocation != null) {
            val newDestQuantity = destLocation.quantity + amount
            dao.updateQuantityForProductLocation(destLocation.productLocationId, newDestQuantity)
        } else {
            val newProductLocation = ProductLocationEntity(
                productId = productId,
                locationId = toLocationId,
                quantity = amount,
                aisle = toAisle,
                shelf = toShelf,
                level = toLevel
            )
            dao.insertProductLocation(newProductLocation)
        }
    }

    companion object {

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

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "store_app_database"
                )
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration() // Opcional para testing
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
