package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.StockItem
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

class GetExpiringStockUseCase @Inject constructor(
    private val repository: StockRepository
) {
    // daysThreshold: 30 para 1 mês, 7 para 1 semana, etc.
    suspend operator fun invoke(daysThreshold: Int): List<StockItem> {
        val currentTime = System.currentTimeMillis()
        val thresholdTime = currentTime + (daysThreshold * 24 * 60 * 60 * 1000L) // Converte dias para milissegundos

        return repository.getItemsExpiringBefore(thresholdTime)
    }
}