package pt.ipca.lojasocial.domain.use_cases.campaign

import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela edição dos dados informativos de uma campanha.
 *
 * Este componente permite alterar as propriedades descritivas da campanha,
 * distinguindo-se do [UpdateCampaignStatusUseCase] que gere apenas o ciclo de vida.
 *
 * **Cenários de Uso:**
 * - Correção de erros ortográficos no título ou descrição.
 * - Prolongamento da data de fim de uma campanha (extensão do prazo).
 * - Alteração da lista de categorias de produtos aceites.
 */
class UpdateCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository
) {

    /**
     * Executa a atualização dos dados da campanha.
     *
     * @param campaign O objeto [Campaign] com os dados editados.
     * **Nota:** O `id` da campanha deve permanecer inalterado para garantir que a atualização
     * incide sobre o registo correto.
     */
    suspend operator fun invoke(campaign: Campaign) {
        repository.updateCampaign(campaign)
    }
}