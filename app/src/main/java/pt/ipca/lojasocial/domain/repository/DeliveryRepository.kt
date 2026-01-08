package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus

/**
 * Interface responsável pela gestão logística das entregas e agendamentos.
 *
 * Controla o fluxo de saída de bens para os beneficiários, desde o planeamento (agendamento)
 * até à concretização da entrega (alteração de stock).
 *
 * Cobre os requisitos de **RF10 a RF16** e suporte a notificações (**RF22**).
 */
interface DeliveryRepository {

    // --- CRUD Básico ---

    /**
     * Obtém a lista global de todas as entregas (agendadas ou realizadas).
     *
     * @return Lista de [Delivery].
     */
    fun getDeliveries(): Flow<List<Delivery>>

    /**
     * Obtém os detalhes de uma entrega específica.
     *
     * @param id O identificador único da entrega.
     * @return [Delivery] se encontrada, ou `null` caso contrário.
     */
    fun getDeliveryById(id: String): Flow<Delivery?>

    /**
     * Regista uma nova entrega ou agendamento no sistema.
     *
     * Cumpre os requisitos **RF10** (Registo de entregas) e **RF11** (Agendamento).
     * A distinção é feita através da data agendada e do estado inicial.
     *
     * @param delivery O objeto [Delivery] a persistir.
     */
    suspend fun addDelivery(delivery: Delivery)

    /**
     * Remove uma entrega do sistema.
     *
     * **Nota:** Se a entrega já foi realizada ([DeliveryStatus.DELIVERED]), a eliminação
     * deve ser tratada com cuidado para manter a coerência do histórico de stock.
     *
     * @param id O identificador da entrega a eliminar.
     */
    suspend fun deleteDelivery(id: String)


    // --- Gestão de Estado e Itens ---

    /**
     * Altera o estado de uma entrega (ex: de [DeliveryStatus.SCHEDULED] para [DeliveryStatus.DELIVERED]).
     *
     * Cumpre o requisito **RF13**.
     * **Nota Importante:** A transição para [DELIVERED] deve desencadear o abate automático
     * no stock (RF07).
     *
     * @param id O identificador da entrega.
     * @param status O novo estado a aplicar.
     */
    suspend fun updateDeliveryStatus(id: String, status: DeliveryStatus)

    /**
     * Atualiza a lista de produtos associados a uma entrega.
     *
     * Cumpre o requisito **RF12** (Associação de produtos às entregas agendadas).
     *
     * @param id O identificador da entrega.
     * @param items Mapa atualizado de bens (ID do Produto -> Quantidade).
     */
    suspend fun updateDeliveryItems(id: String, items: Map<String, Int>)

    /**
     * Atualiza a data e hora planeada de uma entrega.
     *
     * @param id O identificador da entrega.
     * @param timestamp A nova data/hora em milissegundos.
     */
    suspend fun updateDeliveryDate(id: String, timestamp: Long)

    /**
     * Atualiza as observações de uma entrega.
     *
     * @param id O identificador da entrega.
     * @param observations As novas observações.
     */
    suspend fun updateDeliveryObservations(id: String, observations: String)


    // --- Consultas Específicas ---

    /**
     * Lista o histórico de entregas realizadas ou agendadas para um beneficiário específico.
     *
     * Cumpre o requisito **RF16** (Listagem do histórico de entregas).
     *
     * @param beneficiaryId O identificador do beneficiário.
     * @return Lista de entregas associadas a este beneficiário.
     */
    fun getDeliveriesByBeneficiary(beneficiaryId: String): Flow<List<Delivery>>


    // --- Para Notificações e Agendamento ---

    /**
     * Obtém as entregas agendadas cuja data prevista é anterior ou igual ao limite fornecido.
     *
     * Fundamental para o requisito **RF22** (Notificações automáticas de proximidade) e
     * envio de emails (**RF23**).
     *
     * @param timestampLimit O momento no tempo (timestamp) até ao qual queremos verificar agendamentos.
     * @return Lista de entregas agendadas próximas.
     */
    suspend fun getUpcomingDeliveries(timestampLimit: Long): List<Delivery>

    suspend fun getPendingDeliveriesCount(userId: String?): Int
}
