package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.User

interface AuthRepository {
    /**
     * Autentica um utilizador com email e password.
     *
     * @param email Email do utilizador
     * @param password Password do utilizador
     * @return [Result] com [User] em caso de sucesso, ou erro específico
     * @throws IllegalArgumentException se email ou password forem inválidos
     */
    suspend fun login(email: String, password: String): Result<User>

    // Retorna Result<String> onde a String é o UID do utilizador
    suspend fun signUp(email: String, password: String, nome: String): Result<String>

    /**
     * Termina a sessão do utilizador atual.
     *
     * @return [Result] com [Unit] em caso de sucesso
     */
    suspend fun logout(): Result<Unit>

    /**
     * Obtém o utilizador atualmente autenticado.
     *
     * @return [User] se existe sessão ativa, ou null caso contrário
     */
    suspend fun getCurrentUser(): User?
    suspend fun getUserRole(uid: String): String?
}