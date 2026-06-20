package com.example.familypizza.data.repository

import com.example.familypizza.data.local.UserDao
import com.example.familypizza.data.local.UserEntity
import com.example.familypizza.data.firebase.FirebaseDataSource
import com.example.familypizza.data.remote.FamilyPizzaApi
import com.example.familypizza.data.remote.dto.UserDto
import com.example.familypizza.data.remote.safeApiCall
import com.example.familypizza.domain.model.User
import com.example.familypizza.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val dao: UserDao,
    private val api: FamilyPizzaApi,
    private val firebase: FirebaseDataSource
) : UserRepository {

    override fun observeUsers(): Flow<List<User>> =
        dao.observeAll().map { users -> users.map { it.toDomain() } }

    override suspend fun login(login: String): User? =
        dao.findByLogin(login)?.toDomain()

    override suspend fun loginWithEmail(email: String, password: String): User? {
        val user = runCatching { firebase.loginEmail(email, password) }.getOrNull()
        if (user != null) {
            saveLocalUser(user)
            return user
        }
        return dao.findByLogin(email)?.toDomain()?.takeIf { it.password == password }
    }

    override suspend fun loginWithGoogle(idToken: String): User? {
        val user = runCatching { firebase.loginGoogle(idToken) }.getOrNull() ?: return null
        saveLocalUser(user)
        return user
    }

    override suspend fun ensureAdminSession(email: String, password: String): Boolean {
        val signedIn = runCatching { firebase.loginEmail(email, password) }.getOrNull()
        if (signedIn != null) {
            saveLocalUser(signedIn)
            return true
        }

        val admin = User(
            name = "Administrador",
            email = email,
            phone = "",
            password = password,
            address = ""
        )
        val created = runCatching { firebase.registerEmail(admin) }.getOrNull()
        if (created != null) {
            saveLocalUser(created)
            return true
        }

        return false
    }

    override suspend fun emailExists(email: String): Boolean =
        dao.countByEmail(email) > 0

    override suspend fun register(user: User): User {
        val savedUser = runCatching { firebase.registerEmail(user) }.getOrElse {
            val newId = dao.insert(user.toEntity()).toInt()
            user.copy(id = newId)
        }
        saveLocalUser(savedUser)
        safeApiCall { api.createUser(savedUser.toDto()) }
        return savedUser
    }

    override suspend fun updateUser(user: User) {
        dao.updateUser(user.id, user.name, user.email, user.phone, user.password, user.address)
        runCatching { firebase.saveUser(user) }
        safeApiCall { api.updateUser(user.id, user.toDto()) }
    }

    override suspend fun updateProfile(id: Int, phone: String, address: String) {
        dao.updateProfile(id, phone, address)
        dao.findById(id)?.let {
            val user = it.toDomain()
            runCatching { firebase.saveUser(user) }
            safeApiCall { api.updateUser(id, user.toDto()) }
        }
    }

    override suspend fun deleteUser(id: Int) {
        dao.deleteById(id)
    }

    private fun UserEntity.toDomain() = User(id, name, email, phone, password, address)
    private fun User.toEntity() = UserEntity(id, name, email, phone, password, address)
    private fun User.toDto() = UserDto(id, name, email, phone, password, address)

    private suspend fun saveLocalUser(user: User) {
        val existing = dao.findById(user.id) ?: dao.findByLogin(user.email)
        if (existing == null) {
            runCatching { dao.insert(user.toEntity()) }
        } else {
            dao.updateUser(user.id, user.name, user.email, user.phone, user.password, user.address)
        }
    }
}
