package com.example.familypizza.domain.model

import androidx.annotation.DrawableRes

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val priceValue: Double,
    val description: String,
    val tag: String,
    @DrawableRes val imageRes: Int
)
