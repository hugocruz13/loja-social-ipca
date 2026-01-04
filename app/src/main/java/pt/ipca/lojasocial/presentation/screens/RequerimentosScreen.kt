package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.RequerimentoListItem
import pt.ipca.lojasocial.presentation.viewmodels.RequerimentosViewModel

@Composable
fun RequerimentosScreen(
    onBackClick: () -> Unit,
    onRequerimentoClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: RequerimentosViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedStatusFilter.collectAsState()
    val requestsList by viewModel.filteredRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Atualiza a lista sempre que o ecrã fica visível (ex: ao voltar do detalhe)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.loadRequests()
    }

    // Define EXATAMENTE os estados que queres no filtro
    val statusOptions = listOf(
        StatusType.ANALISE,
        StatusType.APROVADA,
        StatusType.DOCS_INCORRETOS,
        StatusType.REJEITADA
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Requerimentos",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "requerimentoslist",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. Barra de Pesquisa
            AppSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Procurar por nome...",
                modifier = Modifier.padding(16.dp)
            )

            // 2. Filtro (Dropdown) - Colocado logo abaixo da pesquisa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp) // Alinhado com a search bar
                    .padding(bottom = 16.dp),    // Espaço antes da lista
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppFilterDropdown(
                    label = "Estado",
                    // Se houver filtro, mostra o nome, senão vazio (mostra label)
                    selectedValue = selectedFilter?.name ?: "",
                    // Converte os Enums para String para mostrar na lista
                    options = statusOptions.map { it.name },
                    leadingIcon = Icons.Default.Tune,
                    onOptionSelected = { selectedName ->
                        if (selectedName.isEmpty()) {
                            // Se veio vazio (Limpar), passa null para o ViewModel
                            viewModel.onFilterChange(null)
                        } else {
                            // Converte a String de volta para Enum
                            val status = StatusType.valueOf(selectedName)
                            viewModel.onFilterChange(status)
                        }
                    },
                    modifier = Modifier.wrapContentWidth() // Ocupa apenas o espaço necessário
                )
            }

            // 3. Lista de Resultados
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF00713C)
                    )
                } else if (requestsList.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nenhum resultado encontrado.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (selectedFilter != null || searchQuery.isNotEmpty()) {
                            TextButton(onClick = {
                                viewModel.onFilterChange(null)
                                viewModel.onSearchQueryChange("")
                            }) {
                                Text("Limpar filtros", color = Color(0xFF00713C))
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(requestsList) { req ->
                            RequerimentoListItem(
                                applicantName = req.beneficiaryName,
                                avatarUrl = null,
                                status = req.status, // Passa o StatusType para o item saber a cor
                                onClick = { onRequerimentoClick(req.requestId) }
                            )
                        }
                    }
                }
            }
        }
    }
}