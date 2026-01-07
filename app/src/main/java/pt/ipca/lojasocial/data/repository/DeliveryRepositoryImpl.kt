package pt.ipca.lojasocial.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.DeliveryMapper
import pt.ipca.lojasocial.data.remote.dto.DeliveryDto
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.repository.DeliveryRepository
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : DeliveryRepository {

    private val collection = firestore.collection("entregas")
    private val beneficiariesCollection = firestore.collection("beneficiarios")
    private val TAG = "DeliveryRepo"

    // Todas as entregas (para Staff)
    override fun getDeliveries(): Flow<List<Delivery>> = callbackFlow {
        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Erro realtime all deliveries: ${error.message}")
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val deliveries = snapshot.documents.mapNotNull { doc ->
                    val dto = doc.toObject(DeliveryDto::class.java)
                    if (dto != null) DeliveryMapper.toDomain(doc.id, dto) else null
                }
                trySend(deliveries)
            }
        }
        awaitClose { listener.remove() }
    }

    // Entregas de um Beneficiário Específico
    override fun getDeliveriesByBeneficiary(beneficiaryId: String): Flow<List<Delivery>> =
        callbackFlow {
            // Criar referência para consulta correta
            val beneficiaryRef = beneficiariesCollection.document(beneficiaryId)

            val listener = collection
                .whereEqualTo("idBeneficiario", beneficiaryRef) // Filtra no Firebase
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Erro realtime beneficiary deliveries: ${error.message}")
                        close(error)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val deliveries = snapshot.documents.mapNotNull { doc ->
                            val dto = doc.toObject(DeliveryDto::class.java)
                            if (dto != null) DeliveryMapper.toDomain(doc.id, dto) else null
                        }
                        trySend(deliveries)
                    }
                }
            awaitClose { listener.remove() }
        }

    // Detalhe de uma Entrega
    override fun getDeliveryById(id: String): Flow<Delivery?> = callbackFlow {
        val listener = collection.document(id).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val dto = snapshot.toObject(DeliveryDto::class.java)
                if (dto != null) {
                    trySend(DeliveryMapper.toDomain(id, dto))
                } else {
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    // --- MÉTODOS DE ESCRITA ---

    override suspend fun addDelivery(delivery: Delivery) {
        val dto = DeliveryMapper.toDto(delivery, firestore)
        collection.document(delivery.id).set(dto).await()
    }

    override suspend fun deleteDelivery(id: String) {
        collection.document(id).delete().await()
    }

    override suspend fun updateDeliveryStatus(id: String, status: DeliveryStatus) {
        val statusString = when (status) {
            DeliveryStatus.SCHEDULED -> "AGENDADA"
            DeliveryStatus.DELIVERED -> "ENTREGUE"
            DeliveryStatus.CANCELLED -> "CANCELADA"
            DeliveryStatus.REJECTED -> "REJEITADA"
            DeliveryStatus.UNDER_ANALYSIS -> "EM_ANALISE"
        }
        collection.document(id).update("estado", statusString).await()
    }

    override suspend fun updateDeliveryItems(id: String, items: Map<String, Int>) {
        collection.document(id).update("produtosEntregues", items).await()
    }

    override suspend fun updateDeliveryDate(id: String, timestamp: Long) {
        collection.document(id).update("dataHoraPlaneada", timestamp).await()
    }

    override suspend fun updateDeliveryObservations(id: String, observations: String) {
        collection.document(id).update("observacoes", observations).await()
    }

    override suspend fun getUpcomingDeliveries(timestampLimit: Long): List<Delivery> {
        // Mantido estático (útil para workers que correm em background e morrem logo a seguir)
        val snapshot =
            collection.whereLessThanOrEqualTo("dataHoraPlaneada", timestampLimit).get().await()
        return snapshot.documents.mapNotNull { doc ->
            val dto = doc.toObject(DeliveryDto::class.java)
            if (dto != null) DeliveryMapper.toDomain(doc.id, dto) else null
        }
    }
}