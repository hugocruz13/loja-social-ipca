package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela remoção de campanhas do sistema.
 *
 * Permite eliminar registos de campanhas criados por engano ou que não possuam
 * histórico relevante associado.
 *
 * **Nota de Arquitetura:**
 * Este método executa uma remoção permanente. Para campanhas que já ocorreram e têm
 * doações associadas, recomenda-se a alteração do estado para 'Inativo' ou 'Arquivado'
 * em vez da eliminação, para preservar a integridade dos relatórios estatísticos (**RF26**).
 */
class DeleteCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository
) {

    /**
     * Executa a eliminação da campanha indicada.
     *
     * @param id O identificador único da campanha a remover.
     */
    suspend operator fun invoke(id: String) {
        // Futura validação: Verificar se existem Stocks associados antes de permitir apagar.
        repository.deleteCampaign(id)
    }
}