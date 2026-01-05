package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.NotificationModel
import pt.ipca.lojasocial.presentation.components.NotificationsList

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    // 1. LISTA SIMULADA / TROCAR PELO BACKEND
    val notifications = remember {
        mutableStateListOf(
            NotificationModel(
                id = 1,
                title = "Estado da entrega 123 alterado para 'Em Progresso'.",
                timestamp = "5m atrás",
                dateLabel = "HOJE",
                icon = Icons.Filled.LocalShipping,
                isUnread = true
            ),
            NotificationModel(
                id = 2,
                title = "Stock de 'Arroz' está baixo.",
                timestamp = "10:30 AM",
                dateLabel = "HOJE",
                icon = Icons.Filled.Description,
                isUnread = false
            ),
            NotificationModel(
                id = 3,
                title = "Definições alteradas.",
                timestamp = "Yesterday",
                dateLabel = "ONTEM",
                icon = Icons.Filled.Settings,
                isUnread = false
            )
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Notificações",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "notification",
                onItemSelected = { item ->
                    onNavigate(item.route)
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NotificationsList(
                notifications = notifications,
                onNotificationClick = { clickedNotification ->
                    val index = notifications.indexOfFirst { it.id == clickedNotification.id }
                    if (index != -1 && notifications[index].isUnread) {
                        notifications[index] = notifications[index].copy(isUnread = false)
                    }
                }
            )
        }
    }
}

