package com.example.familypizza.domain.repository

import com.example.familypizza.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun observeProducts(): Flow<List<Product>>
    suspend fun findById(id: Int): Product?
    suspend fun refreshProducts(): Result<Unit>
    suspend fun upsertProduct(product: Product)
    suspend fun deleteProduct(id: Int)
}
