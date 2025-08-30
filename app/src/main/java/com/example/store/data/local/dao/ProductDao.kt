package com.example.store.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.store.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun update(product: ProductEntity)

    @Delete
    suspend fun delete(product: ProductEntity)

    // Obtener un producto por su ID
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: String?): Flow<ProductEntity?>

    // Obtener todos los productos ordenados por nombre
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    // Buscar productos por nombre (filtro con LIKE)
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchProductsByName(query: String): Flow<List<ProductEntity>>

    // Obtener productos que necesitan ser sincronizados con Firebase (products que tienen needsSync = 1)
    @Query("SELECT * FROM products WHERE needsSync = 1")
    suspend fun getUnsyncedProducts(): List<ProductEntity>

    // Eliminar todos los productos (Ejemplo de operación masiva)
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    // Actualizar la sincronización de productos
    @Query("UPDATE products SET needsSync = 0 WHERE id IN (:productIds)")
    suspend fun updateSyncStatus(productIds: List<String>)
}
