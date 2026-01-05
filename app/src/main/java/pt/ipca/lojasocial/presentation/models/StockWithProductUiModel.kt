package pt.ipca.lojasocial.presentation.models

data class StockWithProductUiModel(
    val stockId: String,
    val productName: String,
    val quantity: Int
)

data class StockUiModel(
    val stockId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val photoUrl: String? = null
)

// Representa uma "linha" de validade específica
data class StockBatchUi(
    val stockId: String,
    val quantity: Int,
    val expiryDate: Long // Timestamp
)

// Representa o Produto Genérico (o cartão principal)
data class ProductStockGroup(
    val productId: String,
    val productName: String,
    val photoUrl: String?,
    val totalQuantity: Int, // Soma de todos os lotes
    val batches: List<StockBatchUi> // Lista de validades
)