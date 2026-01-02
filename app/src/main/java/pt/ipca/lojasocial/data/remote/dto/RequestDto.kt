package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * DTO para a coleção 'requerimentos'.
 * Agora mapeia os documentos como um Mapa (Chave -> URL) para sabermos qual é qual.
 */
data class RequestDto(
    @get:PropertyName("idBeneficiario") @set:PropertyName("idBeneficiario")
    var beneficiaryId: String = "",

    @get:PropertyName("idAnoLetivo") @set:PropertyName("idAnoLetivo")
    var schoolYearId: String = "",

    @get:PropertyName("dataSubmissao") @set:PropertyName("dataSubmissao")
    var submissionDate: Long = 0,

    @get:PropertyName("estado") @set:PropertyName("estado")
    var status: String = "",

    @get:PropertyName("tipo") @set:PropertyName("tipo")
    var type: String = "",

    @get:PropertyName("observacoes") @set:PropertyName("observacoes")
    var observations: String = "",

    @get:PropertyName("documentosUrl") @set:PropertyName("documentosUrl")
    var documentUrls: Map<String, String?> = emptyMap()
)