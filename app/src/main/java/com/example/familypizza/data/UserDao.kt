package com.example.familypizza.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM usuarios WHERE email = :login OR name = :login LIMIT 1")
    suspend fun findByLogin(login: String): UserEntity?

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    suspend fun countByEmail(email: String): Int

    @Query("UPDATE usuarios SET phone = :phone, address = :address WHERE id = :id")
    suspend fun updateProfile(id: Int, phone: String, address: String)
}
