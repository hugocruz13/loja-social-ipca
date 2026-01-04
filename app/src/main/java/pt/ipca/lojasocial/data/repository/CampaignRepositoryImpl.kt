package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CampaignRepository {

    private val campanhasCollection = firestore.collection("campanhas")

    override suspend fun getCampaigns(): List<Campaign> {
        return try {
            val snapshot = campanhasCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val estadoDb = doc.getString("estado") ?: ""
                val status = when (estadoDb) {
                    "Ativa" -> CampaignStatus.ACTIVE
                    "Agendada" -> CampaignStatus.PLANNED
                    "Completa" -> CampaignStatus.INACTIVE
                    else -> CampaignStatus.PLANNED
                }
                Campaign(
                    id = doc.id,
                    title = doc.getString("nome") ?: "",
                    description = doc.getString("descricao") ?: "",
                    startDate = doc.getLong("dataInicio") ?: 0L,
                    endDate = doc.getLong("dataFim") ?: 0L,
                    imageUrl = doc.getString("imagemUrl") ?: "",
                    type = if (doc.getString("tipo") == "Interno") CampaignType.INTERNAL else CampaignType.EXTERNAL,
                    status = status
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getCampaignById(id: String): Campaign? {
        return try {
            val doc = campanhasCollection.document(id).get().await()
            val estadoDb = doc.getString("estado") ?: ""
            val status = when (estadoDb) {
                "Ativa" -> CampaignStatus.ACTIVE
                "Agendada" -> CampaignStatus.PLANNED
                "Completa" -> CampaignStatus.INACTIVE
                else -> CampaignStatus.PLANNED
            }
            if (doc.exists()) {
                Campaign(
                    id = doc.id,
                    title = doc.getString("nome") ?: "",
                    description = doc.getString("descricao") ?: "",
                    startDate = doc.getLong("dataInicio") ?: 0L,
                    endDate = doc.getLong("dataFim") ?: 0L,
                    imageUrl = doc.getString("imagemUrl") ?: "",
                    type = if (doc.getString("tipo") == "Interno") CampaignType.INTERNAL else CampaignType.EXTERNAL,
                    status = status
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addCampaign(campaign: Campaign) {
        try {
            val data = hashMapOf(
                "nome" to campaign.title,
                "descricao" to campaign.description,
                "dataInicio" to campaign.startDate,
                "dataFim" to campaign.endDate,
                "tipo" to if (campaign.type == CampaignType.INTERNAL) "Interno" else "Externo",
                "estado" to when (campaign.status) {
                    CampaignStatus.ACTIVE -> "Ativa"
                    CampaignStatus.PLANNED -> "Agendada"
                    CampaignStatus.INACTIVE -> "Completa"
                    else -> "Agendada"
                },
                "imagemUrl" to campaign.imageUrl
            )
            campanhasCollection.document(campaign.id).set(data).await()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override suspend fun updateCampaign(campaign: Campaign) {
        val data = mutableMapOf<String, Any>(
            "nome" to campaign.title,
            "descricao" to campaign.description,
            "dataInicio" to campaign.startDate,
            "dataFim" to campaign.endDate,
            "tipo" to if (campaign.type == CampaignType.INTERNAL) "Interno" else "Externo",
            "estado" to when (campaign.status) {
                CampaignStatus.ACTIVE -> "Ativa"
                CampaignStatus.PLANNED -> "Agendada"
                CampaignStatus.INACTIVE -> "Completa"
                else -> "Agendada"
            }
        )

        if (!campaign.imageUrl.isNullOrBlank()) {
            data["imagemUrl"] = campaign.imageUrl
        }

        campanhasCollection.document(campaign.id).update(data).await()
    }

    override suspend fun deleteCampaign(id: String) {}

    override suspend fun updateCampaignStatus(id: String, status: CampaignStatus) {}
}