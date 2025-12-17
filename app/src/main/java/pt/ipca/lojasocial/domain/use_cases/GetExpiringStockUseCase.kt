package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.StockItem
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por identificar itens de stock em risco de validade (ou já expirados).
 *
 * Este componente contém a lógica temporal necessária para converter um "prazo em dias"
 * num timestamp concreto, permitindo filtrar o inventário.
 *
 * **Contexto de Negócio:**
 * É a base para o sistema de alertas de validade (**RF09**) e apoia a estratégia logística
 * FEFO (First Expired, First Out - **RF25**), ajudando a priorizar a saída de produtos
 * com validade curta para evitar desperdício.
 */
class GetExpiringStockUseCase @Inject constructor(
    private val repository: StockRepository
) {

    /**
     * Executa a pesquisa de itens cuja data de validade é anterior ao limite calculado.
     *
     * @param daysThreshold O intervalo em dias a partir de hoje para considerar o risco (ex: 30 dias).
     * Se a validade do item for menor que (Hoje + daysThreshold), o item é retornado.
     * @return Lista de [StockItem] em risco ou expirados.
     */
    suspend operator fun invoke(daysThreshold: Int): List<StockItem> {
        val currentTime = System.currentTimeMillis()

        // Lógica de Negócio: Conversão de dias (Humano) para Millis (Máquina)
        // 24h * 60m * 60s * 1000ms
        val thresholdTime = currentTime + (daysThreshold * 24 * 60 * 60 * 1000L)

        return repository.getItemsExpiringBefore(thresholdTime)
    }
}