package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

class UpdateStockQuantityUseCase @Inject constructor(
    private val repository: StockRepository
) {
    suspend operator fun invoke(id: String, newQuantity: Int) {
        // Regra de Negócio: Não permitir stock negativo
        if (newQuantity < 0) {
            throw IllegalArgumentException("A quantidade de stock não pode ser negativa.")
        }
        repository.updateStockQuantity(id, newQuantity)
    }
}