package com.example.familypizza.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.domain.repository.OrderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminOrdersViewModel(
    private val repository: OrderRepository
) : ViewModel() {

    val orders: StateFlow<List<Order>> = repository
        .observeAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            runCatching { repository.refreshAllOrders() }
        }
    }

    fun updateStatus(orderId: Int, status: OrderStatus) = viewModelScope.launch {
        runCatching { repository.updateStatus(orderId, status) }
    }
}
