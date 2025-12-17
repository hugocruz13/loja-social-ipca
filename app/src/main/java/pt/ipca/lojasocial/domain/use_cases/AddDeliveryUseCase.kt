package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

class AddDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(delivery: Delivery) {
        // Validação: Não permitir entrega sem beneficiário
        if (delivery.beneficiaryId.isBlank()) {
            throw IllegalArgumentException("A entrega tem de estar associada a um beneficiário.")
        }
        repository.addDelivery(delivery)
    }
}