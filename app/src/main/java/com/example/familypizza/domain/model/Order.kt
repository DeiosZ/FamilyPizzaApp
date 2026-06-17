package com.example.familypizza.domain.model

enum class OrderStatus(val label: String) {
    PENDIENTE("Pendiente"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado")
}

data class Order(
    val id: Int = 0,
    val userId: Int,
    val items: List<CartItem>,
    val total: Double,
    val status: OrderStatus,
    val createdAt: Long
)