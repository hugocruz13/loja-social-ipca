package pt.ipca.lojasocial.domain.models

import java.util.UUID


enum class EducationLevels {
    LICENCIATURA,
    MESTRADO,
    DOUTORADO,
    CTESP
}

/**
 * Define o tipo de apoio solicitado.
 */
enum class RequestType {
    FOOD,       // Cabaz Alimentar
    HYGIENE,    // Higiene
    CLEANING,   // Limpeza
    ALL             // Todos os tipos
}

/**
 * Representa um pedido de apoio (Candidatura) feito por um Beneficiário.
 * Corresponde à entidade 'Requerimento' no diagrama original.
 *
 * @property id Identificador único do requerimento.
 * @property beneficiaryId O ID do beneficiário que faz o pedido.
 * @property schoolYearId O ano letivo a que diz respeito.
 * @property submissionDate Data de submissão em Timestamp (milissegundos).
 * @property status Estado atual do pedido.
 * @property type Tipo de apoio solicitado.
 * @property documentUrls Lista de links para os ficheiros no Firebase Storage.
 * @property observations Notas opcionais.
 */
data class Request(
    val id: String = UUID.randomUUID().toString(),
    val beneficiaryId: String,
    val schoolYearId: String,
    val submissionDate: Long = System.currentTimeMillis(),
    val status: StatusType = StatusType.ANALISE,
    val type: RequestType? = RequestType.FOOD,
    val documents: Map<String, String?> = emptyMap(),
    val observations: String = ""
)