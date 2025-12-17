package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun getProductById(id: String): Product?
    suspend fun addProduct(product: Product)
}