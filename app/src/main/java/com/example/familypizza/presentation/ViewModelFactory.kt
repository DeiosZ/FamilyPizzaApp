package com.example.familypizza.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.familypizza.domain.repository.CartRepository
import com.example.familypizza.domain.repository.OrderRepository
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.cart.CartViewModel
import com.example.familypizza.presentation.orders.OrdersViewModel

class AppViewModelFactory(
    private val cartRepo : CartRepository,
    private val orderRepo: OrderRepository,
    private val userId   : Int = 0
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(CartViewModel::class.java)   -> CartViewModel(cartRepo, orderRepo) as T
        modelClass.isAssignableFrom(OrdersViewModel::class.java) -> OrdersViewModel(orderRepo, userId) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}