package com.example.familypizza.domain.repository

import com.example.familypizza.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observeCart(): Flow<List<CartItem>>
    suspend fun addItem(item: CartItem)
    suspend fun removeItem(productId: Int)
    suspend fun increaseQuantity(productId: Int)
    suspend fun decreaseQuantity(productId: Int)
    suspend fun clearCart()
}