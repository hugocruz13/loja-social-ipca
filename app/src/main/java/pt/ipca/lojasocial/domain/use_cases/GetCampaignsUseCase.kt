package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela listagem de todas as campanhas registadas.
 *
 * Permite obter a visão global das iniciativas (Ativas, Planeadas ou Inativas),
 * sendo fundamental para o ecrã principal de gestão de campanhas e para seleção
 * em formulários de entrada de stock.
 *
 * **Requisitos Funcionais:**
 * - **RF24**: Visibilidade e gestão das campanhas criadas.
 */
class GetCampaignsUseCase @Inject constructor(
    private val repository: CampaignRepository
) {

    /**
     * Executa a obtenção da lista completa de campanhas.
     *
     * @return Lista não filtrada de [Campaign] (internas e externas).
     */
    suspend operator fun invoke(): List<Campaign> {
        return repository.getCampaigns()
    }
}