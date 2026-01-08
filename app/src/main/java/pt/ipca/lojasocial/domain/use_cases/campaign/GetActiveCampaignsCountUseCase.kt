package pt.ipca.lojasocial.domain.use_cases.campaigns

import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class GetActiveCampaignsCountUseCase @Inject constructor(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getActiveCampaignsCount()
    }
}