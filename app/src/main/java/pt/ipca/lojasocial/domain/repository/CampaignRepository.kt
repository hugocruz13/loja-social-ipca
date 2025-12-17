package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus

interface CampaignRepository {
    suspend fun getCampaigns(): List<Campaign>
    suspend fun getCampaignById(id: String): Campaign?
    suspend fun addCampaign(campaign: Campaign)
    suspend fun updateCampaign(campaign: Campaign)
    suspend fun deleteCampaign(id: String)
    suspend fun updateCampaignStatus(id: String, status: CampaignStatus)
}