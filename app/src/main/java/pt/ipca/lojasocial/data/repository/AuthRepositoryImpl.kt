package pt.ipca.lojasocial.data.repository

import com.google.firebase.auth.FirebaseAuthException
import pt.ipca.lojasocial.data.mapper.UserMapper
import pt.ipca.lojasocial.data.remote.FirebaseAuthDataSource
import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Inject
import pt.ipca.lojasocial.domain.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//-----------------------------------
// Implementação das funções definidas na interface AuthRepository deve usar os atributos
// e devolver o resultado correto como definido na interface
//-----------------------------------
class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: FirebaseAuthDataSource,
    private val firestore: FirebaseFirestore
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

    override suspend fun signUp(email: String, password: String, nome: String): Result<String> {
        return try {
            // Chama a DataSource que criámos no passo 1
            val uid = remoteDataSource.signUp(email, password, nome)
            Result.success(uid)
        } catch (e: FirebaseAuthException) {
            val errorMessage = when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Este email já está registado."
                "ERROR_INVALID_EMAIL" -> "O email é inválido."
                "ERROR_WEAK_PASSWORD" -> "A password é muito fraca (mínimo 6 caracteres)."
                else -> "Erro de registo: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Result.failure(Exception("Erro desconhecido ao registar: ${e.message}"))
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

    override suspend fun getUserRole(uid: String): String? {
        return try {
            // 1. Verifica na coleção de COLABORADORES
            val docColab = firestore.collection("colaboradores").document(uid).get().await()
            if (docColab.exists()) {
                return "colaborador"
            }

            // 2. Verifica na coleção de BENEFICIÁRIOS
            val docBen = firestore.collection("beneficiarios").document(uid).get().await()
            if (docBen.exists()) {
                return "beneficiario"
            }

            // 3. Não encontrou em lado nenhum
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}