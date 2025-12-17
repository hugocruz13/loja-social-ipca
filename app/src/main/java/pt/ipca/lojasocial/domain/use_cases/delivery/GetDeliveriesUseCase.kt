package pt.ipca.lojasocial.domain.use_cases.delivery

import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela listagem global de todas as entregas e agendamentos.
 *
 * Fornece a visão macro da operação logística, permitindo ao Staff visualizar
 * tanto as entregas passadas como os agendamentos futuros.
 *
 * **Contexto:**
 * Geralmente utilizado no painel principal (Dashboard) ou na vista de calendário
 * para gestão diária das saídas de bens.
 */
class GetDeliveriesUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {
    /**
     * Executa a obtenção da lista completa de entregas.
     *
     * @return Lista de [Delivery] (sem filtros aplicados, contendo agendadas, realizadas, canceladas, etc.).
     */
    suspend operator fun invoke(): List<Delivery> {
        return repository.getDeliveries()
    }
}