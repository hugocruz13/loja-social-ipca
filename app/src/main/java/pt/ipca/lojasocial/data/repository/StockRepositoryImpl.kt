package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.StockDto
import pt.ipca.lojasocial.domain.models.Stock
import pt.ipca.lojasocial.domain.repository.StockRepository
import javax.inject.Inject

class StockRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StockRepository {

    private val collection = firestore.collection("bens_inventario")

    override suspend fun getStockItems(): List<Stock> {
        return try {
            val snapshot = collection.get().await()

            snapshot.documents.mapNotNull { doc ->  val dto = doc.toObject(StockDto::class.java)
                dto?.toDomain(doc.id)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getStockItemById(id: String): Stock? {
        return try {
            val doc = collection.document(id).get().await()

            if (doc.exists()) {
                val dto = doc.toObject(StockDto::class.java)
                dto?.toDomain(doc.id)
            } else {
                null
            }

        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addStockItem(item: Stock) {
        try {
            val stockDto = item.toDto()
            collection.document(item.id).set(stockDto).await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateStockQuantity(id: String, newQuantity: Int) {
        try {
            collection
                .document(id)
                .update("quantity", newQuantity)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun deleteStockItem(id: String) {
        try {
            collection
                .document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getItemsExpiringBefore(timestamp: Long): List<Stock> {
        return try {
            val snapshot = collection
                .whereLessThanOrEqualTo("expiryDate", timestamp)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(StockDto::class.java)
                dto?.toDomain(doc.id)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getItemsByCampaign(campaignId: String): List<Stock> {
        return try {
            val snapshot = collection
                .whereEqualTo("campaignId", campaignId)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(StockDto::class.java)
                dto?.toDomain(doc.id)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }
}