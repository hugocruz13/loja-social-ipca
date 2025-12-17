package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por registar uma nova entrega ou agendamento no sistema.
 *
 * Este caso de uso centraliza a lógica de criação, garantindo que todas as entregas
 * (sejam imediatas ou agendadas) cumprem os requisitos mínimos de integridade antes
 * de serem persistidas.
 *
 * **Requisitos Funcionais:**
 * - **RF10**: Registo de entregas de bens associadas a beneficiários.
 * - **RF11**: Agendamento de entregas.
 */
class AddDeliveryUseCase @Inject constructor(
    private val repository: DeliveryRepository
) {

    /**
     * Executa o registo da entrega.
     *
     * @param delivery O objeto [Delivery] contendo os dados da entrega a criar.
     * @throws IllegalArgumentException Se a entrega não estiver associada a um beneficiário (violação do RF10).
     */
    suspend operator fun invoke(delivery: Delivery) {
        // Validação: Não permitir entrega sem beneficiário
        if (delivery.beneficiaryId.isBlank()) {
            throw IllegalArgumentException("A entrega tem de estar associada a um beneficiário.")
        }
        repository.addDelivery(delivery)
    }
}