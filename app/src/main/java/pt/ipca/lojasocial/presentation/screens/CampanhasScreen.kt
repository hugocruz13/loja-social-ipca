package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel

data class CampanhaModel(
    val id: String,
    val nome: String,
    val desc: String,
    val status: StatusType,
    val icon: ImageVector,
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val type: pt.ipca.lojasocial.domain.models.CampaignType = pt.ipca.lojasocial.domain.models.CampaignType.INTERNAL,
    val imageUrl: String? = null
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
                onItemSelected = { item -> onNavigate(item.route)
                }
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

            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(campanhas) { item ->
                    AppCampanhaCard(
                        campaignName = item.nome,
                        descricao = item.desc,
                        status = item.status,
                        campaignIcon = item.icon,
                        onClick = { onCampanhaClick(item.id) }
                    )
                }
            }
        }
    }
}

