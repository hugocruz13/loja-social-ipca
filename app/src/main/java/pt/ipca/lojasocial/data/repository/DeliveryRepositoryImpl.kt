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
        TODO("Not yet implemented")
    }

    override suspend fun getUpcomingDeliveries(timestampLimit: Long): List<Delivery> {
        TODO("Not yet implemented")
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
            Log.e("DeliveryRepositoryImpl", "Error getting deliveries by beneficiary: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun deleteDelivery(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDeliveryStatus(id: String, status: DeliveryStatus) {
        TODO("Not yet implemented")
    }

    override suspend fun updateDeliveryItems(id: String, items: Map<String, Int>) {
        TODO("Not yet implemented")
    }
}