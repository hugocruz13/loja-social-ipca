package pt.ipca.lojasocial.presentation.viewmodels

import android.text.format.DateUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.ipca.lojasocial.domain.repository.AuthRepository
import pt.ipca.lojasocial.domain.use_cases.notification.GetNotificationsUseCase
import pt.ipca.lojasocial.domain.use_cases.notification.MarkNotificationReadUseCase
import pt.ipca.lojasocial.presentation.models.NotificationUiModel
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationReadUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Agora usamos a lista de NotificationUiModel
    private val _notifications = MutableStateFlow<List<NotificationUiModel>>(emptyList())
    val notifications: StateFlow<List<NotificationUiModel>> = _notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                // O collect mantém a ligação em tempo real ao Firestore
                getNotificationsUseCase(user.id).collect { domainList ->

                    // CONVERSÃO: Domínio (Notification) -> UI (NotificationUiModel)
                    val uiList = domainList.map { item ->
                        NotificationUiModel(
                            id = item.id,
                            title = item.title,
                            timestamp = getRelativeTime(item.date.time),
                            dateLabel = getDateLabel(item.date.time),
                            icon = getIconForScreen(item.screenDestination),
                            isUnread = item.readAt == null,
                            screenDestination = item.screenDestination
                        )
                    }
                    _notifications.value = uiList
                }
            }
        }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            markNotificationAsReadUseCase(id)
        }
    }

    // --- Helpers de Formatação ---

    private fun getIconForScreen(screen: String?): ImageVector {
        return when (screen) {
            "entregas" -> Icons.Default.LocalShipping
            "stock" -> Icons.Default.Description
            "aprovado" -> Icons.Default.CheckCircle
            else -> Icons.Default.Notifications
        }
    }

    private fun getRelativeTime(time: Long): String {
        // Ex: "Há 5 min", "Ontem"
        return DateUtils.getRelativeTimeSpanString(
            time,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    private fun getDateLabel(time: Long): String {
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return if (DateUtils.isToday(time)) "HOJE" else sdf.format(time).uppercase()
    }
}