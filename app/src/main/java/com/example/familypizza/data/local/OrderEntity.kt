package com.example.familypizza.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val items: String,          // JSON serializado
    val total: Double,
    val status: String,         // "PENDIENTE" | "EN_CAMINO" | "ENTREGADO" | "CANCELADO"
    val createdAt: Long = System.currentTimeMillis()
)