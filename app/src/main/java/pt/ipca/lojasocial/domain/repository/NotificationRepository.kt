package pt.ipca.lojasocial.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Notification

interface NotificationRepository {
    
    fun getNotificationsStream(userId: String): Flow<List<Notification>>

    suspend fun markAsRead(notificationId: String)
}