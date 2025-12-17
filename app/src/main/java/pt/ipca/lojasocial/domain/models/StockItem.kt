package pt.ipca.lojasocial.domain.models

data class StockItem(
    val id: String,
    val productId: String,
    val campaignId: String? = null,
    val quantity: Int,
    val entryDate: Long,
    val expiryDate: Long,
    val observations: String? = null
)