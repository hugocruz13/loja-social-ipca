package pt.ipca.lojasocial.domain.use_cases.campaign

import pt.ipca.lojasocial.domain.models.CampaignStatus
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela gestão do ciclo de vida (Estados) de uma campanha.
 *
 * Permite transitar uma campanha entre estados (ex: Planeada -> Ativa -> Terminada),
 * controlando quando é que esta está visível para doações ou quando deve ser arquivada.
 *
 * **Contexto de Negócio:**
 * Esta é a alternativa segura à eliminação. Ao passar uma campanha para 'Terminada' ou 'Arquivada',
 * bloqueia-se novas entradas, mas preserva-se todo o histórico de doações para os
 * relatórios estatísticos (**RF26**).
 */
class UpdateCampaignStatusUseCase @Inject constructor(
    private val repository: CampaignRepository
) {

    /**
     * Executa a alteração do estado da campanha.
     *
     * @param id O identificador único da campanha.
     * @param newStatus O novo estado a aplicar (enum [CampaignStatus]).
     */
    suspend operator fun invoke(id: String, newStatus: CampaignStatus) {
        repository.updateCampaignStatus(id, newStatus)
    }
}