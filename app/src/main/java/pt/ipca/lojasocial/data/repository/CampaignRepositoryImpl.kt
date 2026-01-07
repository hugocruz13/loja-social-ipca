package pt.ipca.lojasocial.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.CampaignDto
import pt.ipca.lojasocial.domain.models.Campaign
import pt.ipca.lojasocial.domain.repository.CampaignRepository
import javax.inject.Inject

/**
 * Implementação do repositório de campanhas com suporte a Tempo Real (Realtime).
 * Utiliza Kotlin Flows e Firestore SnapshotListeners.
 */
class CampaignRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CampaignRepository {

    private val collection = firestore.collection("campanhas")
    private val TAG = "CampaignRepo"

    /**
     * Obtém todas as campanhas em tempo real.
     * @return Um Flow que emite uma nova lista sempre que houver mudanças no Firestore.
     */
    override fun getCampaigns(): Flow<List<Campaign>> = callbackFlow {
        // 1. Registar o listener do Firestore
        val listenerRegistration: ListenerRegistration = collection
            .addSnapshotListener { snapshot, exception ->

                // Se ocorrer um erro na conexão (ex: sem permissões ou rede)
                if (exception != null) {
                    Log.e(TAG, "Erro ao escutar campanhas: ${exception.message}", exception)
                    close(exception) // Fecha o Flow com erro
                    return@addSnapshotListener
                }

                // Se houver dados, converte e emite
                if (snapshot != null) {
                    val campaigns = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject<CampaignDto>()?.toDomain()
                        } catch (e: Exception) {
                            Log.e(TAG, "Erro de parse na campanha ${doc.id}", e)
                            null
                        }
                    }
                    // Envia a nova lista para o ViewModel
                    trySend(campaigns)
                }
            }

        // 2. Callback executado quando o Flow é cancelado (ex: sair do ecrã)
        // Essencial para evitar memory leaks.
        awaitClose {
            Log.d(TAG, "Parando de escutar campanhas")
            listenerRegistration.remove()
        }
    }

    /**
     * Obtém uma campanha específica por ID em tempo real.
     */
    override fun getCampaignById(id: String): Flow<Campaign?> = callbackFlow {
        val listenerRegistration = collection.document(id)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "Erro ao escutar campanha $id", exception)
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    try {
                        val campaign = snapshot.toObject<CampaignDto>()?.toDomain()
                        trySend(campaign)
                    } catch (e: Exception) {
                        trySend(null)
                    }
                } else {
                    // Documento não existe ou foi apagado
                    trySend(null)
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    // --- OPERAÇÕES DE ESCRITA (Mantêm-se suspend pois são "One-shot") ---

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

    override suspend fun updateCampaignStatus(
        id: String,
        status: pt.ipca.lojasocial.domain.models.CampaignStatus
    ) {
        val estadoStr = when (status) {
            pt.ipca.lojasocial.domain.models.CampaignStatus.ACTIVE -> "Ativa"
            pt.ipca.lojasocial.domain.models.CampaignStatus.PLANNED -> "Agendada"
            pt.ipca.lojasocial.domain.models.CampaignStatus.INACTIVE -> "Completa"
        }
        collection.document(id).update("estado", estadoStr).await()
    }
}