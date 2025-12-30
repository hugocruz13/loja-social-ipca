package pt.ipca.lojasocial.domain.use_cases.auth

import pt.ipca.lojasocial.domain.models.User
import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Inject

/**
* Use Case para autenticação de utilizadores.
*
* **Regras de negócio aplicadas:**
* - Email deve ser válido (conter @)
* - Password deve ter no mínimo 8 caracteres
* - Não permite emails ou passwords vazias
*
* @property repository Repositório de autenticação
*/
class LoginUserUseCase @Inject constructor(
    private val repository: AuthRepository

){
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if(email.isBlank() || password.isBlank())
            return Result.failure(Exception("Email e ou Password não pode estar vazio"))
        if(!email.contains("@"))
            return Result.failure(Exception("Formato Email inválido"))
        if(password.length < 8)
            return Result.failure(Exception("Password deve ter pelo menos 8 caracteres"))

        return repository.login(email, password)
    }
}