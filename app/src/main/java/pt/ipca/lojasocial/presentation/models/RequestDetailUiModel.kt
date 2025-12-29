package pt.ipca.lojasocial.presentation.models

import pt.ipca.lojasocial.domain.models.RequestType
import pt.ipca.lojasocial.presentation.components.StatusType

data class RequestDetailUiModel(
    val id: String,
    val beneficiaryName: String,
    val cc: String,
    val email: String,
    val phone: String,
    val submissionDate: String,
    val status: StatusType,
    val type: RequestType,
    val documents: Map<String, String?> = emptyMap()
)