package pt.ipca.lojasocial.domain.use_cases;

import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Inject;

class LogoutUserUseCase @Inject constructor(
    private val repository: AuthRepository

){
    suspend operator fun invoke(): Result<Unit> {
        return repository.logout()
    }
}
