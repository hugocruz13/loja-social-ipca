package pt.ipca.lojasocial.domain.use_cases.delivery

import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.models.UserRole
import javax.inject.Inject

class GetPendingDeliveriesCountUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(role: UserRole, userId: String): Int {
        // Se for STAFF passa para null (queremos contagem global)
        // Se for BENEFICIARY passa o ID do user (queremos contagem pessoal)
        val idToSearch = if (role == UserRole.STAFF) null else userId
        return repository.getPendingDeliveriesCount(idToSearch)
    }
}