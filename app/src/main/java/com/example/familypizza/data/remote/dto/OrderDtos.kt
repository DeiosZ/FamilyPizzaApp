package com.example.familypizza.data.remote.dto

data class OrderItemDto(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int
)

data class OrderRequestDto(
    val localId: Int,
    val userId: Int,
    val items: List<OrderItemDto>,
    val total: Double,
    val status: String,
    val createdAt: Long
)

data class OrderResponseDto(
    val id: Int,
    val status: String
)
