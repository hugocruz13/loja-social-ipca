package pt.ipca.lojasocial.domain.use_cases.product

import android.net.Uri
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.repository.ProductRepository
import pt.ipca.lojasocial.domain.repository.StorageRepository
import java.util.UUID
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val productRepository: ProductRepository,
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(
        product: Product,
        imageUri: Uri?
    ) {
        val imageUrl = if (imageUri != null) {
            val fileName = "bens/${product.id}_${UUID.randomUUID()}"
            storageRepository.uploadFile(imageUri, fileName)
        } else {
            null
        }

        val newProduct = product.copy(
            photoUrl = imageUrl
        )

        productRepository.addProduct(newProduct)
    }
}