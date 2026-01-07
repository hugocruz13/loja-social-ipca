package pt.ipca.lojasocial.domain.use_cases.product

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.repository.ProductRepository
import javax.inject.Inject

/**
 * Caso de Uso responsável pela listagem do Catálogo de Produtos (Definições).
 *
 * Este componente fornece a lista de "tipos" de bens conhecidos pelo sistema.
 *
 * **Contexto de UI:**
 * É frequentemente utilizado para povoar listas de seleção (Dropdowns/Spinners)
 * nos ecrãs de:
 * - Registo de novas entradas de stock (**RF05**).
 * - Criação de campanhas com necessidades específicas (**RF24**).
 */
class GetProductsUseCase @Inject constructor(
    private val repository: ProductRepository
) {

    /**
     * Executa a obtenção da lista completa de produtos definidos.
     *
     * @return Flow<Product?> (ex: "Arroz", "Leite", "Champô").
     */
    operator fun invoke(): Flow<List<Product>> {
        return repository.getProducts()
    }
}