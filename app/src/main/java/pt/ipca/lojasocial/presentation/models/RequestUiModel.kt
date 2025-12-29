package pt.ipca.lojasocial.presentation.models
import pt.ipca.lojasocial.presentation.components.StatusType

data class RequestUiModel(
    val requestId: String,
    val beneficiaryName: String,
    val status: StatusType
)