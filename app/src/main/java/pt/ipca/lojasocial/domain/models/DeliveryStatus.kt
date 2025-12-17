package pt.ipca.lojasocial.domain.models

enum class DeliveryStatus {
    DELIVERED,      // Entregue
    CANCELLED,      // Cancelada
    SCHEDULED,      // Agendada
    REJECTED,       // Rejeitada
    UNDER_ANALYSIS  // Em Análise
}