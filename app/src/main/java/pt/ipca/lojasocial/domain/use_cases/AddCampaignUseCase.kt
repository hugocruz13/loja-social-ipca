package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela criação de novas campanhas de angariação.
 *
 * Permite registar iniciativas (Internas ou Externas) no sistema, que servirão
 * posteriormente para agrupar doações e mobilizar recursos.
 *
 * **Requisitos Funcionais:**
 * - **RF24**: Registo de campanhas na app e visibilidade no website.
 */
class AddCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository
) {

    /**
     * Executa o registo de uma nova campanha.
     *
     * @param campaign A entidade [Campaign] com os dados definidos (datas, tipo, descrição).
     */
    suspend operator fun invoke(campaign: Campaign) {
        // Nota: Futuras validações de datas (ex: Data Início < Data Fim) devem ser implementadas aqui.
        repository.addCampaign(campaign)
    }
}