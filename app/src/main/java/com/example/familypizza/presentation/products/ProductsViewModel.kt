package com.example.familypizza.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familypizza.domain.model.Product
import com.example.familypizza.domain.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductsUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = true,
    val error: String = ""
)

class ProductsViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProductsUiState())
    val ui: StateFlow<ProductsUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeProducts().collect { products ->
                _ui.update { it.copy(products = products, isLoading = false) }
            }
        }
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _ui.update { it.copy(isLoading = true, error = "") }
        val result = runCatching { repository.refreshProducts() }
            .getOrElse { Result.failure(it) }
        _ui.update {
            it.copy(
                isLoading = false,
                error = result.exceptionOrNull()?.message.orEmpty()
            )
        }
    }
}
