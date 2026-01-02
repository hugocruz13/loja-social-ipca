package pt.ipca.lojasocial.data.repository

import com.google.firebase.firestore.FirebaseFirestore
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

    private fun generateProductId(name: String): String {
        return "bem_" + name
            .lowercase()
            .trim()
    }


    /**
     * Obtém todos os produtos.
     * Mapeia cada documento para o objeto de domínio Product.
     */
    override suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(ProductDto::class.java)?.toDomain(doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Procura um produto específico pelo ID (ex: código de barras ou UUID).
     */
    override suspend fun getProductById(id: String): Product? {
        return try {
            val doc = collection.document(id).get().await()
            if (doc.exists()) {
                doc.toObject(ProductDto::class.java)?.toDomain(doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    /**
     * Adiciona um produto ao catálogo.
     * O ID do documento é gerado automaticamente baseado no nome do produto.
     */
    override suspend fun addProduct(product: Product) {
        try {
            val dto = product.toDto()
            val documentId = generateProductId(product.name)
            collection.document(documentId).set(dto).await()

        } catch (e: Exception) {
            throw e
        }
    }
}