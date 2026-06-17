package com.example.familypizza.domain.model

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val imageRes: Int,
    val quantity: Int
) {
    val subtotal: Double get() = price * quantity
}