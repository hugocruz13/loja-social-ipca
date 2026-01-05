package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AppAccessCard
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppInfoCard
import pt.ipca.lojasocial.presentation.components.BottomNavItem

// Definimos um Enum simples para controlar a UI na View
enum class UserRole { STAFF, BENEFICIARY }

@Composable
fun DashboardScreen(
    userName: String,
    role: UserRole,
    onNavigateTo: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            val navItems = listOf(
                BottomNavItem("home", Icons.Filled.Home, "Home"),
                BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
                BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
            )
            AppBottomBar(
                navItems = navItems,
                currentRoute = "home",
                onItemSelected = { /* Navegação */ }
            )
        },
        containerColor = Color(0xFFF8FAFC) // Cor de fundo leve (Off-white)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. Header (Bem-vindo + Notificações)
            DashboardHeader(userName = userName)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Grid de Conteúdo (Cards de Info + Acesso)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // --- SECÇÃO DE INFO (STATS) ---
                if (role == UserRole.STAFF) {
                    item { AppInfoCard("Campanhas Ativas", "5", Icons.Default.Campaign) }
                    item { AppInfoCard("Entregas Pendentes", "12", Icons.Default.LocalShipping) }
                } else {
                    // Beneficiário vê apenas Entregas Pendentes em largura total
                    item(span = { GridItemSpan(2) }) {
                        AppInfoCard("Entregas Pendentes", "12", Icons.Default.LocalShipping)
                    }
                }

                item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(8.dp)) }

                // --- SECÇÃO DE ACESSO RÁPIDO ---
                if (role == UserRole.STAFF) {
                    item {
                        AppAccessCard(
                            "Beneficiários",
                            Icons.Default.Groups,
                            { onNavigateTo("beneficiarios") })
                    }
                    item {
                        AppAccessCard(
                            "Produtos / Stock",
                            Icons.Default.Inventory,
                            { onNavigateTo("stock") })
                    }
                    item {
                        AppAccessCard(
                            "Entregas",
                            Icons.Default.LocalShipping,
                            { onNavigateTo("entregas") })
                    }
                    item {
                        AppAccessCard(
                            "Campanhas",
                            Icons.Default.Campaign,
                            { onNavigateTo("campanhas") })
                    }

                    // --- ALTERAÇÃO AQUI ---
                    // Mudou de Reports para Registo de Atividades
                    item {
                        AppAccessCard(
                            "Registo de Atividades",
                            Icons.Default.History,
                            { onNavigateTo("logs") })
                    }
                    // ---------------------

                    item {
                        AppAccessCard(
                            "Requerimentos",
                            Icons.Default.Assignment,
                            { onNavigateTo("requerimentos") })
                    }
                    item {
                        AppAccessCard(
                            "Ano Letivo",
                            Icons.Default.DateRange,
                            { onNavigateTo("ano_letivo") })
                    }
                } else {
                    item {
                        AppAccessCard(
                            "Entregas",
                            Icons.Default.LocalShipping,
                            { onNavigateTo("entregas") })
                    }
                    item {
                        AppAccessCard(
                            "Canal de Apoio",
                            Icons.Default.SupportAgent,
                            { onNavigateTo("apoio") })
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar Placeholder (Verde escuro como na imagem)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003D21)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Bem-vindo de volta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E293B)
                )
            }
        }

        // Ícone de Notificação com Badge
        BadgedBox(
            badge = {
                Badge(containerColor = Color(0xFFEF4444)) { Text("2") }
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificações",
                tint = Color(0xFF1E293B),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Dashboard Colaborador")
@Composable
fun StaffDashboardPreview() {
    DashboardScreen(userName = "João Silva", role = UserRole.STAFF, onNavigateTo = {})
}

@Preview(showBackground = true, name = "Dashboard Beneficiário")
@Composable
fun BeneficiaryDashboardPreview() {
    DashboardScreen(userName = "Filipe Luís", role = UserRole.BENEFICIARY, onNavigateTo = {})
}