package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class DeleteCampaignUseCase @Inject constructor(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteCampaign(id)
    }
}