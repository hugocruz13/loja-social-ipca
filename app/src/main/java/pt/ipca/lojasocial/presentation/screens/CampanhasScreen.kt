package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.*

data class CampanhaModel(
    val id: String,
    val nome: String,
    val desc: String,
    val status: StatusType,
    val icon: ImageVector
)

@Composable
fun CampanhasScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onCampanhaClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val campanhas = listOf(
        CampanhaModel(
            "1", "Campanha Inverno", "Campanha para produtos de limpeza.",
            StatusType.ATIVA, Icons.Filled.CleaningServices
        ),
        CampanhaModel(
            "2", "Campanha Natal Solidário", "Campanha solidária de alimentos e produtos de limpeza natal",
            StatusType.COMPLETA, Icons.Filled.Fastfood
        ),
        CampanhaModel(
            "3", "Volta á Escola", "Suplementos necessários para o ano letivo.",
            StatusType.AGENDADA, Icons.Filled.LocalShipping
        )
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
                onQueryChange = { searchQuery = it },
                placeholder = "Procurar campanhas",
                modifier = Modifier.padding(16.dp)
            )

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

