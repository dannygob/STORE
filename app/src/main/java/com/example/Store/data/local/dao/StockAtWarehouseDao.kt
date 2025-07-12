package com.example.Store.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.Store.data.local.entity.StockAtWarehouseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StockAtWarehouseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStock(stock: StockAtWarehouseEntity)

    @Update
    suspend fun updateStock(stock: StockAtWarehouseEntity)

    @Delete
    suspend fun deleteStock(stock: StockAtWarehouseEntity)

    @Query("SELECT * FROM stock_at_warehouse WHERE productId = :productId AND warehouseId = :warehouseId")
    fun getStockForProductInWarehouse(productId: String, warehouseId: String): Flow<StockAtWarehouseEntity?>

    @Query("SELECT * FROM stock_at_warehouse WHERE productId = :productId")
    fun getAllStockForProduct(productId: String): Flow<List<StockAtWarehouseEntity>>

    @Query("SELECT * FROM stock_at_warehouse WHERE warehouseId = :warehouseId")
    fun getAllStockInWarehouse(warehouseId: String): Flow<List<StockAtWarehouseEntity>>

    // Sums the quantity of a specific product across all warehouses.
    // Returns Flow<Int?> because the product might not have any stock entries, resulting in null sum.
    @Query("SELECT SUM(quantity) FROM stock_at_warehouse WHERE productId = :productId")
    fun getTotalStockQuantityForProduct(productId: String): Flow<Int?>

    @Query("DELETE FROM stock_at_warehouse WHERE productId = :productId AND warehouseId = :warehouseId")
    suspend fun deleteStockForProductInWarehouse(productId: String, warehouseId: String)

    @Query("DELETE FROM stock_at_warehouse")
    suspend fun deleteAllStock()
}
