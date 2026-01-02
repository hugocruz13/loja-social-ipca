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
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.*


data class ProductItem(
    val name: String,
    val id: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector

)


@Composable
fun ProductListScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onAddNewTypeClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var selectedYear by remember { mutableStateOf("2024-2025") }
    var selectedStatus by remember { mutableStateOf("") }

    val years = listOf("2023-2024", "2024-2025", "2025-2026")
    val statusOptions = listOf("Ativo", "Inativo", "Pendente")

    val products = listOf(
        ProductItem("Arroz", "B-67890", Icons.Filled.Fastfood),
        ProductItem("Água", "B-24680", Icons.Filled.WaterDrop),
        ProductItem("Bolacha", "B-13579", Icons.Filled.Fastfood)
    )

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
                onBackClick = onBackClick,

            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = androidx.compose.ui.Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (expanded) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            expanded = false
                            onAddNewTypeClick()
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = { Icon(Icons.Default.LibraryAdd, contentDescription = null) },
                        text = { Text("Novo Tipo") }
                    )

                    ExtendedFloatingActionButton(
                        onClick = {
                            expanded = false
                            onAddProductClick()
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                        text = { Text("Registar Stock") }
                    )
                }

                FloatingActionButton(
                    onClick = { expanded = !expanded },
                    containerColor = Color(0XFF00713C),
                    contentColor = Color.White,
                    shape = androidx.compose.foundation.shape.CircleShape
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Menu Adicionar"
                    )
                }
            }
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route)
                }
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Procurar produto"
                )

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

