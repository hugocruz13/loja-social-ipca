package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Product

/**
 * Interface responsável pela gestão do Catálogo de Produtos.
 *
 * Gere as referências ou definições dos bens aceites pela Loja Social (ex: "Arroz", "Leite").
 *
 * **Distinção Importante:**
 * - Este repositório trata do "O Que é" (Definição).
 * - O [StockRepository] trata do "Quanto há" (Quantidade e Validade).
 *
 * Serve de base para os requisitos de entrada de bens (**RF05**, **RF06**), fornecendo
 * a lista de produtos selecionáveis nos formulários.
 */
interface ProductRepository {

    /**
     * Obtém a lista completa de produtos definidos no catálogo.
     *
     * Frequentemente utilizado para povoar listas de seleção (dropdowns/spinners)
     * ao registar novas entradas de stock ou criar campanhas.
     *
     * @return Lista de [Product].
     */
    suspend fun getProducts(): List<Product>

    /**
     * Obtém os detalhes de um produto específico através do seu ID.
     *
     * @param id O identificador único do produto.
     * @return [Product] se existir no catálogo, ou `null` caso contrário.
     */
    suspend fun getProductById(id: String): Product?

    /**
     * Adiciona uma nova definição de produto ao catálogo.
     *
     * Permite registar novos tipos de bens que a Loja Social passa a aceitar ou gerir
     * (ex: criar uma nova ficha para "Leite em Pó" se ainda não existir no sistema).
     *
     * @param product O objeto [Product] a persistir.
     */
    suspend fun addProduct(product: Product)
}