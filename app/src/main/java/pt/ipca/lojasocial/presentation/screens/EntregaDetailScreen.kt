package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.navigation.AppScreen

@Composable
fun EntregaDetailScreen(
    entregaId: String,
    userRole: String, // "colaborador" ou "beneficiario"
    onBackClick: () -> Unit,
    onStatusUpdate: (Boolean) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    val isCollaborator = userRole == "colaborador"
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    Scaffold(
        topBar = {
            AppTopBar(title = "Detalhe Entrega", onBackClick = onBackClick)
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = AppScreen.EntregasList.route,
                onItemSelected = { item -> onNavigate(item.route)}
            )
        }
    ) { padding ->
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
            AppStatusBadge(status = if (isCollaborator) StatusType.ATIVA else StatusType.PENDENTE)

            if (isCollaborator) {
                // --- VISÃO COLABORADOR ---

                DetailCardWrapper(title = "Beneficiário") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Maria da Silva", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("ID: 458.123.789-00", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }

                DetailCardWrapper(title = "Data") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("28 de Maio, 2024", fontWeight = FontWeight.Bold)
                            Text("10:00 - 12:00", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }

                DetailCardWrapper(title = "Itens para Entregar") {
                    Column {
                        SimpleProductListItem(
                            productName = "Cesta Básica",
                            quantity = 1,
                            icon = Icons.Filled.Archive
                        )
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        SimpleProductListItem(
                            productName = "Kit de Higiene",
                            quantity = 2,
                            icon = Icons.Filled.Archive
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppButton(
                        text = "Não Entregue",
                        onClick = { onStatusUpdate(false) },
                        modifier = Modifier.weight(1f),
                        containerColor = Color.Red,
                    )
                    AppButton(
                        text = "Entregue",
                        onClick = { onStatusUpdate(true) },
                        modifier = Modifier.weight(1f),
                        containerColor = accentGreen
                    )
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
                            modifier = Modifier.size(32.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)).padding(6.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("ENTREGA PROGRAMADA", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("Data e Horário", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("25 de Outubro de 2024", style = MaterialTheme.typography.bodyMedium)
                        Text("Entre 14:00 e 16:00", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Text("Produtos Inclusos", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        SimpleProductListItem(productName = "Cesta Básica", quantity = 1)
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        SimpleProductListItem(productName = "Kit de Higiene", quantity = 1)
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        SimpleProductListItem(productName = "Botijão de Gás", quantity = 1)
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