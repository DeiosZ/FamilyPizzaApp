package com.example.familypizza.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM usuarios ORDER BY name")
    fun observeAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM usuarios WHERE email = :login OR name = :login LIMIT 1")
    suspend fun findByLogin(login: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun findById(id: Int): UserEntity?

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    suspend fun countByEmail(email: String): Int

    @Query("UPDATE usuarios SET phone = :phone, address = :address WHERE id = :id")
    suspend fun updateProfile(id: Int, phone: String, address: String)

    @Query("UPDATE usuarios SET name = :name, email = :email, phone = :phone, password = :password, address = :address WHERE id = :id")
    suspend fun updateUser(id: Int, name: String, email: String, phone: String, password: String, address: String)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun deleteById(id: Int)
}
