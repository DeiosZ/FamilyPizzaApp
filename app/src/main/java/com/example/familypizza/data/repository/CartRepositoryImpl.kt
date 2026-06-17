package com.example.familypizza.data.repository

import com.example.familypizza.data.local.CartDao
import com.example.familypizza.data.local.CartItemEntity
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl(private val dao: CartDao) : CartRepository {

    override fun observeCart(): Flow<List<CartItem>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addItem(item: CartItem) {
        val existing = dao.findById(item.productId)
        if (existing == null) {
            dao.upsert(item.toEntity())
        } else {
            dao.updateQuantity(item.productId, existing.quantity + 1)
        }
    }

    override suspend fun removeItem(productId: Int) {
        dao.deleteById(productId)
    }

    override suspend fun increaseQuantity(productId: Int) {
        val item = dao.findById(productId) ?: return
        dao.updateQuantity(productId, item.quantity + 1)
    }

    override suspend fun decreaseQuantity(productId: Int) {
        val item = dao.findById(productId) ?: return
        if (item.quantity <= 1) dao.deleteById(productId)
        else dao.updateQuantity(productId, item.quantity - 1)
    }

    override suspend fun clearCart() = dao.clearAll()

    private fun CartItemEntity.toDomain() = CartItem(productId, name, price, imageRes, quantity)
    private fun CartItem.toEntity()       = CartItemEntity(productId, name, price, imageRes, quantity)
}