package pt.ipca.lojasocial.domain.use_cases.stock

import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela atualização das quantidades físicas no inventário.
 *
 * Este componente é central para a manutenção da verdade logística do armazém,
 * sendo utilizado tanto para correções manuais (inventário) como para abates
 * automáticos após entregas (**RF07**).
 *
 * **Regras de Negócio:**
 * - **Integridade de Dados:** Impede a existência de stock negativo, lançando uma
 * exceção caso a operação resulte num valor inválido.
 * - **Rastreabilidade:** Permite ajustar lotes específicos sem afetar outros
 * lotes do mesmo produto com validades diferentes.
 */
class UpdateStockQuantityUseCase @Inject constructor(
    private val repository: StockRepository
) {

    /**
     * Executa a atualização da quantidade do lote indicado.
     *
     * @param id O identificador único do lote de stock (StockItem).
     * @param newQuantity A nova quantidade total (deve ser >= 0).
     * @throws IllegalArgumentException Se a quantidade fornecida for negativa.
     */
    suspend operator fun invoke(id: String, newQuantity: Int) {
        // Regra de Negócio: Não permitir stock negativo.
        // Fisicamente, um armazém não pode ter "menos dois" pacotes de massa.
        if (newQuantity < 0) {
            throw IllegalArgumentException("A quantidade de stock não pode ser negativa.")
        }
        repository.updateStockQuantity(id, newQuantity)
    }
}