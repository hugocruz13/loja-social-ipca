package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.Delivery
import pt.ipca.lojasocial.domain.models.DeliveryStatus
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AppAccessCard
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppDeliveryDetailCard
import pt.ipca.lojasocial.presentation.components.AppInfoCard
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.CampanhasViewModel
import pt.ipca.lojasocial.presentation.viewmodels.EntregasViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

// Enum para controlar a UI
enum class UserRole { STAFF, BENEFICIARY }

@Composable
fun DashboardScreen(
    userName: String,
    role: UserRole,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: CampanhasViewModel = hiltViewModel(),
    entregasViewModel: EntregasViewModel = hiltViewModel()
) {
    // Carregar dados
    val activeCount by viewModel.activeCount.collectAsState()
    val pendingDeliveriesCount by entregasViewModel.pendingCount.collectAsState()
    val deliveries by entregasViewModel.allDeliveriesForDashboard.collectAsState()

    // Cor Institucional
    val accentGreen = Color(0XFF00713C)

    Scaffold(
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "dashboard",
                onItemSelected = { item -> onNavigate(item.route) },
                fabContent = {
                    FloatingActionButton(
                        onClick = { onNavigate("agendar_entrega?role=beneficiario") },
                        containerColor = accentGreen,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Entrega Espontânea")
                    }
                }

            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (role == UserRole.STAFF) {
                StaffDashboard(
                    userName = userName,
                    activeCount = activeCount,
                    pendingCount = pendingDeliveriesCount,
                    onNavigate = onNavigate
                )
            } else {
                BeneficiaryDashboard(
                    userName = userName,
                    deliveries = deliveries.map { it.delivery }, // Extrair o modelo de domínio
                    onNavigate = onNavigate
                )
            }
        }
    }
}

// =============================================================================
// DASHBOARD DO BENEFICIÁRIO (MODERNA)
// =============================================================================
@Composable
fun BeneficiaryDashboard(
    userName: String,
    deliveries: List<Delivery>,
    onNavigate: (String) -> Unit
) {

    android.util.Log.d("BeneficiaryDashboard", "=== ENTREGAS RECEBIDAS ===")
    android.util.Log.d("BeneficiaryDashboard", "Total de entregas: ${deliveries.size}")
    deliveries.forEachIndexed { index, delivery ->
        android.util.Log.d(
            "BeneficiaryDashboard",
            "Entrega #$index: id=${delivery.id}, status=${delivery.status}, data=${delivery.scheduledDate}"
        )
    }

    val accentGreen = Color(0XFF00713C)

    // Estado para controlar a aba selecionada: 0 = Agendadas, 1 = Histórico
    var selectedTab by remember { mutableIntStateOf(0) }

    // Lógica de Separação das Entregas
    val (upcoming, history) = remember(deliveries) {
        val historyStatus = listOf(
            DeliveryStatus.DELIVERED,
            DeliveryStatus.CANCELLED,
            DeliveryStatus.REJECTED
        )

        val up = deliveries
            .filter { it.status !in historyStatus }
            .sortedBy { it.scheduledDate } // Próximas primeiro

        val hist = deliveries
            .filter { it.status in historyStatus }
            .sortedByDescending { it.scheduledDate } // Mais recentes primeiro

        Pair(up, hist)
    }

    val listToDisplay = if (selectedTab == 0) upcoming else history

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Olá, $userName",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Bem-vindo à Loja Social",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }

            IconButton(
                onClick = { onNavigate("support") },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Apoio ao Cliente",
                    tint = accentGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // --- CALENDÁRIO (Sempre visível para contexto) ---
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "A sua Agenda",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        CompactCalendarView(
                            deliveries = deliveries, // O calendário mostra TUDO (pontos)
                            accentColor = accentGreen
                        )
                    }
                }
            }

            // --- SELETOR DE VISTA (Agendadas vs Histórico) ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DashboardFilterChip(
                        label = "Próximas",
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        accentColor = accentGreen,
                        count = upcoming.size
                    )
                    DashboardFilterChip(
                        label = "Histórico",
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        accentColor = accentGreen,
                        count = history.size
                    )
                }
            }

            // --- LISTA DINÂMICA ---
            if (listToDisplay.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Default.EventBusy else Icons.Default.History,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (selectedTab == 0) "Sem entregas agendadas" else "Sem histórico de entregas",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(listToDisplay) { delivery ->
                    val statusType = when (delivery.status) {
                        DeliveryStatus.DELIVERED -> StatusType.ENTREGUE
                        DeliveryStatus.SCHEDULED -> StatusType.AGENDADA
                        DeliveryStatus.CANCELLED -> StatusType.NOT_ENTREGUE
                        DeliveryStatus.REJECTED -> StatusType.REJEITADA
                        DeliveryStatus.UNDER_ANALYSIS -> StatusType.ANALISE
                    }

                    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(Date(delivery.scheduledDate))

                    // Título inteligente
                    val title = if (selectedTab == 1) "Entrega Realizada" else "Agendada"
                    val content = if (delivery.items.isNotEmpty())
                        delivery.items.keys.take(2)
                            .joinToString(", ") + if (delivery.items.size > 2) "..." else ""
                    else "Produtos diversos"

                    AppDeliveryDetailCard(
                        deliveryDate = formattedDate,
                        deliveryTitle = title,
                        deliveryContent = content,
                        status = statusType,
                        onEditClick = { },
                        showEditButton = false,
                        modifier = Modifier.clickable { onNavigate("entrega_detail/${delivery.id}/beneficiario") }
                    )
                }
            }
        }
    }
}

// --- Componente Auxiliar para os Chips ---
@Composable
fun DashboardFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    count: Int
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) accentColor else Color.White,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        modifier = Modifier.height(40.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else Color(0xFF64748B)
            )
            if (count > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) Color.White.copy(alpha = 0.2f) else Color(
                                0xFFF1F5F9
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (selected) Color.White else Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

// =============================================================================
// COMPONENTE: CALENDÁRIO COMPACTO COM PONTOS
// =============================================================================
@Composable
fun CompactCalendarView(
    deliveries: List<pt.ipca.lojasocial.domain.models.Delivery>,
    accentColor: Color
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val daysInMonth = currentDate.lengthOfMonth()
    val firstDayOfWeek = currentDate.withDayOfMonth(1).dayOfWeek.value // 1 (Mon) - 7 (Sun)
    val monthName = currentDate.month.getDisplayName(TextStyle.FULL, Locale("pt", "PT"))
        .replaceFirstChar { it.uppercase() }
    val year = currentDate.year

    // Converter datas das entregas para LocalDate para comparar
    val deliveryDates = remember(deliveries) {
        deliveries.map {
            Instant.ofEpochMilli(it.scheduledDate)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()
    }

    Column {
        // Navegação Mês
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentDate = currentDate.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color.Gray)
            }
            Text(
                text = "$monthName $year",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = accentColor
            )
            IconButton(onClick = { currentDate = currentDate.plusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dias da Semana
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("S", "T", "Q", "Q", "S", "S", "D").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grelha de Dias
        Column {
            var dayCounter = 1
            // 6 linhas para garantir que cabe qualquer mês
            for (row in 0 until 6) {
                if (dayCounter > daysInMonth) break
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 1..7) {
                        if ((row == 0 && col < firstDayOfWeek) || dayCounter > daysInMonth) {
                            // Espaço vazio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        } else {
                            val thisDay = LocalDate.of(year, currentDate.month, dayCounter)
                            val hasDelivery = deliveryDates.contains(thisDay)
                            val isToday = thisDay == LocalDate.now()

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isToday) accentColor.copy(alpha = 0.1f) else Color.Transparent)
                                    ) {
                                        Text(
                                            text = dayCounter.toString(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isToday) accentColor else Color.Black
                                            )
                                        )
                                    }

                                    // Ponto indicador de entrega
                                    if (hasDelivery) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(accentColor)
                                        )
                                    }
                                }
                            }
                            dayCounter++
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// DASHBOARD DO STAFF (ORIGINAL)
// =============================================================================
@Composable
fun StaffDashboard(
    userName: String,
    activeCount: Int,
    pendingCount: Int,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DashboardHeader(
            userName = userName,
            onNotificationClick = { onNavigate("notification") },
            onProfileClick = { onNavigate("profile") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AppInfoCard("Campanhas Ativas", activeCount.toString(), Icons.Default.Campaign)
            }
            item {
                AppInfoCard(
                    "Entregas Pendentes",
                    pendingCount.toString(),
                    Icons.Default.LocalShipping
                )
            }
            item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                AppAccessCard(
                    "Beneficiários",
                    Icons.Default.Groups,
                    { onNavigate("beneficiaries") })
            }
            item {
                AppAccessCard(
                    "Produtos / Stock",
                    Icons.Default.Inventory,
                    { onNavigate("stock") })
            }
            item {
                AppAccessCard(
                    "Entregas",
                    Icons.Default.LocalShipping,
                    { onNavigate("entregas") })
            }
            item { AppAccessCard("Campanhas", Icons.Default.Campaign, { onNavigate("campanhas") }) }
            item {
                AppAccessCard(
                    "Registo de Atividades",
                    Icons.Default.History,
                    { onNavigate("logs") })
            }
            item {
                AppAccessCard(
                    "Requerimentos",
                    Icons.Default.Assignment,
                    { onNavigate("requerimentos") })
            }
            item {
                AppAccessCard(
                    "Ano Letivo",
                    Icons.Default.DateRange,
                    { onNavigate("ano_letivo") })
            }
        }
    }
}

// Header Comum
@Composable
fun DashboardHeader(
    userName: String,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onProfileClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF003D21)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Bem-vindo de volta",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true, name = "Dashboard Colaborador")
@Composable
fun StaffDashboardPreview() {
    DashboardScreen(
        userName = "João Silva",
        role = UserRole.STAFF,
        navItems = emptyList(),
        onNavigate = {}
    )
}

@Preview(showBackground = true, name = "Dashboard Beneficiário")
@Composable
fun BeneficiaryDashboardPreview() {
    DashboardScreen(
        userName = "Filipe Luís",
        role = UserRole.BENEFICIARY,
        navItems = emptyList(),
        onNavigate = {}
    )
}