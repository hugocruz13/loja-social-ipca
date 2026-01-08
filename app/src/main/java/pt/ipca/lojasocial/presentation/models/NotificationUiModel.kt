package pt.ipca.lojasocial.presentation.models

import androidx.compose.ui.graphics.vector.ImageVector

data class NotificationUiModel(
    val id: String,
    val title: String,
    val timestamp: String,
    val dateLabel: String,
    val icon: ImageVector,
    val isUnread: Boolean,
    val screenDestination: String? = null
)