package pt.ipca.lojasocial.domain.use_cases.notification

import kotlinx.coroutines.flow.Flow
import pt.ipca.lojasocial.domain.models.Notification
import pt.ipca.lojasocial.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(userId: String): Flow<List<Notification>> {
        return repository.getNotificationsStream(userId)
    }
}