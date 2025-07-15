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
// Cleaned up duplicate imports
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.store.data.local.dao.*
import com.example.store.data.local.entity.*


@Database(
    entities = [
        ProductEntity::class,
        CustomerEntity::class,
        SupplierEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        UserPreferenceEntity::class,
        LocationEntity::class,
        ProductLocationEntity::class // Replaced StockAtWarehouseEntity
    ],
    version = 8, // Incremented version to 8
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun locationDao(): LocationDao
    abstract fun productLocationDao(): ProductLocationDao // Renamed from stockAtWarehouseDao

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

    @Transaction
    open suspend fun transferStock(
        productId: String,
        fromLocationId: String, fromAisle: String?, fromShelf: String?, fromLevel: String?,
        toLocationId: String, toAisle: String?, toShelf: String?, toLevel: String?,
        amount: Int
    ) {
        val dao = productLocationDao()

        // Find the source location for the product
        val sourceLocation = dao.findProductLocation(productId, fromLocationId, fromAisle, fromShelf, fromLevel)
        require(sourceLocation != null && sourceLocation.quantity >= amount) { "Insufficient stock at source location." }

        // Decrease stock at source
        val newSourceQuantity = sourceLocation.quantity - amount
        dao.updateQuantityForProductLocation(sourceLocation.productLocationId, newSourceQuantity)

        // Find or create destination location
        val destLocation = dao.findProductLocation(productId, toLocationId, toAisle, toShelf, toLevel)
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
        val MIGRATION_5_6: Migration = object : Migration(5, 6) { /* ... */ }
        val MIGRATION_6_7: Migration = object : Migration(6, 7) { /* ... */ }

        // New migration for refactoring 'stock_at_warehouse' to 'product_locations'
        val MIGRATION_7_8: Migration = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create the new 'product_locations' table with the correct schema
                db.execSQL("CREATE TABLE product_locations (productLocationId TEXT NOT NULL, productId TEXT NOT NULL, locationId TEXT NOT NULL, quantity INTEGER NOT NULL, aisle TEXT, shelf TEXT, level TEXT, PRIMARY KEY(productLocationId), FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE, FOREIGN KEY(locationId) REFERENCES locations(locationId) ON DELETE CASCADE)")

                // 2. Copy data from the old 'stock_at_warehouse' table to the new one.
                // Note: The new 'aisle', 'shelf', 'level' columns will be NULL for old data.
                db.execSQL("INSERT INTO product_locations (productLocationId, productId, locationId, quantity) SELECT stockId, productId, warehouseId, quantity FROM stock_at_warehouse")

                // 3. Drop the old 'stock_at_warehouse' table
                db.execSQL("DROP TABLE stock_at_warehouse")

                // Create indices for the new table as defined in the entity
                db.execSQL("CREATE UNIQUE INDEX index_product_locations_productId_locationId_aisle_shelf_level ON product_locations (productId, locationId, aisle, shelf, level)")
                db.execSQL("CREATE INDEX index_product_locations_locationId ON product_locations (locationId)")
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
                .addMigrations(MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8) // Add the new migration
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
