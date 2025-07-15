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

    @Query("SELECT * FROM product_locations WHERE productId = :productId")
    fun getLocationsForProduct(productId: String): Flow<List<ProductLocationEntity>>

    @Query("SELECT * FROM product_locations WHERE locationId = :locationId")
    fun getProductsAtLocation(locationId: String): Flow<List<ProductLocationEntity>>

    @Query(
        """
        SELECT quantity FROM product_locations 
        WHERE productId = :productId AND locationId = :locationId 
        AND (:aisle IS NULL AND aisle IS NULL OR aisle = :aisle)
        AND (:shelf IS NULL AND shelf IS NULL OR shelf = :shelf)
        AND (:level IS NULL AND level IS NULL OR level = :level)
    """
    )
    fun getProductQuantityAtSpot(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?
    ): Flow<Int?>

    @Query(
        """
        SELECT * FROM product_locations 
        WHERE productId = :productId AND locationId = :locationId 
        AND (:aisle IS NULL AND aisle IS NULL OR aisle = :aisle)
        AND (:shelf IS NULL AND shelf IS NULL OR shelf = :shelf)
        AND (:level IS NULL AND level IS NULL OR level = :level)
        LIMIT 1
    """
    )
    suspend fun findProductLocation(
        productId: String,
        locationId: String,
        aisle: String?,
        shelf: String?,
        level: String?
    ): ProductLocationEntity?

    @Query("SELECT SUM(quantity) FROM product_locations WHERE productId = :productId")
    fun getTotalStockForProduct(productId: String): Flow<Int?>

    @Query("UPDATE product_locations SET quantity = :newQuantity WHERE productLocationId = :productLocationId")
    suspend fun updateQuantityForProductLocation(productLocationId: String, newQuantity: Int)

    @Query("DELETE FROM product_locations")
    suspend fun deleteAll()

    @Query("DELETE FROM product_locations WHERE productId = :productId")
    suspend fun deleteByProduct(productId: String)

    @Query("DELETE FROM product_locations WHERE locationId = :locationId")
    suspend fun deleteByLocation(locationId: String)
}
