package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import pt.ipca.lojasocial.presentation.components.NotificationModel
import pt.ipca.lojasocial.presentation.components.NotificationsList
import pt.ipca.lojasocial.presentation.components.AppTopBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.tooling.preview.Preview
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit
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
            val navItems = listOf(
                BottomNavItem("home", Icons.Filled.Home, "Home"),
                BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
                BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
            )
            AppBottomBar(
                navItems = navItems,
                currentRoute = "home",
                onItemSelected = { }
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(onBackClick = {})
}