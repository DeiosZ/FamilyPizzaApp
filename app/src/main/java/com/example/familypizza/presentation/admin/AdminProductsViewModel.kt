package com.example.familypizza.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familypizza.domain.model.Product
import com.example.familypizza.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminProductsUiState(
    val products: List<Product> = emptyList(),
    val message: String = ""
)

class AdminProductsViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(AdminProductsUiState())
    val ui: StateFlow<AdminProductsUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeProducts().collect { products ->
                _ui.update { it.copy(products = products) }
            }
        }
        viewModelScope.launch {
            runCatching { repository.refreshProducts() }
                .onFailure { _ui.update { state -> state.copy(message = "No se pudo sincronizar productos.") } }
        }
    }

    fun saveProduct(product: Product) = viewModelScope.launch {
        runCatching { repository.upsertProduct(product) }
            .onSuccess { _ui.update { it.copy(message = "Producto guardado en Firebase.") } }
            .onFailure { e -> _ui.update { state -> state.copy(message = "No se pudo guardar en Firebase: ${e.message ?: "error desconocido"}") } }
    }

    fun deleteProduct(id: Int) = viewModelScope.launch {
        runCatching { repository.deleteProduct(id) }
            .onSuccess { _ui.update { it.copy(message = "Producto eliminado.") } }
            .onFailure { e -> _ui.update { state -> state.copy(message = "No se pudo eliminar en Firebase: ${e.message ?: "error desconocido"}") } }
    }

    fun clearMessage() = _ui.update { it.copy(message = "") }
}
