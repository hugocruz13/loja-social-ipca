package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.BeneficiariesViewModel
import pt.ipca.lojasocial.presentation.components.*
// Certifica-te que o import do ViewModel está correto consoante a pasta onde o criaste
// import pt.ipca.lojasocial.presentation.screens.BeneficiariesViewModel

@Composable
fun ListBeneficiariesScreen(
    onNavigateBack: () -> Unit,
    onBeneficiaryClick: (String) -> Unit, // ID do beneficiário
    // MUDANÇA 1: Usar hiltViewModel() para injetar dependências (Repositório)
    viewModel: BeneficiariesViewModel = hiltViewModel()
) {
    // Coleta dos estados do ViewModel
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    // MUDANÇA 2: Observar estado de carregamento
    val isLoading by viewModel.isLoading.collectAsState()

    // Lista filtrada em tempo real
    val beneficiariesList = viewModel.getFilteredList()

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
        containerColor = Color(0xFFF9FAFB) // Fundo cinza claro
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Barra de Pesquisa
            AppSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Procurar por nome ou ID"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Filtro de Ano
                AppFilterDropdown(
                    label = "Ano Letivo",
                    selectedValue = selectedYear,
                    options = listOf("2023-2024", "2024-2025", "2025-2026"),
                    onOptionSelected = viewModel::onYearSelected,
                    leadingIcon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )

                // Filtro de Status
                AppFilterDropdown(
                    label = "Status",
                    selectedValue = selectedStatus,
                    options = listOf("Ativo", "Inativo"), // Deve bater certo com os nomes do Enum ou Strings na BD
                    onOptionSelected = viewModel::onStatusSelected,
                    leadingIcon = Icons.Default.Tune,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Área de Conteúdo (Lógica de Loading vs Lista Vazia vs Lista Cheia)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ocupa o resto do espaço disponível
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        // MUDANÇA 3: Mostrar Spinner enquanto carrega do Firebase
                        CircularProgressIndicator(color = Color(0XFF00713C))
                    }
                    beneficiariesList.isEmpty() -> {
                        // MUDANÇA 4: Mostrar mensagem se não houver dados
                        Text(
                            text = "Nenhum beneficiário encontrado.",
                            color = Color.Gray
                        )
                    }
                    else -> {
                        // Lista de Beneficiários
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(beneficiariesList) { beneficiary ->
                                BeneficiarioListItem(
                                    fullName = beneficiary.name,
                                    beneficiaryId = beneficiary.id,
                                    avatarUrl = null, // Futuramente ligar ao campo fotoURL
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