package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.RequerimentoListItem
import pt.ipca.lojasocial.presentation.components.StatusType

// Modelo de dados alinhado com o teu componente
data class RequerimentoData(
    val id: String,
    val nome: String,
    val data: String,
    val status: StatusType
)

@Composable
fun RequerimentosScreen(
    onBackClick: () -> Unit,
    onRequerimentoClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    // Lista simulada
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
            val navItems = listOf(
                BottomNavItem("home", Icons.Filled.Home, "Home"),
                BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
                BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
            )
            AppBottomBar(
                navItems = navItems,
                currentRoute = "home",
                onItemSelected = { }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Barra de Pesquisa utilizando o teu componente
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Procurar requerimentos...",
                modifier = Modifier.padding(16.dp)
            )


            // Lista utilizando o teu RequerimentoListItem
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RequerimentosScreenPreview() {
    RequerimentosScreen(onBackClick = {}, onRequerimentoClick = {})
}