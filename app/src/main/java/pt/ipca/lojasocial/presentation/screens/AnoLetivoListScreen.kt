package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AdicionarButton
import pt.ipca.lojasocial.presentation.components.AnoLetivoListItem
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem

data class AnoLetivo(
    val id: Int,
    val label: String,
    val isCurrent: Boolean
)

@Composable
fun AnoLetivoListScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onYearClick: (AnoLetivo) -> Unit
) {
    // Lista SIMULADA
    val anosLetivos = listOf(
        AnoLetivo(1, "2024/2025", true),
        AnoLetivo(2, "2023/2024", false),
        AnoLetivo(3, "2022/2023", false),
        AnoLetivo(4, "2021/2022", false)
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Anos Letivos",
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
        floatingActionButton = {
            AdicionarButton(
                onClick = onAddClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Histórico de Anos Letivos",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(anosLetivos) { ano ->
                    AnoLetivoListItem(
                        yearLabel = ano.label,
                        isCurrentYear = ano.isCurrent,
                        onClick = { onYearClick(ano) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnoLetivoListScreenPreview() {
    AnoLetivoListScreen(
        onBackClick = {},
        onAddClick = {},
        onYearClick = {}
    )
}