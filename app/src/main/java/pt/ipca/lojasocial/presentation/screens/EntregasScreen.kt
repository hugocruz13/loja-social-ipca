package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AdicionarButton
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppDeliveryDetailCard
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.EntregasViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EntregasScreen(
    viewModel: EntregasViewModel,
    isCollaborator: Boolean, // Novo parâmetro
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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()


    val filters = listOf("All", "Agendada", "Entregue", "Cancelada")
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
            // Botão visível para todos (Colaboradores agendam, Beneficiários pedem)
            AdicionarButton(onClick = onAddClick)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) },
                placeholder = "Procurar por ID, beneficiário, data...",
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
                        onClick = { viewModel.onFilterSelected(filter) },
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
                Text(
                    text = "Erro: $error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(deliveries) { deliveryUiModel ->
                        val statusType = when (deliveryUiModel.delivery.status) {
                            DeliveryStatus.DELIVERED -> StatusType.ENTREGUE
                            DeliveryStatus.SCHEDULED -> StatusType.AGENDADA // Mapeado para AGENDADA
                            DeliveryStatus.CANCELLED -> StatusType.NOT_ENTREGUE
                            DeliveryStatus.REJECTED -> StatusType.REJEITADA // Mapeado para REJEITADA
                            DeliveryStatus.UNDER_ANALYSIS -> StatusType.ANALISE // Mapeado para ANALISE
                        }
                        val formattedDate =
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                                Date(deliveryUiModel.delivery.scheduledDate)
                            )

                        val isDelivered =
                            deliveryUiModel.delivery.status == DeliveryStatus.DELIVERED

                        // Mostra botão editar APENAS se for colaborador E a entrega não tiver sido realizada
                        val canEdit = isCollaborator && !isDelivered

                        AppDeliveryDetailCard(
                            deliveryDate = formattedDate,
                            deliveryId = deliveryUiModel.delivery.id,
                            deliveryTitle = "Entrega para ${deliveryUiModel.beneficiaryName}",
                            deliveryContent = deliveryUiModel.delivery.items.keys.joinToString(", "),
                            status = statusType,
                            onEditClick = { onEditDelivery(deliveryUiModel.delivery.id) },
                            showEditButton = canEdit,
                            modifier = Modifier.clickable { onDeliveryClick(deliveryUiModel.delivery.id) }
                        )
                    }
                }
            }
        }
    }
}
