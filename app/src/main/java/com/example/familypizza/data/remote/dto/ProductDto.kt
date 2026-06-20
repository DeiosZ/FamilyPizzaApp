package com.example.familypizza.data.remote.dto

data class ProductDto(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String,
    val tag: String,
    val imageKey: String
)
