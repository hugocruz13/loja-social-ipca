package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class GetCampaignByIdUseCase @Inject constructor(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(id: String): Campaign? {
        return repository.getCampaignById(id)
    }
}