package com.example.store.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.store.data.local.entity.OrderEntity
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.domain.model.OrderWithOrderItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderById(orderId: String): Flow<OrderEntity?>

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY orderDate DESC")
    fun getOrdersByCustomerId(customerId: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY orderDate DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE orderDate BETWEEN :startDate AND :endDate ORDER BY orderDate DESC")
    fun getOrdersByDateRange(startDate: Long, endDate: Long): Flow<List<OrderEntity>>

    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderWithItems(order: OrderEntity, items: List<OrderItemEntity>)

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?>
}
