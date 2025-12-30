package pt.ipca.lojasocial.domain.use_cases.delivery

import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela transição de estados de uma entrega (Ciclo de Vida).
 *
 * Gere o fluxo operacional das entregas, desde o agendamento ('PENDING') até
 * à confirmação de receção ('DELIVERED') ou cancelamento ('CANCELLED').
 *
 * **Nota de Arquitetura (CRÍTICA):**
 * Este componente é o "gatilho" para a atualização de inventário (**RF07**).
 * Quando o estado transita para [DeliveryStatus.DELIVERED], este Use Case deve:
 * 1. Confirmar a transação.
 * 2. Invocar o [pt.ipca.lojasocial.domain.use_cases.stock.UpdateStockQuantityUseCase] para abater os bens entregues no stock físico.
 * (Funcionalidade pendente de implementação, ver TODO no código).
 */
class UpdateDeliveryStatusUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {

    /**
     * Executa a alteração do estado da entrega.
     *
     * @param id O identificador da entrega.
     * @param status O novo estado a aplicar.
     */
    suspend operator fun invoke(id: String, status: DeliveryStatus) {
        // TODO: Se status mudar para DELIVERED, chamar StockUseCase para abater stock
        // Esta lógica garante que o stock físico reflete a realidade das saídas.
        repository.updateDeliveryStatus(id, status)
    }
}