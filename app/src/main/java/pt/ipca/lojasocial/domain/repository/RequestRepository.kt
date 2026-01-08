package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Request
import pt.ipca.lojasocial.domain.models.StatusType

/**
 * Interface responsável pela gestão dos Pedidos de Apoio (Candidaturas).
 * * Agora adaptada para Reactive Streams (Flow) para permitir atualizações em tempo real.
 */
interface RequestRepository {

    /**
     * Regista uma nova candidatura no sistema.
     * Mantém-se SUSPEND (operação única de escrita).
     */
    suspend fun addRequest(request: Request)

    /**
     * Obtém os detalhes de uma candidatura específica.
     * * NOTA: Podes manter 'suspend' se não precisares de atualizações em tempo real no ecrã de detalhes.
     * Se quiseres que o detalhe também atualize sozinho, muda para: Flow<Request?>
     */
    suspend fun getRequestById(id: String): Request?

    /**
     * Lista todas as candidaturas de um beneficiário.
     * * @return Flow<List<Request>> -> Emite uma nova lista sempre que este beneficiário fizer um novo pedido.
     */
    fun getRequestsByBeneficiary(beneficiaryId: String): Flow<List<Request>>

    /**
     * Lista as candidaturas de um determinado Ano Letivo.
     * É este método que alimenta o ecrã principal.
     * * @return Flow<List<Request>> -> O Room/Firestore vai emitir dados aqui automaticamente.
     */
    fun getRequestsByYear(schoolYearId: String, status: StatusType? = null): Flow<List<Request>>

    /**
     * Atualiza o estado de uma candidatura.
     */
    suspend fun updateStatusAndObservation(id: String, status: StatusType, observation: String)

    /**
     * Atualiza documentos e estado.
     */
    suspend fun updateRequestDocsAndStatus(
        id: String,
        documents: Map<String, String?>,
        status: StatusType
    )
}