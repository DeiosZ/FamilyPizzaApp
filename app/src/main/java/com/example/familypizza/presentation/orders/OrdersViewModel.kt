package com.example.familypizza.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.domain.repository.OrderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val repo  : OrderRepository,
    private val userId: Int
) : ViewModel() {

    val orders: StateFlow<List<Order>> = repo
        .observeOrders(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cancel(orderId: Int) = viewModelScope.launch {
        repo.updateStatus(orderId, OrderStatus.CANCELADO)
    }
}