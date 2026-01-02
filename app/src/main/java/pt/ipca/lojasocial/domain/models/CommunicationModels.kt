package pt.ipca.lojasocial.domain.models

// Modelo genérico para Email
data class EmailRequest(
    val to: String,
    val subject: String,
    val body: String,
    val isHtml: Boolean = false,
    val senderName: String? = "Loja Social",
    val replyTo: String? = null
)

// Modelo genérico para Notificação (Push)
data class NotificationRequest(
    val userId: String,      // Quem vai receber (UID)
    val title: String,
    val message: String,
    val type: NotificationType = NotificationType.INFO,
    val data: Map<String, String> = emptyMap() // Dados extra para navegação (ex: abrir pedido X)
)

enum class NotificationType {
    INFO, SUCCESS, WARNING, ERROR
}