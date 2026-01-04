package pt.ipca.lojasocial.domain.use_cases.delivery

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
        // 1. Obter a entrega
        val delivery = deliveryRepository.getDeliveryById(deliveryId) 
            ?: throw Exception("Entrega não encontrada")

        // Se já estiver entregue, não fazemos nada para evitar duplo desconto de stock
        if (delivery.status == DeliveryStatus.DELIVERED) return

        // 2. Obter todo o stock disponível
        // Nota: Idealmente o repositório teria um método getStockByProduct, mas filtramos aqui por agora.
        val allStockItems = stockRepository.getStockItems().filter { it.quantity > 0 }

        // 3. Processar cada produto da entrega
        val stockUpdates = mutableMapOf<String, Int>() // Map<StockItemId, NewQuantity>

        delivery.items.forEach { (productId, quantityNeeded) ->
            var remainingQtyToDeduct = quantityNeeded

            // Encontrar lotes deste produto, ordenados por validade (FEFO)
            val relevantStock = allStockItems
                .filter { it.productId == productId }
                .sortedBy { it.expiryDate }

            for (stockItem in relevantStock) {
                if (remainingQtyToDeduct <= 0) break

                // Quanto podemos tirar deste lote?
                val quantityToTake = min(remainingQtyToDeduct, stockItem.quantity)
                
                // Calcular nova quantidade
                val newQuantity = stockItem.quantity - quantityToTake
                
                // Guardar para atualização
                stockUpdates[stockItem.id] = newQuantity
                
                remainingQtyToDeduct -= quantityToTake
            }

            // Nota: Se remainingQtyToDeduct > 0 aqui, significa que não havia stock suficiente.
            // O sistema permite 'stock negativo' ou inconsistente neste caso, ou podíamos lançar erro.
            // Assumimos que prossegue com o que há.
        }

        // 4. Aplicar as atualizações de stock na base de dados
        stockUpdates.forEach { (stockId, newQuantity) ->
            stockRepository.updateStockQuantity(stockId, newQuantity)
        }

        // 5. Atualizar o estado da entrega
        deliveryRepository.updateDeliveryStatus(deliveryId, DeliveryStatus.DELIVERED)
    }
}
