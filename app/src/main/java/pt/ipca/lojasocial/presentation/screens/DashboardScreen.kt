package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
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
import androidx.hilt.navigation.compose.hiltViewModel // Para injetar o ViewModel
import androidx.compose.runtime.collectAsState     // Para ler o valor dinâmico
import androidx.compose.runtime.getValue           // Para usar "by"
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel // O teu ViewModel
import pt.ipca.lojasocial.presentation.viewmodels.EntregasViewModel

// Definimos um Enum simples para controlar a UI na View
enum class UserRole { STAFF, BENEFICIARY }

@Composable
fun DashboardScreen(
    userName: String,
    role: UserRole,
    navItems: List<BottomNavItem>, // <--- Adicionado
    onNavigate: (String) -> Unit,   // <--- Renomeado para padronizar
    viewModel: CampanhasViewModel = hiltViewModel(),
    entregasViewModel: EntregasViewModel = hiltViewModel()
) {
    val activeCount by viewModel.activeCount.collectAsState()
    val pendingDeliveriesCount by entregasViewModel.pendingCount.collectAsState()
    Scaffold(
        bottomBar = {
            AppBottomBar(
                navItems = navItems, // <--- Passa a lista recebida
                currentRoute = "dashboard", // Rota fixa deste ecrã
                onItemSelected = { item -> onNavigate(item.route) } // <--- Conecta a navegação
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. Header (Bem-vindo + Notificações)
            DashboardHeader(
                userName = userName,
                onNotificationClick = { onNavigate("notification") },
                onProfileClick = { onNavigate("profile") }
            )

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
                    item { AppInfoCard ("Campanhas Ativas", activeCount.toString(), Icons.Default.Campaign)}
                    item {AppInfoCard("Entregas Pendentes", pendingDeliveriesCount.toString(), Icons.Default.LocalShipping )}
                } else {
                    // Beneficiário vê apenas Entregas Pendentes em largura total
                    item(span = { GridItemSpan(2) }) {
                        AppInfoCard("Entregas Pendentes", pendingDeliveriesCount.toString(), Icons.Default.LocalShipping)
                    }
                }

                item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(8.dp)) }

                // --- SECÇÃO DE ACESSO RÁPIDO ---
                if (role == UserRole.STAFF) {
                    item {
                        AppAccessCard(
                            "Beneficiários",
                            Icons.Default.Groups,
                            { onNavigate("beneficiaries") })
                    }
                    item {
                        AppAccessCard(
                            "Produtos / Stock",
                            Icons.Default.Inventory,
                            { onNavigate("stock") })
                    }
                    item {
                        AppAccessCard(
                            "Entregas",
                            Icons.Default.LocalShipping,
                            { onNavigate("entregas") })
                    }
                    item {
                        AppAccessCard(
                            "Campanhas",
                            Icons.Default.Campaign,
                            { onNavigate("campanhas") })
                    }

                    item {
                        AppAccessCard(
                            "Registo de Atividades",
                            Icons.Default.History,
                            { onNavigate("logs") })
                    }

                    item {
                        AppAccessCard(
                            "Requerimentos",
                            Icons.Default.Assignment,
                            { onNavigate("requerimentos") })
                    }
                    item {
                        AppAccessCard(
                            "Ano Letivo",
                            Icons.Default.DateRange,
                            { onNavigate("ano_letivo") })
                    }
                } else {
                    item {
                        AppAccessCard(
                            "Entregas",
                            Icons.Default.LocalShipping,
                            { onNavigate("entregas") })
                    }
                    item {
                        AppAccessCard(
                            "Canal de Apoio",
                            Icons.Default.SupportAgent,
                            { onNavigate("apoio") })
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardHeader(
    userName: String,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() } // Atalho para perfil
        ) {
            // Avatar Placeholder
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
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Dashboard Colaborador")
@Composable
fun StaffDashboardPreview() {
    DashboardScreen(
        userName = "João Silva",
        role = UserRole.STAFF,
        navItems = emptyList(),
        onNavigate = {}
    )
}

@Preview(showBackground = true, name = "Dashboard Beneficiário")
@Composable
fun BeneficiaryDashboardPreview() {
    DashboardScreen(
        userName = "Filipe Luís",
        role = UserRole.BENEFICIARY,
        navItems = emptyList(),
        onNavigate = {}
    )
}