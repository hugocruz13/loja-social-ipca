package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

class DeleteStockItemUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteStockItem(id)
    }
}