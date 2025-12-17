package pt.ipca.lojasocial.domain.use_cases.auth

import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case para fazer logout de um utilizador.
 *
 * @property repository Repositório de autenticação
 */
class LogoutUserUseCase @Inject constructor(
    private val repository: AuthRepository

){
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}
