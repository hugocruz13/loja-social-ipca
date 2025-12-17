package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

class UpdateDeliveryStatusUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    suspend operator fun invoke(id: String, status: DeliveryStatus) {
        // TODO: Se status mudar para DELIVERED, chamar StockUseCase para abater stock
        repository.updateDeliveryStatus(id, status)
    }
}