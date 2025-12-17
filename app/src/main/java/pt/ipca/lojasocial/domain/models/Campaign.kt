package pt.ipca.lojasocial.domain.models

data class Campaign(
    val id: String,
    val title: String,
    val description: String,
    val startDate: Long,
    val endDate: Long,
    val type: CampaignType,
    val status: CampaignStatus,
    val neededProductIds: List<String> = emptyList()
)