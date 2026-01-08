package pt.ipca.lojasocial.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.data.mapper.toDomain
import pt.ipca.lojasocial.data.mapper.toDto
import pt.ipca.lojasocial.data.remote.dto.ProductDto
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val collection = firestore.collection("bens")
    private val TAG = "ProductRepo"

    // Função auxiliar para gerar IDs
    private fun generateProductId(name: String): String {
        val normalized = java.text.Normalizer
            .normalize(name.trim(), java.text.Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .replace(Regex("[^a-z0-9 ]"), "")
            .replace(Regex("\\s+"), "_")

        return "bens_$normalized"
    }

    //Obter produtos
    override fun getProducts(): Flow<List<Product>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = collection
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e(TAG, "Erro ao escutar produtos: ${exception.message}")
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(ProductDto::class.java)?.toDomain(doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(products)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    //Obter produto por ID
    override fun getProductById(id: String): Flow<Product?> = callbackFlow {
        val listenerRegistration = collection.document(id)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    try {
                        val product =
                            snapshot.toObject(ProductDto::class.java)?.toDomain(snapshot.id)
                        trySend(product)
                    } catch (e: Exception) {
                        trySend(null)
                    }
                } else {
                    trySend(null)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    // --- ESCRITA ---
    override suspend fun addProduct(product: Product) {
        try {
            val dto = product.toDto()

            val documentId = if (product.id.isBlank() || product.id.length > 30)
                generateProductId(product.name)
            else product.id

            collection.document(documentId).set(dto).await()
        } catch (e: Exception) {
            throw e
        }
    }
}