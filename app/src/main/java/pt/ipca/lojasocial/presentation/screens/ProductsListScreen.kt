package pt.ipca.lojasocial.presentation.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.*

/**
 * Modelo de dados representativo de um Produto na View.
 */
data class ProductItem(
    val name: String,
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

/**
 * Ecrã principal de listagem de produtos.
 * Integra pesquisa, filtragem por ano letivo/status e a lista de bens em stock.
 * O fundo é uniforme para destacar os componentes individuais.
 */
@Composable
fun ProductListScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onAddProductClick: () -> Unit
) {
    // --- ESTADOS DA UI ---
    var searchQuery by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("2024-2025") }
    var selectedStatus by remember { mutableStateOf("") }

    val years = listOf("2023-2024", "2024-2025", "2025-2026")
    val statusOptions = listOf("Ativo", "Inativo", "Pendente")

    // Dados fictícios baseados na imagem de referência
    val products = listOf(
        ProductItem("Arroz", "B-67890", Icons.Filled.Fastfood),
        ProductItem("Água", "B-24680", Icons.Filled.WaterDrop),
        ProductItem("Bolacha", "B-13579", Icons.Filled.Fastfood)
    )

    // Configuração de cores e navegação
    val backgroundColor = Color(0xFFF8F9FA)
    val navItems = listOf(
        BottomNavItem("home", Icons.Filled.Home, "Home"),
        BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
        BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Produtos",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            // Substituído pelo componente personalizado AdicionarButton
            AdicionarButton(
                onClick = onAddProductClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "home",
                onItemSelected = { /* Lógica de navegação */ }
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- SECÇÃO SUPERIOR: PESQUISA E FILTROS ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Componente de Pesquisa
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Procurar produto"
                )

                // Linha de Filtros (Dropdowns lado a lado)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppFilterDropdown(
                        label = "Ano Letivo",
                        selectedValue = selectedYear,
                        options = years,
                        onOptionSelected = { selectedYear = it },
                        leadingIcon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )
                    AppFilterDropdown(
                        label = "Status",
                        selectedValue = selectedStatus,
                        options = statusOptions,
                        onOptionSelected = { selectedStatus = it },
                        leadingIcon = Icons.Default.Tune,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- LISTA DE PRODUTOS ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    AppProductListItem(
                        productName = product.name,
                        productId = product.id,
                        productIcon = product.icon,
                        onClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    ProductListScreen(
        onBackClick = {},
        onProductClick = {},
        onAddProductClick = {}
    )
}