package pt.ipca.lojasocial.data.remote.dto

import com.google.firebase.firestore.PropertyName

/**
 * DTO para a coleção 'requerimentos'.
 * Mapeia as variáveis em Inglês para os campos em Português na BD.
 */
data class RequestDto(
    @get:PropertyName("idBeneficiario") @set:PropertyName("idBeneficiario")
    var beneficiaryId: String = "",

    @get:PropertyName("idAnoLetivo") @set:PropertyName("idAnoLetivo")
    var schoolYearId: String = "",

    @get:PropertyName("dataSubmissao") @set:PropertyName("dataSubmissao")
    var submissionDate: Long = 0,

    // Guardamos o Enum como String (ex: "SUBMITTED", "APPROVED")
    @get:PropertyName("estado") @set:PropertyName("estado")
    var status: String = "",

    // Guardamos o Enum como String (ex: "FOOD", "HYGIENE")
    @get:PropertyName("tipo") @set:PropertyName("tipo")
    var type: String = "",

    // Lista de URLs dos ficheiros (PDFs/Imagens)
    @get:PropertyName("documentosUrl") @set:PropertyName("documentosUrl")
    var documentUrls: List<String> = emptyList(),
)