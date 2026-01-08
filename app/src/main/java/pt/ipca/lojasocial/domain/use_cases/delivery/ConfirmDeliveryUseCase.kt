package pt.ipca.lojasocial.domain.use_cases.delivery

import kotlinx.coroutines.flow.firstOrNull
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject
import kotlin.math.min

/**
 * Caso de Uso para confirmar uma entrega como realizada.
 *
 * Responsabilidades:
 * 1. Alterar o estado da entrega para [DeliveryStatus.DELIVERED].
 * 2. Abater os produtos entregues do stock existente, seguindo a lógica FEFO (First Expired, First Out).
 */
class ConfirmDeliveryUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository,
    private val stockRepository: StockRepository
) {
    suspend operator fun invoke(deliveryId: String) {

        val delivery = deliveryRepository.getDeliveryById(deliveryId).firstOrNull()
            ?: throw Exception("Entrega não encontrada")

        if (delivery.status == DeliveryStatus.DELIVERED) return

        // 2. Obter todo o stock disponível
        val allStockItems = stockRepository.getStockItems().filter { it.quantity > 0 }

        // 3. Processar cada produto da entrega
        val stockUpdates = mutableMapOf<String, Int>()

        delivery.items.forEach { (productId, quantityNeeded) ->
            var remainingQtyToDeduct = quantityNeeded

            val relevantStock = allStockItems
                .filter { it.productId == productId }
                .sortedBy { it.expiryDate }

            for (stockItem in relevantStock) {
                if (remainingQtyToDeduct <= 0) break

                val quantityToTake = min(remainingQtyToDeduct, stockItem.quantity)

                val newQuantity = stockItem.quantity - quantityToTake

                stockUpdates[stockItem.id] = newQuantity

                remainingQtyToDeduct -= quantityToTake
            }
        }

        stockUpdates.forEach { (stockId, newQuantity) ->
            stockRepository.updateStockQuantity(stockId, newQuantity)
        }

        deliveryRepository.updateDeliveryStatus(deliveryId, DeliveryStatus.DELIVERED)
    }
}