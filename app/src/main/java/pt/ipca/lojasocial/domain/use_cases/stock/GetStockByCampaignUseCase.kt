package pt.ipca.lojasocial.domain.use_cases.stock

import pt.ipca.lojasocial.domain.models.Stock
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por obter todo o stock angariado no âmbito de uma campanha específica.
 *
 * Permite isolar os bens que entraram no armazém associados a uma iniciativa (Interna ou Externa),
 * facilitando a análise de resultados e rastreabilidade da origem.
 *
 * **Contexto de Negócio:**
 * Fundamental para a geração de relatórios de impacto (**RF26**) e para verificar se os objetivos
 * da campanha foram cumpridos (ex: "Conseguimos as 500 latas de atum pedidas?").
 *
 * **Requisitos Funcionais:**
 * - **RF06**: Associação de entradas de stock a campanhas.
 */
class GetStockByCampaignUseCase @Inject constructor(
    private val repository: StockRepository
) {

    /**
     * Executa a pesquisa de lotes de stock associados ao ID da campanha fornecido.
     *
     * @param campaignId O identificador único da campanha.
     * @return Lista de [Stock] cuja origem foi essa campanha.
     */
    suspend operator fun invoke(campaignId: String): List<Stock> {
        return repository.getItemsByCampaign(campaignId)
    }
}