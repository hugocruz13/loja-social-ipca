package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.models.DeliveryUiModel
import pt.ipca.lojasocial.presentation.viewmodels.EntregasViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EntregasScreen(
    viewModel: EntregasViewModel,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEditDelivery: (String) -> Unit,
    onDeliveryClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    val deliveries by viewModel.deliveries.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Agendada", "Entregue", "Cancelada", "Rejeitada", "Em Análise")
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

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (error != null) {
                Text(text = "Erro: $error", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(deliveries) { deliveryUiModel ->
                        val statusType = when (deliveryUiModel.delivery.status) {
                            DeliveryStatus.DELIVERED -> StatusType.ENTREGUE
                            DeliveryStatus.SCHEDULED -> StatusType.PENDENTE
                            DeliveryStatus.CANCELLED -> StatusType.NOT_ENTREGUE
                            DeliveryStatus.REJECTED -> StatusType.NOT_ENTREGUE
                            DeliveryStatus.UNDER_ANALYSIS -> StatusType.PENDENTE
                        }
                        val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(deliveryUiModel.delivery.scheduledDate))
                        AppDeliveryDetailCard(
                            deliveryDate = formattedDate,
                            deliveryId = deliveryUiModel.delivery.id,
                            deliveryTitle = "Entrega para ${deliveryUiModel.beneficiaryName}",
                            deliveryContent = deliveryUiModel.delivery.items.keys.joinToString(", "),
                            status = statusType,
                            onEditClick = { onEditDelivery(deliveryUiModel.delivery.id) },
                            modifier = Modifier.clickable { onDeliveryClick(deliveryUiModel.delivery.id) }
                        )
                    }
                }
            }
        }
    }
}