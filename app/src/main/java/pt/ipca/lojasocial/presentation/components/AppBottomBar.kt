package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun AppBottomBar(
    navItems: List<BottomNavItem>,
    currentRoute: String?,
    onItemSelected: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Definição das Cores dos Itens (Selecionado/Não Selecionado)
    val itemColors = NavigationBarItemDefaults.colors(
        // Cor do Ícone/Texto selecionado (usamos a cor primária do tema)
        selectedIconColor = Color(0XFF00713C),
        selectedTextColor = Color(0XFF00713C),

        // Cor do Ícone/Texto não selecionado (usamos cinza)
        unselectedIconColor = Color(0XFF64748B),
        unselectedTextColor = Color(0XFF64748B),

        indicatorColor = Color.White
    )

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = { onItemSelected(item) },
                colors = itemColors
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    val navItems = listOf(
        BottomNavItem("home", Icons.Filled.Home, "Home"),
        BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
        BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
    )

    Scaffold(
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "notifications",
                onItemSelected = { }
            )
        }
    ) { paddingValues ->
        Text(text = "Ecrã", modifier = Modifier.padding(paddingValues))
    }
}