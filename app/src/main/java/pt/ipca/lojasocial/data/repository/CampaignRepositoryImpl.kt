package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.models.CampaignStatus
import pt.ipca.lojasocial.domain.models.CampaignType
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import java.util.Calendar
import javax.inject.Inject

class CampaignRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CampaignRepository {

    private val campanhasCollection = firestore.collection("campanhas")

    private fun calculateRealStatus(dbStatus: String, startDate: Long, endDate: Long): CampaignStatus {
        val hoje = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return when {
            endDate < hoje -> CampaignStatus.INACTIVE // Se já passou o fim, está Completa
            startDate > hoje -> CampaignStatus.PLANNED // Se ainda não começou, está Agendada
            else -> CampaignStatus.ACTIVE // Se está no intervalo, está Ativa
        }
    }

    override suspend fun getCampaigns(): List<Campaign> {
        return try {
            val snapshot = campanhasCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val start = doc.getLong("dataInicio") ?: 0L
                val end = doc.getLong("dataFim") ?: 0L
                val estadoDb = doc.getString("estado") ?: ""

                // Validamos o status em tempo real ao carregar da DB
                val status = calculateRealStatus(estadoDb, start, end)

                Campaign(
                    id = doc.id,
                    title = doc.getString("nome") ?: "",
                    description = doc.getString("descricao") ?: "",
                    startDate = start,
                    endDate = end,
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
            if (doc.exists()) {
                val start = doc.getLong("dataInicio") ?: 0L
                val end = doc.getLong("dataFim") ?: 0L
                val estadoDb = doc.getString("estado") ?: ""

                val status = calculateRealStatus(estadoDb, start, end)

                Campaign(
                    id = doc.id,
                    title = doc.getString("nome") ?: "",
                    description = doc.getString("descricao") ?: "",
                    startDate = start,
                    endDate = end,
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
                "estado" to mapStatusToString(campaign.status),
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
            "estado" to mapStatusToString(campaign.status)
        )

        if (!campaign.imageUrl.isNullOrBlank()) {
            data["imagemUrl"] = campaign.imageUrl
        }

        campanhasCollection.document(campaign.id).update(data).await()
    }

    private fun mapStatusToString(status: CampaignStatus): String {
        return when (status) {
            CampaignStatus.ACTIVE -> "Ativa"
            CampaignStatus.PLANNED -> "Agendada"
            CampaignStatus.INACTIVE -> "Completa"
            else -> "Agendada"
        }
    }

    override suspend fun deleteCampaign(id: String) {}
    override suspend fun updateCampaignStatus(id: String, status: CampaignStatus) {}
}