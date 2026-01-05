package pt.ipca.lojasocial.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun getDeliveries(): List<Delivery> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val deliveryDto = doc.toObject(DeliveryDto::class.java)
                if (deliveryDto != null) {
                    DeliveryMapper.toDomain(doc.id, deliveryDto)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error getting deliveries: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun addDelivery(delivery: Delivery) {
        try {
            val deliveryDto = DeliveryMapper.toDto(delivery, firestore)
            collection.document(delivery.id).set(deliveryDto).await()
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error adding delivery: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getDeliveryById(id: String): Delivery? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                val deliveryDto = doc.toObject(DeliveryDto::class.java)
                if (deliveryDto != null) {
                    DeliveryMapper.toDomain(id, deliveryDto)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error getting delivery by ID: ${e.message}", e)
            null
        }
    }

    override suspend fun getUpcomingDeliveries(timestampLimit: Long): List<Delivery> {
        return try {
            val snapshot = collection
                .whereLessThanOrEqualTo("dataHoraPlaneada", timestampLimit)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val deliveryDto = doc.toObject(DeliveryDto::class.java)
                if (deliveryDto != null) {
                    DeliveryMapper.toDomain(doc.id, deliveryDto)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error getting upcoming deliveries: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getDeliveriesByBeneficiary(beneficiaryId: String): List<Delivery> {
        return try {
            val beneficiaryRef = beneficiariesCollection.document(beneficiaryId)
            val snapshot = collection.whereEqualTo("idBeneficiario", beneficiaryRef).get().await()
            snapshot.documents.mapNotNull { doc ->
                val deliveryDto = doc.toObject(DeliveryDto::class.java)
                if (deliveryDto != null) {
                    DeliveryMapper.toDomain(doc.id, deliveryDto)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(
                "DeliveryRepositoryImpl",
                "Error getting deliveries by beneficiary: ${e.message}",
                e
            )
            emptyList()
        }
    }

    override suspend fun deleteDelivery(id: String) {
        try {
            collection.document(id).delete().await()
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error deleting delivery: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateDeliveryStatus(id: String, status: DeliveryStatus) {
        try {
            val statusString = when (status) {
                DeliveryStatus.SCHEDULED -> "AGENDADA"
                DeliveryStatus.DELIVERED -> "ENTREGUE"
                DeliveryStatus.CANCELLED -> "CANCELADA"
                DeliveryStatus.REJECTED -> "REJEITADA"
                DeliveryStatus.UNDER_ANALYSIS -> "EM_ANALISE"
            }
            collection.document(id).update("estado", statusString).await()
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error updating delivery status: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateDeliveryItems(id: String, items: Map<String, Int>) {
        try {
            collection.document(id).update("produtosEntregues", items).await()
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error updating delivery items: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateDeliveryDate(id: String, timestamp: Long) {
        try {
            collection.document(id).update("dataHoraPlaneada", timestamp).await()
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error updating delivery date: ${e.message}", e)
            throw e
        }
    }

    override suspend fun updateDeliveryObservations(id: String, observations: String) {
        try {
            collection.document(id).update("observacoes", observations).await()
        } catch (e: Exception) {
            Log.e("DeliveryRepositoryImpl", "Error updating delivery observations: ${e.message}", e)
            throw e
        }
    }
}
