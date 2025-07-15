package com.example.Store.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.example.Store.data.local.entity.OrderEntity
import com.example.Store.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

// POKO for representing an Order with its associated OrderItems
data class OrderWithOrderItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId"
    )
    val items: List<OrderItemEntity>
)

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

    @Transaction // Ensures the read operation is atomic
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    fun getOrderWithOrderItems(orderId: String): Flow<OrderWithOrderItems?>

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrdersWithOrderItems(): Flow<List<OrderWithOrderItems>>

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteAllOrderItemsForOrder(orderId: String)

    @Query("DELETE FROM order_items")
    suspend fun deleteAllOrderItems()
}
