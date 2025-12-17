package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.StockItem
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

class GetStockListUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): List<StockItem> {
        return repository.getStockItems()
    }
}