package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.CampaignDto
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CampaignRepository {

    private val collection = firestore.collection("campanhas")

    override suspend fun getCampaigns(): List<Campaign> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject<CampaignDto>()?.toDomain()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCampaignById(id: String): Campaign? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject<CampaignDto>()?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addCampaign(campaign: Campaign) {
        val dto = campaign.toDto()
        collection.document(campaign.id).set(dto).await()
    }

    override suspend fun updateCampaign(campaign: Campaign) {
        val dto = campaign.toDto()
        collection.document(campaign.id).set(dto).await()
    }

    override suspend fun deleteCampaign(id: String) {
        collection.document(id).delete().await()
    }

    override suspend fun updateCampaignStatus(id: String, status: pt.ipca.lojasocial.domain.models.CampaignStatus) {
        val estadoStr = when (status) {
            pt.ipca.lojasocial.domain.models.CampaignStatus.ACTIVE -> "Ativa"
            pt.ipca.lojasocial.domain.models.CampaignStatus.PLANNED -> "Agendada"
            pt.ipca.lojasocial.domain.models.CampaignStatus.INACTIVE -> "Completa"
        }
        collection.document(id).update("estado", estadoStr).await()
    }
}