package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BeneficiarioListItem
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.BeneficiariesViewModel

@Composable
fun ListBeneficiariesScreen(
    onNavigateBack: () -> Unit,
    onBeneficiaryClick: (String) -> Unit,
    onAddBeneficiaryClick: () -> Unit, // Adicionei esta callback para o botão flutuante
    viewModel: BeneficiariesViewModel = hiltViewModel()
) {
    // 1. COLETA DE ESTADOS (A UI reage automaticamente a mudanças aqui)
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // IMPORTANTE: Agora observamos o StateFlow 'filteredBeneficiaries' criado no ViewModel
    // Isto garante que a lista atualiza sozinha quando a pesquisa muda.
    val beneficiariesList by viewModel.filteredBeneficiaries.collectAsState()

    // Itens da Bottom Bar
    val navItems = listOf(
        BottomNavItem("home", Icons.Filled.Home, "Home"),
        BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
        BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Beneficiários",
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "home",
                onItemSelected = { /* Navegação futura */ }
            )
        },
        // Botão Flutuante (FAB) para Adicionar Novo Beneficiário
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBeneficiaryClick,
                containerColor = Color(0xFF00713C), // Verde Loja Social
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Beneficiário")
            }
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 2. BARRA DE PESQUISA
            AppSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Nome, Email ou ID..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. FILTROS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filtro de Ano
                // Nota: As options devem corresponder aos IDs que tens no Firebase ("2024_2025")
                AppFilterDropdown(
                    label = "Ano Letivo",
                    selectedValue = selectedYear,
                    options = listOf(
                        "",
                        "2023_2024",
                        "2024_2025",
                        "2025_2026"
                    ), // "" para limpar filtro
                    onOptionSelected = viewModel::onYearSelected,
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )

                // Filtro de Status
                AppFilterDropdown(
                    label = "Estado",
                    selectedValue = selectedStatus,
                    options = listOf("", "Ativo", "Inativo"), // "" para limpar filtro
                    onOptionSelected = viewModel::onStatusSelected,
                    leadingIcon = Icons.Default.Tune,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. ÁREA DE LISTAGEM (Gestão de Estados)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(color = Color(0xFF00713C))
                    }

                    beneficiariesList.isEmpty() -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Nenhum beneficiário encontrado.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                            if (searchQuery.isNotEmpty()) {
                                Text(
                                    text = "Tente alterar os filtros.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 80.dp) // Espaço extra para o FAB não tapar o último item
                        ) {
                            items(beneficiariesList) { beneficiary ->
                                BeneficiarioListItem(
                                    fullName = beneficiary.name,
                                    beneficiaryId = "Nº ${beneficiary.id}", // Exibe o ID
                                    // Podes adicionar mais detalhes ao teu componente BeneficiarioListItem se quiseres
                                    // ex: statusColor = if(beneficiary.status == BeneficiaryStatus.ATIVO) Color.Green else Color.Red
                                    avatarUrl = null,
                                    onClick = { onBeneficiaryClick(beneficiary.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}