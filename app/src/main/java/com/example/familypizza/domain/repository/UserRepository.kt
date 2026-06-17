package com.example.familypizza.domain.repository

import com.example.familypizza.domain.model.User

interface UserRepository {
    suspend fun login(login: String): User?
    suspend fun emailExists(email: String): Boolean
    suspend fun register(user: User): User
    suspend fun updateProfile(id: Int, phone: String, address: String)
}