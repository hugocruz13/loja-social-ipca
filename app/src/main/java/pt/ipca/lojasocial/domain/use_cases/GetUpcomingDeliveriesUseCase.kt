package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por filtrar as próximas entregas agendadas (Futuro próximo).
 *
 * Este componente é essencial para o **Planeamento Operacional** diário/semanal.
 * Permite ao Staff antecipar a preparação de cabazes, visualizando apenas o que
 * está agendado para os próximos X dias, ignorando o histórico antigo ou agendamentos muito distantes.
 *
 * **Requisitos Funcionais:**
 * - **RF11**: Agendamento de entregas (Visualização de agenda de curto prazo).
 */
class GetUpcomingDeliveriesUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {

    /**
     * Obtém as entregas agendadas desde o momento atual até ao limite de dias indicado.
     *
     * @param limitDays O horizonte temporal em dias (ex: 7 para ver a "Próxima Semana").
     * @return Lista de [Delivery] filtrada pela data de agendamento.
     */
    suspend operator fun invoke(limitDays: Int): List<Delivery> {
        val currentTime = System.currentTimeMillis()

        // Lógica de Negócio: Definição da janela temporal (Agora -> Agora + X Dias)
        // Conversão: Dias * 24h * 60m * 60s * 1000ms
        val limitTime = currentTime + (limitDays * 24 * 60 * 60 * 1000L)

        return repository.getUpcomingDeliveries(limitTime)
    }
}