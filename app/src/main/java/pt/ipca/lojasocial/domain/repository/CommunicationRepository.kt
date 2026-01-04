package pt.ipca.lojasocial.domain.repository

import pt.ipca.lojasocial.domain.models.EmailRequest
import pt.ipca.lojasocial.domain.models.NotificationRequest

interface CommunicationRepository {
    suspend fun sendEmail(request: EmailRequest)
    suspend fun sendNotification(request: NotificationRequest)

    // Extra: Guardar o Token do telemóvel para receber notificações
    suspend fun saveFcmToken(userId: String, token: String)
}