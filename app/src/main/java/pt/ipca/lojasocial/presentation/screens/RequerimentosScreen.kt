package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.RequerimentoListItem
import pt.ipca.lojasocial.presentation.components.StatusType

data class RequerimentoData(
    val id: String,
    val nome: String,
    val data: String,
    val status: StatusType
)

@Composable
fun RequerimentosScreen(
    onBackClick: () -> Unit,
    onRequerimentoClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val listaRequerimentos = listOf(
        RequerimentoData("1234", "Maria Joana Sousa", "20/05/2026", StatusType.ANALISE),
        RequerimentoData("1235", "Leonor Ferreira", "21/05/2026", StatusType.ANALISE),
        RequerimentoData("1236", "Marco Costa", "22/05/2026", StatusType.ANALISE)
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
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route)
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Procurar requerimentos...",
                modifier = Modifier.padding(16.dp)
            )


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaRequerimentos) { req ->
                    RequerimentoListItem(
                        applicantName = req.nome,
                        requerimentId = req.id,
                        submissionDate = req.data,
                        avatarUrl = null,
                        status = req.status,
                        onClick = { onRequerimentoClick(req.id) }
                    )
                }
            }
        }
    }
}
