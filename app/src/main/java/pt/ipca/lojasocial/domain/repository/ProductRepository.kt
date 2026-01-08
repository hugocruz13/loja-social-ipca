package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Product

/**
 * Interface responsável pela gestão do Catálogo de Produtos.
 *
 * Agora com suporte a **Tempo Real** (Realtime) via Kotlin Flows.
 */
interface ProductRepository {

    /**
     * Obtém um fluxo (Flow) com a lista completa de produtos em tempo real.
     * @return Flow contendo a lista atualizada de [Product].
     */
    fun getProducts(): Flow<List<Product>>

    /**
     * Obtém um fluxo (Flow) com os detalhes de um produto específico.
     * @param id O identificador único do produto.
     * @return Flow que emite [Product] se existir, ou `null` caso contrário.
     */
    fun getProductById(id: String): Flow<Product?>

    /**
     * Adiciona uma nova definição de produto ao catálogo (Operação one-shot).
     */
    suspend fun addProduct(product: Product)
}