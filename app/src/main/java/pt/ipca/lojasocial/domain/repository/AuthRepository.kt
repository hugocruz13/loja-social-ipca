package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): User?
}