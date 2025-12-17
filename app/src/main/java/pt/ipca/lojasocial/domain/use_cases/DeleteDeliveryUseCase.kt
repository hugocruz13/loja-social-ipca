package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

class DeleteDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteDelivery(id)
    }
}