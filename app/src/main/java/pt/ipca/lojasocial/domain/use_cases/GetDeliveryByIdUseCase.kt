package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

class GetDeliveryByIdUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: String): Delivery? {
        return repository.getDeliveryById(id)
    }
}