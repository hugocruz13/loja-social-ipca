package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por obter os detalhes de uma campanha específica.
 *
 * É utilizado principalmente na navegação para o ecrã de detalhes de uma campanha
 * ou para pré-carregar os dados num formulário de edição.
 *
 * **Contexto:**
 * Permite isolar a visualização de uma única campanha (Interna ou Externa) para
 * consulta de estatísticas, datas ou produtos necessários.
 */
class GetCampaignByIdUseCase @Inject constructor(
    private val repository: CampaignRepository
) {

    /**
     * Executa a pesquisa da campanha pelo seu identificador único.
     *
     * @param id O identificador da campanha a pesquisar.
     * @return O objeto [Campaign] se encontrado, ou `null` caso não exista.
     */
    suspend operator fun invoke(id: String): Campaign? {
        return repository.getCampaignById(id)
    }
}