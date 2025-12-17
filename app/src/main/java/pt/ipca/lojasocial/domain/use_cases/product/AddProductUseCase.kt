package pt.ipca.lojasocial.domain.use_cases.product

import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.repository.ProductRepository
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.addProduct(product)
    }
}