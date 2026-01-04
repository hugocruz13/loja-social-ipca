package pt.ipca.lojasocial.domain.use_cases.stock

import pt.ipca.lojasocial.domain.models.Stock
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela listagem geral do Inventário Físico.
 *
 * Fornece a visão global de todos os lotes de produtos existentes no armazém,
 * independentemente da sua origem, validade ou categoria.
 *
 * **Contexto de UI:**
 * Alimenta o ecrã principal de gestão de stock, onde o Staff pode consultar
 * quantidades e datas de validade de cada lote para tomar decisões logísticas.
 */
class GetStockListUseCase @Inject constructor(
    private val repository: StockRepository
) {

    /**
     * Executa a obtenção da lista completa de itens em stock.
     *
     * @return Lista de [Stock]. Note que cada elemento representa um lote físico
     * (com validade específica), e não apenas o tipo de produto abstrato.
     */
    suspend operator fun invoke(): List<Stock> {
        return repository.getStockItems()
    }
}