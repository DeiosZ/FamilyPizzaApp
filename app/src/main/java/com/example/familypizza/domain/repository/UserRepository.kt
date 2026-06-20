package com.example.familypizza.domain.repository

import com.example.familypizza.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUsers(): Flow<List<User>>
    suspend fun login(login: String): User?
    suspend fun loginWithEmail(email: String, password: String): User?
    suspend fun loginWithGoogle(idToken: String): User?
    suspend fun ensureAdminSession(email: String, password: String): Boolean
    suspend fun emailExists(email: String): Boolean
    suspend fun register(user: User): User
    suspend fun updateUser(user: User)
    suspend fun updateProfile(id: Int, phone: String, address: String)
    suspend fun deleteUser(id: Int)
}
