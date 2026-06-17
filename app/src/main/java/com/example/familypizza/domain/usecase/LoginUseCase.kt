package com.example.familypizza.domain.usecase
import com.example.familypizza.domain.model.User
import com.example.familypizza.domain.repository.UserRepository

class LoginUseCase(
    private val repo: UserRepository = TODO()
) {
    suspend operator fun invoke(email: String): User? {
        return repo.login(email)
    }
}