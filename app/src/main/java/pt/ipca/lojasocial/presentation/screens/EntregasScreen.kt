package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.*

@Composable
fun EntregasScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditDelivery: (String) -> Unit,
    onDeliveryClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Entregue", "Pendente", "Não Entregue")
    val accentGreen = Color(0XFF00713C)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Entregas",
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
                .padding(horizontal = 16.dp)
        ) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Procurar por item, ID, data, beneficiário",
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = accentGreen,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF1F1F1)
                        ),
                        border = null,
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(getMockEntregas()) { entrega ->
                    AppDeliveryDetailCard(
                        deliveryDate = entrega.date,
                        deliveryId = entrega.id,
                        deliveryTitle = entrega.title,
                        deliveryContent = entrega.items,
                        status = entrega.status,
                        onEditClick = { onEditDelivery(entrega.id) },
                        modifier = Modifier.clickable { onDeliveryClick(entrega.id) }
                    )
                }
            }
        }
    }
}

data class EntregaData(
    val id: String,
    val date: String,
    val title: String,
    val items: String,
    val status: StatusType
)

fun getMockEntregas() = listOf(
    EntregaData("12345", "15 Oct 2023", "Entrega 1", "Pacote Arroz, Água", StatusType.ENTREGUE),
    EntregaData("12342", "10 Oct 2023", "Items para Entrega", "Kit de Higiene", StatusType.PENDENTE),
    EntregaData("12338", "05 Oct 2023", "Items", "Limpeza", StatusType.NOT_ENTREGUE),
    EntregaData("12330", "01 Oct 2023", "Itens Entrega", "Comida", StatusType.ENTREGUE)
)