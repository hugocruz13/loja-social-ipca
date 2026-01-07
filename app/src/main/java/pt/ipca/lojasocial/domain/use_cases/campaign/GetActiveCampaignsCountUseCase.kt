package pt.ipca.lojasocial.domain.use_cases.campaigns

import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class GetActiveCampaignsCountUseCase @Inject constructor(
    private val repository: CampaignRepository
) {
    // O operador 'invoke' permite chamar a classe como se fosse uma função
    suspend operator fun invoke(): Int {
        return repository.getActiveCampaignsCount()
    }
}