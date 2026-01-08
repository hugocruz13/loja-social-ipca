package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppCampanhaCard
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampanhasScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onCampanhaClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val campanhas by viewModel.filteredCampanhas.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedFilter by viewModel.selectedStatusFilter.collectAsState()

    val statusOptions = listOf(
        StatusType.ATIVA,
        StatusType.AGENDADA,
        StatusType.COMPLETA
    )

    val accentGreen = Color(0XFF00713C)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Campanhas",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            // APLICAMOS O BOTÃO ENCAIXADO AQUI
            AppBottomBar(
                navItems = navItems,
                currentRoute = "campanhaslist", // Verifica se esta é a rota no teu NavHost
                onItemSelected = { item -> onNavigate(item.route) },
                fabContent = {
                    FloatingActionButton(
                        onClick = onAddClick,
                        containerColor = accentGreen,
                        contentColor = Color.White,
                        shape = androidx.compose.foundation.shape.CircleShape,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nova Campanha")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA) // Fundo para destacar os cards
    ) { paddingValues ->

        // Box para permitir que a lista passe por trás da barra flutuante
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                // Secção de Filtros Fixa no Topo
                Surface(
                    color = Color.White,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        AppSearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = "Procurar campanhas...",
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        AppFilterDropdown(
                            label = "Estado",
                            selectedValue = selectedFilter?.name ?: "",
                            options = statusOptions.map { it.name },
                            leadingIcon = Icons.Default.Tune,
                            onOptionSelected = { selectedName ->
                                if (selectedName.isEmpty()) {
                                    viewModel.onFilterChange(null)
                                } else {
                                    val status = StatusType.valueOf(selectedName)
                                    viewModel.onFilterChange(status)
                                }
                            },
                            modifier = Modifier.wrapContentWidth()
                        )
                    }
                }

                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = accentGreen
                    )
                }

                // Lista de Campanhas
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 120.dp // Padding extra para o efeito transparente da barra
                    )
                ) {
                    items(campanhas) { item ->
                        AppCampanhaCard(
                            campaignName = item.nome,
                            descricao = item.desc,
                            status = item.status,
                            campaignIcon = item.icon,
                            imageUrl = item.imageUrl,
                            onClick = { onCampanhaClick(item.id) }
                        )
                    }
                }
            }
        }
    }
}