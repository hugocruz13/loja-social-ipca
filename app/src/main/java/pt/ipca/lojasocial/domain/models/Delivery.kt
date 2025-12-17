package pt.ipca.lojasocial.domain.models

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