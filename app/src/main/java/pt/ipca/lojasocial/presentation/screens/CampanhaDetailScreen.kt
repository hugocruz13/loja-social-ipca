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
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.lojasocial.domain.models.StatusType
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel

import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun CampanhaDetailScreen(
    campanhaId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    val campanha by viewModel.selectedCampanha.collectAsState()
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    LaunchedEffect(campanhaId) {
        viewModel.loadCampanhaById(campanhaId)
    }

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
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        }
    ) { paddingValues ->
        if (campanha == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = accentGreen)
            }
        } else {
            val data = campanha!!
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
                        AsyncImage(
                            model = data.imageUrl,
                            contentDescription = "Imagem da campanha",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop,
                            onSuccess = { android.util.Log.d("COIL", "Imagem carregada com sucesso!") },
                            onError = { error ->
                                android.util.Log.e("COIL", "Erro ao carregar: ${error.result.throwable.message}")
                            }
                        )

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = data.nome,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AppStatusBadge(status = data.status)
                        }
                    }
                }

                DetailSection(title = "Descrição") {
                    Text(
                        text = data.desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }

                DetailSection(title = "Timeline") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TimelineItem(
                            label = "Data Início",
                            value = formatLongToString(data.startDate)
                        )
                        TimelineItem(
                            label = "Data Fim",
                            value = formatLongToString(data.endDate)
                        )
                    }
                }

                DetailSection(title = "Produtos Associados") {
                    // Aqui podes futuramente carregar a lista real de produtos do Firebase
                    Column {
                        DeliveryProductItem(productName = "Arroz", quantity = 200, unit = "un")
                        HorizontalDivider(color = Color(0xFFF1F1F1))
                        DeliveryProductItem(productName = "Bolachas", quantity = 150, unit = "un")
                    }
                }

                DetailSection(title = "Associações") {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AssociationItem(
                            label = "Tipo:",
                            value = if (data.type == pt.ipca.lojasocial.domain.models.CampaignType.INTERNAL) "Interna" else "Externa"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
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