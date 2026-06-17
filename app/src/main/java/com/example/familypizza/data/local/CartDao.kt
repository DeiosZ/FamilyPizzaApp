package com.example.familypizza.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun observeAll(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun findById(productId: Int): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CartItemEntity)

    @Query("UPDATE cart_items SET quantity = :qty WHERE productId = :productId")
    suspend fun updateQuantity(productId: Int, qty: Int)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun deleteById(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()
}