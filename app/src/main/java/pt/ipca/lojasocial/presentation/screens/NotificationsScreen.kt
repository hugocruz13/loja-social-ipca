package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
// Importa o teu componente de lista (deves atualizá-lo para aceitar o novo modelo)
import pt.ipca.lojasocial.presentation.components.NotificationsList
import pt.ipca.lojasocial.presentation.viewmodels.NotificationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    // 1. Obtém a lista atualizada em tempo real
    val notifications by viewModel.notifications.collectAsState()

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
                currentRoute = "notification", // Verifica se a rota é esta
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            if (notifications.isEmpty()) {
                // Estado Vazio
                Text(
                    text = "Não tens notificações novas.",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Lista de Notificações
                // ATENÇÃO: Vai ao ficheiro NotificationsList.kt e altera o tipo da lista para List<NotificationUiModel>
                NotificationsList(
                    notifications = notifications,
                    onNotificationClick = { notification ->

                        // 1. Marcar como lida
                        if (notification.isUnread) {
                            viewModel.markAsRead(notification.id)
                        }

                        // 2. Navegação Inteligente (Baseada no campo 'screen' do Firestore)
                        if (!notification.screenDestination.isNullOrBlank()) {
                            // Exemplo: se screen="entregas", navega para "entregas_screen"
                            onNavigate(notification.screenDestination)
                        }
                    }
                )
            }
        }
    }
}