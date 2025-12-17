package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.StockItem

interface StockRepository {
    // Básicos
    suspend fun getStockItems(): List<StockItem>
    suspend fun getStockItemById(id: String): StockItem?
    suspend fun addStockItem(item: StockItem)

    // Gestão de Stock e Validades
    suspend fun updateStockQuantity(id: String, newQuantity: Int)
    suspend fun deleteStockItem(id: String)

    // Devolve itens cuja validade termina antes de uma certa data (timestamp)
    suspend fun getItemsExpiringBefore(timestamp: Long): List<StockItem>

    // Saber stock vindo de uma campanha específica
    suspend fun getItemsByCampaign(campaignId: String): List<StockItem>
}