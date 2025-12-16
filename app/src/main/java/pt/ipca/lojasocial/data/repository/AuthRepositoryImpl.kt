package pt.ipca.lojasocial.data.repository

import com.google.firebase.auth.FirebaseAuthException
import pt.ipca.lojasocial.data.mapper.UserMapper
import pt.ipca.lojasocial.data.remote.FirebaseAuthDataSource
import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Inject
import pt.ipca.lojasocial.domain.models.User

//-----------------------------------
// Implementação das funções definidas na interface AuthRepository deve usar os atributos
// e devolver o resultado correto como definido na interface
//-----------------------------------
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseAuthDataSource
): AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val userDto = remoteDataSource.login(email, password)
            val user = UserMapper.toDomain(userDto)
            Result.success(user)
        } catch (e: FirebaseAuthException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> "Email inválido"
                "ERROR_WRONG_PASSWORD" -> "Password incorreta"
                "ERROR_USER_NOT_FOUND" -> "Utilizador não encontrado"
                "ERROR_USER_DISABLED" -> "Conta desativada"
                "ERROR_TOO_MANY_REQUESTS" -> "Demasiadas tentativas. Tente mais tarde"
                else -> "Erro de autenticação: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
            } catch (e: Exception) {
            Result.failure(Exception("Erro desconhecido: ${e.message}"))
        }
    }

    override suspend fun getCurrentUser(): User?{
        return try {
            val userDto = remoteDataSource.getCurrentUser()
            val user = UserMapper.toDomain(userDto!!)
            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            remoteDataSource.logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}