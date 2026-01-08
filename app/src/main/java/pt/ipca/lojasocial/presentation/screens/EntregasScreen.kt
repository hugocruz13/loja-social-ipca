package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
    isCollaborator: Boolean,
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
            AppTopBar(title = "Entregas", onBackClick = onBackClick)
        },
        bottomBar = {
            // IMPLEMENTAÇÃO DO BOTÃO ENCAIXADO (DOCKED FAB)
            AppBottomBar(
                navItems = navItems,
                currentRoute = "entregaslist",
                onItemSelected = { item -> onNavigate(item.route) },
                fabContent = {
                    FloatingActionButton(
                        onClick = onAddClick,
                        containerColor = accentGreen,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nova Entrega")
                    }
                }
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding -> // CORREÇÃO: paddingValues do Scaffold

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()) // Aplica o padding do topo
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Zona de Filtros e Search (Fixa no topo)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AppSearchBar(
                            query = searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = "Procurar entregas...",
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filters.forEach { filter ->
                                FilterChip(
                                    selected = selectedFilter == filter,
                                    onClick = { viewModel.onFilterSelected(filter) },
                                    label = { Text(filter) },
                                    shape = CircleShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = accentGreen,
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFFF1F1F1)
                                    ),
                                    border = null
                                )
                            }
                        }
                    }
                }

                // Conteúdo Principal
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = accentGreen)
                    }
                } else if (error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Erro: $error", color = Color.Red)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 120.dp // Espaço para ver a lista a passar atrás da barra
                        )
                    ) {
                        items(deliveries) { deliveryUiModel ->
                            val statusType = when (deliveryUiModel.delivery.status) {
                                DeliveryStatus.DELIVERED -> StatusType.ENTREGUE
                                DeliveryStatus.SCHEDULED -> StatusType.AGENDADA
                                DeliveryStatus.CANCELLED -> StatusType.NOT_ENTREGUE
                                DeliveryStatus.REJECTED -> StatusType.REJEITADA
                                DeliveryStatus.UNDER_ANALYSIS -> StatusType.ANALISE
                            }

                            val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                .format(Date(deliveryUiModel.delivery.scheduledDate))

                            AppDeliveryDetailCard(
                                deliveryDate = formattedDate,
                                deliveryTitle = "Entrega: ${deliveryUiModel.beneficiaryName}",
                                deliveryContent = deliveryUiModel.delivery.items.keys.joinToString(", "),
                                status = statusType,
                                onEditClick = { onEditDelivery(deliveryUiModel.delivery.id) },
                                showEditButton = isCollaborator && deliveryUiModel.delivery.status != DeliveryStatus.DELIVERED,
                                modifier = Modifier.clickable { onDeliveryClick(deliveryUiModel.delivery.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}