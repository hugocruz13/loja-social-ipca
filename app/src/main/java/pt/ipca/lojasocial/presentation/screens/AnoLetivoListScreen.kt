package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AdicionarButton
import pt.ipca.lojasocial.presentation.components.AnoLetivoListItem
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.AnosLetivosViewModel

data class AnoLetivo(
    val id: String,
    val label: String,
    val isCurrent: Boolean
)

@Composable
fun AnoLetivoListScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onYearClick: (AnoLetivo) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: AnosLetivosViewModel = hiltViewModel()
) {
    val anosLetivos by viewModel.anosLetivos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Anos Letivos",
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
            AdicionarButton(
                onClick = onAddClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Histórico de Anos Letivos",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(anosLetivos) { ano ->
                        AnoLetivoListItem(
                            yearLabel = ano.label,
                            isCurrentYear = ano.isCurrent,
                            onClick = { onYearClick(ano) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnoLetivoListScreenPreview() {
    // Criação de dados fictícios para a navegação
    // Certifica-te que a classe BottomNavItem aceita estes parâmetros
    val dummyNavItems = listOf(
        BottomNavItem("Início", Icons.Default.Home, "home"),
        BottomNavItem("Perfil", Icons.Default.Person, "profile"),
        BottomNavItem("Definições", Icons.Default.Settings, "settings")
    )

    // Se tiveres um tema personalizado (ex: LojaSocialTheme),
    // envolve o AnoLetivoListScreen nele.
    MaterialTheme {
        AnoLetivoListScreen(
            onBackClick = { /* Ação de voltar vazia para preview */ },
            onAddClick = { /* Ação de adicionar vazia */ },
            onYearClick = { /* Ação de clique no ano vazia */ },
            navItems = dummyNavItems,
            onNavigate = { /* Navegação vazia */ }
        )
    }
}

