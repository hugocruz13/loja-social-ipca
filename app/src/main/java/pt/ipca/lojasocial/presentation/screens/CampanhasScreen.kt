package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AdicionarButton
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppCampanhaCard
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel

// O Modelo tem de ter a propriedade imageUrl
data class CampanhaModel(
    val id: String,
    val nome: String,
    val desc: String,
    val status: StatusType,
    val icon: ImageVector,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val type: pt.ipca.lojasocial.domain.models.CampaignType = pt.ipca.lojasocial.domain.models.CampaignType.INTERNAL,
    val imageUrl: String? = null // <--- IMPORTANTE: Este campo tem de existir
)

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

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Campanhas",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        floatingActionButton = {
            AdicionarButton(onClick = onAddClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                placeholder = "Procurar campanhas",
                modifier = Modifier.padding(16.dp)
            )

            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                AppFilterDropdown(
                    label = "Estado",
                    selectedValue = selectedFilter?.name ?: "",
                    options = statusOptions.map { it.name },
                    leadingIcon = androidx.compose.material.icons.Icons.Default.Tune,
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



            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Adicionei bottom padding para o FAB e a BottomBar não taparem o último item
                contentPadding = PaddingValues(
                    top = 8.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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