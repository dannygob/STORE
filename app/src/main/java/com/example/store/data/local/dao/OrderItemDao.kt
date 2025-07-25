package com.example.store.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.store.data.local.entity.OrderItemEntity
import com.example.store.data.local.entity.OrderWithOrderItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOrderItems(orderItems: List<OrderItemEntity>)

    @Update
    suspend fun updateOrderItem(orderItem: OrderItemEntity)

    @Delete
    suspend fun deleteOrderItem(orderItem: OrderItemEntity)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteAllOrderItemsForOrder(orderId: String)

    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?>

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>>
}
