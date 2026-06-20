package com.example.familypizza.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.familypizza.domain.repository.CartRepository
import com.example.familypizza.domain.repository.OrderRepository
import com.example.familypizza.domain.repository.ProductRepository
import com.example.familypizza.domain.repository.UserRepository
import com.example.familypizza.presentation.admin.AdminOrdersViewModel
import com.example.familypizza.presentation.admin.AdminProductsViewModel
import com.example.familypizza.presentation.cart.CartViewModel
import com.example.familypizza.presentation.orders.OrdersViewModel
import com.example.familypizza.presentation.products.ProductsViewModel

class AppViewModelFactory(
    private val cartRepo : CartRepository,
    private val orderRepo: OrderRepository,
    private val productRepo: ProductRepository? = null,
    private val userId   : Int = 0
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(CartViewModel::class.java)   -> CartViewModel(cartRepo, orderRepo) as T
        modelClass.isAssignableFrom(OrdersViewModel::class.java) -> OrdersViewModel(orderRepo, userId) as T
        modelClass.isAssignableFrom(ProductsViewModel::class.java) -> ProductsViewModel(requireNotNull(productRepo)) as T
        modelClass.isAssignableFrom(AdminProductsViewModel::class.java) -> AdminProductsViewModel(requireNotNull(productRepo)) as T
        modelClass.isAssignableFrom(AdminOrdersViewModel::class.java) -> AdminOrdersViewModel(orderRepo) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
