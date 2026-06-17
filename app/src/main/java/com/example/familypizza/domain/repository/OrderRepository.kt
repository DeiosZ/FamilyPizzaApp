package com.example.familypizza.domain.repository

import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun observeOrders(userId: Int): Flow<List<Order>>
    suspend fun placeOrder(order: Order): Order
    suspend fun updateStatus(orderId: Int, status: OrderStatus)
}