package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

class UpdateDeliveryItemsUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: String, items: Map<String, Int>) {
        if (items.values.any { it <= 0 }) {
            throw IllegalArgumentException("A quantidade dos itens deve ser positiva.")
        }
        repository.updateDeliveryItems(id, items)
    }
}