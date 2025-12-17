package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus

interface DeliveryRepository {

    // CRUD Básico
    suspend fun getDeliveries(): List<Delivery>
    suspend fun getDeliveryById(id: String): Delivery?
    suspend fun addDelivery(delivery: Delivery)
    suspend fun deleteDelivery(id: String)

    // Gestão de Estado e Itens
    suspend fun updateDeliveryStatus(id: String, status: DeliveryStatus)
    suspend fun updateDeliveryItems(id: String, items: Map<String, Int>)

    // Consultas Específicas
    suspend fun getDeliveriesByBeneficiary(beneficiaryId: String): List<Delivery>

    // Para Notificações e Agendamento
    suspend fun getUpcomingDeliveries(timestampLimit: Long): List<Delivery>
}