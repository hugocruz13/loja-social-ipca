package pt.ipca.lojasocial.domain.use_cases.stock

import pt.ipca.lojasocial.domain.models.StockItem
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

class AddStockItemUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(item: StockItem) {
        // Validação básica (Quantidade positiva)
        if (item.quantity <= 0) throw IllegalArgumentException("Quantidade deve ser maior que 0")

        repository.addStockItem(item)
    }
}