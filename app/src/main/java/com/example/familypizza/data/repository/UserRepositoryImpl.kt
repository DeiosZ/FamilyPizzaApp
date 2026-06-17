package com.example.familypizza.data.repository

import com.example.familypizza.data.local.UserDao
import com.example.familypizza.data.local.UserEntity
import com.example.familypizza.domain.model.User
import com.example.familypizza.domain.repository.UserRepository

class UserRepositoryImpl(private val dao: UserDao) : UserRepository {

    override suspend fun login(login: String): User? =
        dao.findByLogin(login)?.toDomain()

    override suspend fun emailExists(email: String): Boolean =
        dao.countByEmail(email) > 0

    override suspend fun register(user: User): User {
        val entity = user.toEntity()
        val newId = dao.insert(entity).toInt()
        return user.copy(id = newId)
    }

    override suspend fun updateProfile(id: Int, phone: String, address: String) {
        dao.updateProfile(id, phone, address)
    }

    private fun UserEntity.toDomain() = User(id, name, email, phone, password, address)
    private fun User.toEntity() = UserEntity(id, name, email, phone, password, address)
}