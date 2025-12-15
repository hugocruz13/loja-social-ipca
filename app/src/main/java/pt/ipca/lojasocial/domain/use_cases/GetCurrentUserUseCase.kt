package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.User
import pt.ipca.lojasocial.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}