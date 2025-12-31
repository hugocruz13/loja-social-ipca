package pt.ipca.lojasocial.presentation.models

import pt.ipca.lojasocial.domain.models.StatusType

data class RequestUiModel(
    val requestId: String,
    val beneficiaryName: String,
    val status: StatusType
)