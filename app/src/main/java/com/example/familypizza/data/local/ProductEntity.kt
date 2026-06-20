package com.example.familypizza.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val priceLabel: String,
    val priceValue: Double,
    val description: String,
    val tag: String,
    val imageRes: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
