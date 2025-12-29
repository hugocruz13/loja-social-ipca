package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.presentation.components.StatusType

/**
 * Interface responsável pela gestão dos Pedidos de Apoio (Candidaturas).
 *
 * Define as operações essenciais para criar, ler e atualizar candidaturas,
 * servindo de ponte entre os Use Cases e a implementação concreta (Firestore).
 */
interface RequestRepository {

    /**
     * Regista uma nova candidatura no sistema.
     *
     * @param request O objeto [Request] totalmente preenchido (incluindo URLs de documentos, se houver).
     */
    suspend fun addRequest(request: Request)

    /**
     * Obtém os detalhes de uma candidatura específica através do seu ID.
     *
     * @param id O identificador único do pedido.
     * @return O objeto [Request] se encontrado, ou null caso contrário.
     */
    suspend fun getRequestById(id: String): Request?

    /**
     * Lista todas as candidaturas feitas por um determinado beneficiário.
     * Útil para mostrar o histórico na ficha do utente.
     *
     * @param beneficiaryId O ID do beneficiário.
     * @return Lista de [Request] associados a esse beneficiário.
     */
    suspend fun getRequestsByBeneficiary(beneficiaryId: String): List<Request>

    /**
     * Lista as candidaturas de um determinado Ano Letivo.
     * Permite filtragem opcional pelo estado (ex: ver apenas as "SUBMITTED").
     *
     * @param schoolYearId O identificador do ano letivo (ex: "2024_2025").
     * @param status (Opcional) Se fornecido, filtra a lista para mostrar apenas pedidos neste estado.
     * @return Lista de [Request] que correspondem aos critérios.
     */
    suspend fun getRequestsByYear(schoolYearId: String, status: StatusType? = null): List<Request>

    /**
     * Atualiza o estado de uma candidatura (ex: de SUBMITTED para APPROVED).
     *
     * @param id O identificador do pedido.
     * @param newStatus O novo estado a aplicar.
     */
    suspend fun updateStatus(id: String, newStatus: StatusType)

    suspend fun updateRequestDocsAndStatus(id: String, documents: Map<String, String?>, status: StatusType)
}