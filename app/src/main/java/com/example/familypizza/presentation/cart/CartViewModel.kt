package com.example.familypizza.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familypizza.domain.model.CartItem
import com.example.familypizza.domain.model.Order
import com.example.familypizza.domain.model.OrderStatus
import com.example.familypizza.domain.repository.CartRepository
import com.example.familypizza.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CartUiState(
    val items: List<CartItem> = emptyList(),
    val isLoading: Boolean = false,
    val orderPlaced: Boolean = false,
    val error: String = ""
) {
    val total: Double get() = items.sumOf { it.subtotal }
    val count: Int get() = items.sumOf { it.quantity }
}

class CartViewModel(
    private val cartRepo: CartRepository,
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
    fun remove(productId: Int) = viewModelScope.launch { cartRepo.removeItem(productId) }
    fun increase(productId: Int) = viewModelScope.launch { cartRepo.increaseQuantity(productId) }
    fun decrease(productId: Int) = viewModelScope.launch { cartRepo.decreaseQuantity(productId) }

    fun placeOrder(userId: Int) = viewModelScope.launch {
        val items = _ui.value.items
        if (userId <= 0) {
            _ui.update { it.copy(error = "Inicia sesion antes de hacer un pedido.") }
            return@launch
        }
        if (items.isEmpty()) {
            _ui.update { it.copy(error = "El carrito esta vacio.") }
            return@launch
        }

        val order = Order(
            userId = userId,
            items = items,
            total = _ui.value.total,
            status = OrderStatus.PENDIENTE,
            createdAt = System.currentTimeMillis()
        )

        _ui.update { it.copy(isLoading = true, error = "") }
        runCatching {
            orderRepo.placeOrder(order)
            cartRepo.clearCart()
        }.onSuccess {
            _ui.update { it.copy(isLoading = false, orderPlaced = true, error = "") }
        }.onFailure { error ->
            _ui.update {
                it.copy(
                    isLoading = false,
                    error = error.message ?: "No se pudo registrar el pedido."
                )
            }
        }
    }

    fun resetOrderPlaced() = _ui.update { it.copy(orderPlaced = false) }
    fun clearError() = _ui.update { it.copy(error = "") }
}
