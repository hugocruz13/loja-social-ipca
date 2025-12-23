package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
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

@Composable
fun CampanhaDetailScreen(
    campanhaId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    Scaffold(
        topBar = {
            AppTopBar(title = "Detalhe Campanha", onBackClick = onBackClick)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditClick(campanhaId) },
                containerColor = accentGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Campanha")
            }
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(Color(0xFFF8F9FA))
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    // Placeholder para a imagem (como na imagem_bc8fcf.png)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.LightGray)
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Super Alimentação",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AppStatusBadge(status = StatusType.ATIVA)
                    }
                }
            }

            DetailSection(title = "Descrição") {
                Text(
                    text = "Esta campanha visa fornecer mantimentos essenciais a famílias vulneráveis, focando-se em bens alimentares e produtos de primeira necessidade.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }

            DetailSection(title = "Timeline") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimelineItem(label = "Data Início", value = "01 Dez, 2025")
                    TimelineItem(label = "Data Fim", value = "10 Jan, 2026")
                }
            }

            DetailSection(title = "Produtos Associados") {
                Column {
                    DeliveryProductItem(
                        productName = "Arroz",
                        quantity = 200,
                        unit = "units"
                    )
                    HorizontalDivider(color = Color(0xFFF1F1F1))

                    DeliveryProductItem(
                        productName = "Papel Higiénico",
                        quantity = 500,
                        unit = "units"
                    )
                    HorizontalDivider(color = Color(0xFFF1F1F1))

                    DeliveryProductItem(
                        productName = "Bolachas",
                        quantity = 150,
                        unit = "units"
                    )
                }
            }

            DetailSection(title = "Associações") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AssociationItem(label = "Linked to:", value = "Annual Food Drive")
                    AssociationItem(label = "Partner:", value = "Global Aid Foundation")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun TimelineItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF1F8F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0XFF00713C))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AssociationItem(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            tint = Color(0XFF00713C),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}