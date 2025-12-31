package pt.ipca.lojasocial.domain.use_cases.stock

import pt.ipca.lojasocial.domain.models.StockItem
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por obter os detalhes de um lote específico de stock.
 *
 * **Distinção Importante:**
 * Ao contrário do `GetProductById` (que devolve a definição genérica, ex: "Arroz"),
 * este Use Case devolve uma instância física concreta (ex: "Lote de 50kg de Arroz que vence em Dezembro").
 *
 * **Contexto:**
 * Utilizado para abrir o ecrã de detalhes de um item do inventário ou para
 * carregar os dados num formulário de edição/correção de stock (**RF07**).
 */
class GetStockItemByIdUseCase @Inject constructor(
    private val repository: StockRepository
) {

    /**
     * Executa a pesquisa do lote de stock pelo seu identificador único.
     *
     * @param id O identificador do lote (StockItem).
     * @return O objeto [StockItem] encontrado ou `null` se não existir.
     */
    suspend operator fun invoke(id: String): StockItem? {
        return repository.getStockItemById(id)
    }
}