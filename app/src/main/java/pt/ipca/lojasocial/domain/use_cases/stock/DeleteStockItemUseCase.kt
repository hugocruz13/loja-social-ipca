package pt.ipca.lojasocial.domain.use_cases.stock

import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela remoção de registos de stock (lotes) do inventário.
 *
 * Esta operação destina-se primariamente à **correção de erros de lançamento**
 * (ex: entrada registada em duplicado ou com dados incorrigíveis).
 *
 * **Nota de Processo:**
 * Para saídas normais de produtos (entregas a beneficiários) ou quebras (lixo),
 * **NÃO** se deve usar este método. Nesses casos, deve-se atualizar a quantidade
 * através do [UpdateStockQuantityUseCase] para manter o histórico de movimentação
 * e cumprir os requisitos **RF07** (Atualização de stock) e **RF27** (Histórico).
 */
class DeleteStockItemUseCase @Inject constructor(
    private val repository: StockRepository
) {

    /**
     * Executa a eliminação física do lote de stock indicado.
     *
     * @param id O identificador único do lote de stock a remover.
     */
    suspend operator fun invoke(id: String) {
        repository.deleteStockItem(id)
    }
}