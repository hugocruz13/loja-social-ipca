package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.StockItem

/**
 * Interface responsável pela gestão do Inventário Físico (Stock).
 *
 * Gere os lotes reais de produtos existentes no armazém, controlando quantidades,
 * datas de validade e origens (doações individuais ou campanhas).
 *
 * Cobre os requisitos críticos de gestão de entradas (**RF05, RF06**), atualização de stock (**RF07**)
 * e controlo de validades (**RF08, RF09**).
 */
interface StockRepository {

    // --- Básicos ---

    /**
     * Obtém a lista global de todos os lotes de stock existentes.
     *
     * Permite uma visão geral do que existe em armazém.
     *
     * @return Lista de [StockItem].
     */
    suspend fun getStockItems(): List<StockItem>

    /**
     * Obtém os detalhes de um lote específico de stock.
     *
     * @param id O identificador único do lote.
     * @return [StockItem] se encontrado, ou `null` caso contrário.
     */
    suspend fun getStockItemById(id: String): StockItem?

    /**
     * Regista a entrada física de bens no armazém.
     *
     * Cumpre os requisitos **RF05** (Doação individual) e **RF06** (Entrada via Campanha).
     *
     * @param item O objeto [StockItem] contendo a quantidade, validade e origem.
     */
    suspend fun addStockItem(item: StockItem)


    // --- Gestão de Stock e Validades ---

    /**
     * Atualiza a quantidade disponível num lote específico.
     *
     * Cumpre o requisito **RF07** (Atualização automática do stock).
     * Deve ser invocado quando há uma saída de bens (entrega) ou uma correção de inventário.
     *
     * @param id O identificador do lote.
     * @param newQuantity A nova quantidade total.
     */
    suspend fun updateStockQuantity(id: String, newQuantity: Int)

    /**
     * Remove um lote de stock do sistema.
     *
     * Usado tipicamente para correções de erros de lançamento. Para saídas normais de produtos
     * (entregas) ou quebras (lixo), deve-se usar o [updateStockQuantity] ou registar uma quebra.
     *
     * @param id O identificador do lote a remover.
     */
    suspend fun deleteStockItem(id: String)

    /**
     * Devolve os itens cuja validade termina antes de uma certa data limite.
     *
     * Fundamental para cumprir:
     * - **RF09**: Notificações de aviso (1 mês, 15 dias, etc.).
     * - **RF08**: Gestão automática de produtos fora de validade.
     * - **RF25**: Relatórios para priorização de saídas (FEFO - First Expired, First Out).
     *
     * @param timestamp A data limite para a pesquisa.
     * @return Lista de [StockItem] em risco de expirar ou já expirados.
     */
    suspend fun getItemsExpiringBefore(timestamp: Long): List<StockItem>

    /**
     * Identifica todo o stock angariado através de uma campanha específica.
     *
     * Cumpre o requisito **RF06**, permitindo associar entradas a campanhas internas/externas
     * e avaliar o sucesso das mesmas.
     *
     * @param campaignId O identificador da campanha.
     * @return Lista de [StockItem] associados a essa campanha.
     */
    suspend fun getItemsByCampaign(campaignId: String): List<StockItem>
}