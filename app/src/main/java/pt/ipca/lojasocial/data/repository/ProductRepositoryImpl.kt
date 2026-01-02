package pt.ipca.lojasocial.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.ProductType
import pt.ipca.lojasocial.domain.repository.ProductRepository
import javax.inject.Inject

// A simple DTO for Firestore, assuming these field names
data class ProductDto(
    val name: String = "",
    val type: String = "",
    val photoUrl: String? = null,
    val observations: String? = null,
    val quantity: Int = 0
)

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ProductRepository {

    private val collection = firestore.collection("produtos")

    override suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val dto = doc.toObject(ProductDto::class.java)
                dto?.let {
                    Product(
                        id = doc.id,
                        name = it.name,
                        type = ProductType.valueOf(it.type.uppercase()),
                        photoUrl = it.photoUrl,
                        observations = it.observations,
                        quantity = it.quantity
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ProductRepositoryImpl", "Error getting products: ${e.message}", e)
            emptyList()
        }
    }

    override suspend fun getProductById(id: String): Product? {
        // Implementation for getting a single product
        TODO("Not yet implemented")
    }

    override suspend fun addProduct(product: Product) {
        // Implementation for adding a new product
        TODO("Not yet implemented")
    }
}