package com.example.familypizza.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.domain.repository.CartRepository
import com.example.familypizza.domain.repository.OrderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val orderPlaced: Boolean  = false,
    val error: String         = ""
) {
    val total: Double get() = items.sumOf { it.subtotal }
    val count: Int    get() = items.sumOf { it.quantity }
}

class CartViewModel(
    private val cartRepo : CartRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CartUiState())
    val ui: StateFlow<CartUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepo.observeCart().collect { items ->
                _ui.update { it.copy(items = items) }
            }
        }
    }

    fun addItem(item: CartItem) = viewModelScope.launch { cartRepo.addItem(item) }
    fun remove(productId: Int)  = viewModelScope.launch { cartRepo.removeItem(productId) }
    fun increase(productId: Int)= viewModelScope.launch { cartRepo.increaseQuantity(productId) }
    fun decrease(productId: Int)= viewModelScope.launch { cartRepo.decreaseQuantity(productId) }

    fun placeOrder(userId: Int) = viewModelScope.launch {
        val items = _ui.value.items
        if (items.isEmpty()) { _ui.update { it.copy(error = "El carrito está vacío.") }; return@launch }
        val order = Order(
            userId    = userId,
            items     = items,
            total     = _ui.value.total,
            status    = OrderStatus.PENDIENTE,
            createdAt = System.currentTimeMillis()
        )
        orderRepo.placeOrder(order)
        cartRepo.clearCart()
        _ui.update { it.copy(orderPlaced = true, error = "") }
    }

    fun resetOrderPlaced() = _ui.update { it.copy(orderPlaced = false) }
    fun clearError()       = _ui.update { it.copy(error = "") }
}