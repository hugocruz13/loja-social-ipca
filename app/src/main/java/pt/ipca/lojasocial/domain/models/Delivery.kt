package pt.ipca.lojasocial.domain.models

import pt.ipca.lojasocial.domain.models.DeliveryStatus.CANCELLED
import pt.ipca.lojasocial.domain.models.DeliveryStatus.DELIVERED
import pt.ipca.lojasocial.domain.models.DeliveryStatus.REJECTED
import pt.ipca.lojasocial.domain.models.DeliveryStatus.SCHEDULED
import pt.ipca.lojasocial.domain.models.DeliveryStatus.UNDER_ANALYSIS


/**
 * Define o estado logístico e administrativo de uma entrega.
 *
 * - [SCHEDULED]: Entrega agendada para o futuro, mas ainda não realizada.
 * - [DELIVERED]: Entrega concluída com sucesso (bens entregues ao beneficiário).
 * - [CANCELLED]: Entrega cancelada administrativamente.
 * - [REJECTED]: Entrega rejeitada pelo beneficiário ou por incumprimento de regras.
 * - [UNDER_ANALYSIS]: Entrega sob revisão ou aprovação pendente.
 */
enum class DeliveryStatus {
    SCHEDULED,
    DELIVERED,
    CANCELLED,
    REJECTED,
    UNDER_ANALYSIS
}

/**
 * Representa um registo de entrega de bens a um beneficiário.
 *
 * Esta entidade gere tanto o planeamento (agendamento) como a execução da entrega,
 * associando os produtos ao beneficiário final.
 *
 * **Regras de Negócio e Invariantes:**
 * - Deve estar sempre associada a um `beneficiaryId` válido.
 * - O mapa de `items` não deve conter quantidades negativas.
 * - A alteração para o estado [DELIVERED] deve desencadear a saída de stock.
 *
 * @property id Identificador único da entrega.
 * @property beneficiaryId ID do beneficiário que recebe (ou receberá) a entrega.
 * @property date Data efetiva da realização da entrega (timestamp). Relevante quando o status é [DELIVERED].
 * @property scheduledDate Data planeada para a entrega (timestamp). Usada para agendamentos.
 * @property status Estado atual do processo de entrega.
 * @property items Lista de bens incluídos na entrega. Representado por um Mapa: (ID do Produto -> Quantidade).
 * @property observations Notas opcionais sobre a entrega (ex: "Beneficiário recusou item X").
 * @property createdBy ID do utilizador (Staff) que registou ou agendou a entrega (para auditoria).
 */
data class Delivery(
    val id: String,
    val beneficiaryId: String,
    val date: Long,
    val scheduledDate: Long,
    val status: DeliveryStatus,
    val items: Map<String, Int> = emptyMap(),
    val observations: String? = null,
    val createdBy: String
)