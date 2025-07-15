package com.example.Store.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.Store.data.local.entity.ProductLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductLocation(productLocation: ProductLocationEntity)

    @Update
    suspend fun updateProductLocation(productLocation: ProductLocationEntity)

    @Delete
    suspend fun deleteProductLocation(productLocation: ProductLocationEntity)

    // Step 4c.2: Get all locations for a specific product
    @Query("SELECT * FROM product_locations WHERE productId = :productId")
    fun getLocationsForProduct(productId: String): Flow<List<ProductLocationEntity>>

    // Step 4c.2: Get all products at a specific location
    @Query("SELECT * FROM product_locations WHERE locationId = :locationId")
    fun getProductsAtLocation(locationId: String): Flow<List<ProductLocationEntity>>

    // Step 4c.2: Get the quantity of a specific product at a specific location (and specific spot)
    @Query("SELECT quantity FROM product_locations WHERE productId = :productId AND locationId = :locationId AND aisle = :aisle AND shelf = :shelf AND level = :level")
    fun getProductQuantityAtSpot(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?
    ): Flow<Int?>

    // Helper to get a specific ProductLocationEntity to update it
    @Query("SELECT * FROM product_locations WHERE productId = :productId AND locationId = :locationId AND aisle IS :aisle AND shelf IS :shelf AND level IS :level LIMIT 1")
    suspend fun findProductLocation(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?
    ): ProductLocationEntity?


    // Sums the quantity of a specific product across all locations.
    @Query("SELECT SUM(quantity) FROM product_locations WHERE productId = :productId")
    fun getTotalStockForProduct(productId: String): Flow<Int?>

    @Query("UPDATE product_locations SET quantity = :newQuantity WHERE productLocationId = :productLocationId")
    suspend fun updateQuantityForProductLocation(productLocationId: String, newQuantity: Int)

    @Query("DELETE FROM product_locations")
    suspend fun deleteAll()

    // Transactional methods will be implemented in a repository or use case
    // as they require multiple DAO operations.
    // For example, transferStock would involve two updates and could be handled
    // in a @Transaction annotated method in the AppDatabase or a repository.
}