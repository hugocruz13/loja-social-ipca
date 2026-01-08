package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.models.NotificationUiModel

@Composable
fun AppNotificationItem(
    title: String,
    timestamp: String,
    notificationIcon: ImageVector,
    isUnread: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val iconTint = if (isUnread) Color(0XFF00713C) else MaterialTheme.colorScheme.onSurfaceVariant
    val backgroundTint =
        if (isUnread) Color(0X3000713C) else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(backgroundTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notificationIcon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )

                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isUnread) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0XFF00713C))
                )
            }
        }
    }
}

@Composable
fun NotificationsList(
    notifications: List<NotificationUiModel>, // <--- Tipo Correto
    onNotificationClick: (NotificationUiModel) -> Unit
) {
    val grouped = notifications.groupBy { it.dateLabel }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        grouped.forEach { (label, items) ->
            item {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Graças ao import 'androidx.compose.foundation.lazy.items', isto agora funciona:
            items(items) { notification ->
                AppNotificationItem(
                    title = notification.title,
                    timestamp = notification.timestamp,
                    notificationIcon = notification.icon,
                    isUnread = notification.isUnread,
                    onClick = { onNotificationClick(notification) }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationListFullPreview() {
    // Atualizei o Preview para usar NotificationUiModel e IDs String
    val demoNotifications = listOf(
        NotificationUiModel(
            id = "1",
            title = "Estado da entrega 123 alterado para 'Em Progresso'.",
            timestamp = "5m atrás",
            dateLabel = "HOJE",
            icon = Icons.Filled.LocalShipping,
            isUnread = true
        ),
        NotificationUiModel(
            id = "2",
            title = "Stock de 'Arroz' está baixo.",
            timestamp = "10:30 AM",
            dateLabel = "HOJE",
            icon = Icons.Filled.Description,
            isUnread = false
        ),
        NotificationUiModel(
            id = "3",
            title = "Definições alteradas.",
            timestamp = "Yesterday",
            dateLabel = "ONTEM",
            icon = Icons.Filled.Settings,
            isUnread = false
        )
    )

    Surface(color = MaterialTheme.colorScheme.background) {
        NotificationsList(
            notifications = demoNotifications,
            onNotificationClick = {}
        )
    }
}