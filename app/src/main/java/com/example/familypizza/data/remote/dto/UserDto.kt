package com.example.familypizza.data.remote.dto

data class UserDto(
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val address: String = ""
)
