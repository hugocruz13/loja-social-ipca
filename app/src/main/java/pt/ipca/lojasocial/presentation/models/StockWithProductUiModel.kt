package pt.ipca.lojasocial.presentation.models

data class StockWithProductUiModel (
    val stockId: String,
    val productName: String,
    val quantity: Int
)

data class StockUiModel(
    val stockId: String,
    val productId: String,
    val productName: String,
    val quantity: Int
)