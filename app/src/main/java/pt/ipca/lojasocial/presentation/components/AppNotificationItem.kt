package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Settings

@Composable
fun AppNotificationItem(
    title: String,
    timestamp: String,
    notificationIcon: ImageVector,
    isUnread: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val iconTint = if (isUnread) {
        Color(0XFF00713C)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val backgroundTint = if (isUnread) {
        Color(0X3000713C)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = modifier
            .padding(vertical = 4.dp, horizontal = 0.dp)
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
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


@Preview(showBackground = true)
@Composable
fun NotificationListCardsConditionalColorPreview() {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {

        Text("HOJE", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))

        AppNotificationItem(
            title = "Estado da entraga 123 alterado para 'Em Progresso'.",
            timestamp = "5m atrás",
            notificationIcon = Icons.Filled.LocalShipping,
            isUnread = true,
            onClick = {}
        )

        AppNotificationItem(
            title = "Stock de 'Arroz' está baixo.",
            timestamp = "10:30 AM",
            notificationIcon = Icons.Filled.Description,
            isUnread = false,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("ONTEM", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 8.dp))

        AppNotificationItem(
            title = "Definições alteradas.",
            timestamp = "Yesterday",
            notificationIcon = Icons.Filled.Settings,
            isUnread = false,
            onClick = {}
        )
    }
}