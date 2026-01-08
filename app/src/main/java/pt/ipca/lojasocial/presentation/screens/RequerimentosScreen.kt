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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.RequerimentoListItem
import pt.ipca.lojasocial.presentation.models.RequestUiModel
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

    // 1. Lógica de Inicialização (Forçar filtro ANALISE)
    LaunchedEffect(Unit) {
        if (selectedFilter == null) {
            viewModel.onFilterChange(StatusType.ANALISE)
        }
    }

    // ❌ REMOVIDO: LifecycleEventEffect com loadRequests()
    // Como estamos a usar Flow, a lista atualiza-se sozinha assim que o ecrã abre.

    // 2. Passar dados para a UI
    RequerimentosScreenContent(
        searchQuery = searchQuery,
        selectedFilter = selectedFilter,
        requestsList = requestsList,
        isLoading = isLoading,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterChange = viewModel::onFilterChange,
        onBackClick = onBackClick,
        onRequerimentoClick = onRequerimentoClick,
        navItems = navItems,
        onNavigate = onNavigate
    )
}

@Composable
fun RequerimentosScreenContent(
    searchQuery: String,
    selectedFilter: StatusType?,
    requestsList: List<RequestUiModel>,
    isLoading: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (StatusType?) -> Unit,
    onBackClick: () -> Unit,
    onRequerimentoClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
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
            // --- HEADER: PESQUISA E FILTRO ---
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    AppSearchBar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        placeholder = "Procurar por beneficiário...",
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Filtrar estado:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        AppFilterDropdown(
                            label = "Todos",
                            selectedValue = selectedFilter?.name ?: "Todos",
                            options = statusOptions.map { it.name },
                            leadingIcon = Icons.Default.FilterList,
                            onOptionSelected = { name ->
                                val status = try {
                                    StatusType.valueOf(name)
                                } catch (e: Exception) {
                                    null
                                }
                                onFilterChange(status)
                            },
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
            }

            // --- LISTA DE CONTEÚDO ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (requestsList.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Nenhum requerimento encontrado.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        if (selectedFilter != null || searchQuery.isNotEmpty()) {
                            TextButton(onClick = {
                                onFilterChange(null)
                                onSearchQueryChange("")
                            }) {
                                Text("Limpar Filtros", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(requestsList) { req ->
                            RequerimentoListItem(
                                applicantName = req.beneficiaryName,
                                avatarUrl = null,
                                status = req.status,
                                onClick = {
                                    onRequerimentoClick(req.requestId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}