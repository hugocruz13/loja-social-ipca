package pt.ipca.lojasocial.domain.use_cases

import pt.ipca.lojasocial.domain.models.CampaignStatus
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class UpdateCampaignStatusUseCase @Inject constructor(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(id: String, newStatus: CampaignStatus) {
        repository.updateCampaignStatus(id, newStatus)
    }
}