package pt.ipca.lojasocial.domain.use_cases.product

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável por obter a definição detalhada de um produto do catálogo.
 *
 * **Contexto de Utilização:**
 * Embora pareça simples, este caso de uso é vital para "traduzir" os IDs de produtos
 * encontrados nas listas de Stock ou Entregas em informação legível (Nome, Foto, Tipo).
 *
 * Exemplo: Ao listar o stock, a entidade [StockItem] tem apenas `productId="123"`.
 * Este Use Case permite ir buscar Flow<Product?> correspondente para mostrar "Arroz Agulha" na UI.
 */
class GetProductByIdUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    /**
     * Executa a pesquisa do produto no catálogo pelo seu identificador.
     *
     * @param id O identificador único do produto (definição).
     * @return Flow<Product?> com os detalhes (nome, foto, etc.) ou `null` se não existir.
     */
    operator fun invoke(id: String): Flow<Product?> {
        return repository.getProductById(id)
    }
}