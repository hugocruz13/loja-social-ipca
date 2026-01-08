package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppStatusBadge
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.SimpleProductListItem
import pt.ipca.lojasocial.presentation.navigation.AppScreen
import pt.ipca.lojasocial.presentation.viewmodels.EntregaDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntregaDetailScreen(
    entregaId: String,
    userRole: String, // "colaborador" ou "beneficiario"
    onBackClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: EntregaDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isCollaborator = userRole == "colaborador"
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    LaunchedEffect(entregaId) {
        viewModel.loadDelivery(entregaId)
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Detalhe Entrega", onBackClick = onBackClick)
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = AppScreen.EntregasList.route,
                onItemSelected = { item -> onNavigate(item.route) }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = accentGreen)
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Erro: ${uiState.error}", color = Color.Red)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5))
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Badge de Estado
                AppStatusBadge(status = uiState.status)

                if (isCollaborator) {
                    // --- VISÃO COLABORADOR ---

                    DetailCardWrapper(title = "Beneficiário") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.LightGray)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    uiState.beneficiaryName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    uiState.beneficiaryIdDisplay,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    DetailCardWrapper(title = "Data") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(uiState.date, fontWeight = FontWeight.Bold)
                                Text(
                                    uiState.time,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    DetailCardWrapper(title = "Itens para Entregar") {
                        Column {
                            uiState.items.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Imagem do Produto
                                    AsyncImage(
                                        model = item.photoUrl,
                                        contentDescription = item.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.LightGray)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(item.name, modifier = Modifier.weight(1f))
                                    Text("x${item.quantity}", fontWeight = FontWeight.Bold)
                                }
                                if (index < uiState.items.lastIndex) {
                                    Divider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(
                                            alpha = 0.5f
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // AÇÕES (BOTÕES) baseadas no estado
                    when (uiState.status) {
                        StatusType.ANALISE -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AppButton(
                                    text = "Rejeitar",
                                    onClick = { viewModel.rejectDelivery(entregaId) },
                                    modifier = Modifier.weight(1f),
                                    containerColor = Color.Red,
                                )
                                AppButton(
                                    text = "Aprovar",
                                    onClick = { viewModel.approveDelivery(entregaId) },
                                    modifier = Modifier.weight(1f),
                                    containerColor = accentGreen
                                )
                            }
                        }

                        StatusType.PENDENTE, StatusType.AGENDADA, StatusType.ATIVA -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AppButton(
                                    text = "Não Entregue",
                                    onClick = { viewModel.updateStatus(entregaId, false) },
                                    modifier = Modifier.weight(1f),
                                    containerColor = Color.Red,
                                )
                                AppButton(
                                    text = "Entregue",
                                    onClick = { viewModel.updateStatus(entregaId, true) },
                                    modifier = Modifier.weight(1f),
                                    containerColor = accentGreen
                                )
                            }
                        }

                        else -> {
                            // Para estados finais (Entregue, Rejeitada, Cancelada), não mostra botões de ação
                        }
                    }

                } else {
                    // --- VISÃO BENEFICIÁRIO ---

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Icon(
                                imageVector = Icons.Default.EventNote,
                                contentDescription = null,
                                tint = accentGreen,
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                    .padding(6.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "ENTREGA PROGRAMADA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            Text(
                                "Data e Horário",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(uiState.date, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                uiState.time,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        "Produtos Inclusos",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            uiState.items.forEachIndexed { index, item ->
                                SimpleProductListItem(
                                    productName = item.name,
                                    quantity = item.quantity
                                ) // Podíamos atualizar este componente também para aceitar imagem
                                if (index < uiState.items.lastIndex) {
                                    Divider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(
                                            alpha = 0.5f
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailCardWrapper(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true, name = "Visão Colaborador")
@Composable
fun EntregaDetailScreenStaffPreview() {
}

