package pt.ipca.lojasocial.presentation.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel
import pt.ipca.lojasocial.utils.PdfValidadeService
import java.text.SimpleDateFormat
import java.util.*

// =====================================================================
// MODELOS AUXILIARES (Para agrupar os dados neste ecrã)
// =====================================================================
data class StockBatchUi(
    val stockId: String,
    val quantity: Int,
    val expiryDate: Long
)

data class ProductStockGroup(
    val productId: String,
    val productName: String,
    val photoUrl: String?,
    val totalQuantity: Int,
    val batches: List<StockBatchUi>
)

// =====================================================================
// 1. STATEFUL COMPOSABLE (Lógica + ViewModels)
// =====================================================================
@Composable
fun ProductListScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit, // Agora recebe o ID do Produto ou do Lote
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onAddNewTypeClick: () -> Unit,
    stockViewModel: StockViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val pdfService = remember { PdfValidadeService(context) }

    // Recolha de Estados
    val stockList by stockViewModel.stockList.collectAsState()
    val products by productViewModel.filteredProducts.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()

    // Carregar dados
    LaunchedEffect(Unit) {
        stockViewModel.loadStock()
        productViewModel.loadProducts()
    }

    // --- LÓGICA DE AGRUPAMENTO ---
    // Transforma a lista plana de stocks numa lista agrupada por Produto
    val groupedStockList = remember(stockList, products) {
        stockList.groupBy { it.productId }.mapNotNull { (productId, batches) ->
            val product = products.firstOrNull { it.id == productId } ?: return@mapNotNull null

            val totalQty = batches.sumOf { it.quantity }
            val batchUiList = batches.map {
                StockBatchUi(it.id, it.quantity, it.expiryDate ?: 0L)
            }.sortedBy { it.expiryDate }

            ProductStockGroup(
                productId = product.id,
                productName = product.name,
                photoUrl = product.photoUrl,
                totalQuantity = totalQty,
                batches = batchUiList
            )
        }
    }

    ProductListContent(
        groupedStockList = groupedStockList,
        products = products,
        navItems = navItems,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onBatchClick = onProductClick, // Ao clicar num lote específico
        onNavigate = onNavigate,
        onAddProductClick = onAddProductClick,
        onAddNewTypeClick = onAddNewTypeClick,
        onDownloadReport = {
            // Lógica simplificada para relatório
            val dadosParaRelatorio = stockList.mapNotNull { stock ->
                val produto = products.firstOrNull { it.id == stock.productId }
                if (produto != null) {
                    pt.ipca.lojasocial.domain.models.ItemRelatorioValidade(
                        nomeProduto = produto.name,
                        quantidade = stock.quantity,
                        dataValidade = stock.expiryDate ?: 0L
                    )
                } else null
            }
            if (dadosParaRelatorio.isNotEmpty()) pdfService.gerarRelatorio(dadosParaRelatorio)
            else Toast.makeText(context, "Sem dados para relatório", Toast.LENGTH_SHORT).show()
        },
        onConfirmAddStock = { stock -> stockViewModel.addStockItem(stock) },
        onConfirmCreateProduct = { newProduct, imageUri ->
            productViewModel.addProduct(newProduct, imageUri)
            productViewModel.loadProducts()
        }
    )
}

// =====================================================================
// 2. STATELESS COMPOSABLE (UI Pura)
// =====================================================================
@Composable
fun ProductListContent(
    groupedStockList: List<ProductStockGroup>,
    products: List<Product>,
    navItems: List<BottomNavItem>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onBatchClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onAddNewTypeClick: () -> Unit,
    onDownloadReport: () -> Unit,
    onConfirmAddStock: (pt.ipca.lojasocial.domain.models.Stock) -> Unit,
    onConfirmCreateProduct: (Product, android.net.Uri?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var expandedFab by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf("2024-2025") }
    var selectedStatus by remember { mutableStateOf("") }

    val years = listOf("2023-2024", "2024-2025")
    val statusOptions = listOf("Ativo", "Inativo")

    var showAddProductSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val backgroundColor = Color(0xFFF8F9FA)

    Scaffold(
        topBar = { AppTopBar(title = "Produtos", onBackClick = onBackClick) },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (expandedFab) {
                    ExtendedFloatingActionButton(
                        onClick = { expandedFab = false; onAddNewTypeClick() },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = { Icon(Icons.Default.LibraryAdd, null) },
                        text = { Text("Novo Tipo") }
                    )
                    ExtendedFloatingActionButton(
                        onClick = { expandedFab = false; showAddProductSheet = true },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = { Icon(Icons.Default.Inventory, null) },
                        text = { Text("Registar Stock") }
                    )
                }
                FloatingActionButton(
                    onClick = { expandedFab = !expandedFab },
                    containerColor = Color(0XFF00713C),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(if (expandedFab) Icons.Default.Close else Icons.Default.Add, "Menu")
                }
            }
        },
        bottomBar = { AppBottomBar(navItems, "", { onNavigate(it.route) }) },
        containerColor = backgroundColor
    ) { paddingValues ->

        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Filtros e Pesquisa
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppSearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "Procurar produto")

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    AppFilterDropdown("Ano", selectedYear, years, { selectedYear = it }, Icons.Default.CalendarToday, Modifier.weight(1f))
                    AppFilterDropdown("Status", selectedStatus, statusOptions, { selectedStatus = it }, Icons.Default.Tune, Modifier.weight(1f))
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F2F5)).clickable(onClick = onDownloadReport).padding(12.dp)) {
                        Icon(Icons.Default.Download, "Relatório", tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0XFF00713C)) }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Filtrar e Listar Grupos
                    val filteredList = if (searchQuery.isBlank()) groupedStockList
                    else groupedStockList.filter { it.productName.contains(searchQuery, true) }

                    items(filteredList) { group ->
                        StockListItem(
                            name = group.productName,
                            totalQuantity = group.totalQuantity,
                            imageUrl = group.photoUrl,
                            batches = group.batches,
                            onBatchClick = onBatchClick
                        )
                    }
                }
            }
        }

        // Dialogs
        if (showAddProductSheet) {
            AddProductDialog(products, { showAddProductSheet = false }, { p -> selectedProduct = p; showAddProductSheet = false; showAddStockDialog = true }, { showAddProductSheet = false; showCreateProductDialog = true })
        }
        if (showAddStockDialog && selectedProduct != null) {
            AddStockDialog(selectedProduct!!, null, { showAddStockDialog = false }, { s -> onConfirmAddStock(s); showAddStockDialog = false })
        }
        if (showCreateProductDialog) {
            AddNewProductDialog({ showCreateProductDialog = false }, { np, uri -> onConfirmCreateProduct(np, uri); showCreateProductDialog = false; showAddProductSheet = false })
        }
    }
}

// =====================================================================
// 3. COMPONENTE VISUAL EXPANSÍVEL (StockListItem)
// =====================================================================
@Composable
fun StockListItem(
    name: String,
    totalQuantity: Int,
    imageUrl: String?,
    batches: List<StockBatchUi>,
    onBatchClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "rot")

    // Cores baseadas no TOTAL
    val containerColor = when {
        totalQuantity == 0 -> Color(0xFFFFDAD6) // Vermelho Crítico
        totalQuantity < 5 -> Color(0xFFFFF9C4)  // Amarelo Aviso
        else -> Color.White
    }
    val quantityColor = if (totalQuantity == 0) Color(0xFFB3261E) else Color.Black

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cabeçalho do Produto
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Esquerda: Imagem + Nome
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(45.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.ShoppingCart, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // Direita: Quantidade + Seta
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "$totalQuantity un", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = quantityColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ExpandMore, null, modifier = Modifier.rotate(rotationState), tint = Color.Gray)
                }
            }

            // Lista de Validades (Expandida)
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.03f)).padding(16.dp)
                ) {
                    if (batches.isEmpty()) {
                        Text("Sem lotes registados.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Validades / Lotes:", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
                        batches.forEach { batch ->
                            val dateStr = if (batch.expiryDate > 0) SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(batch.expiryDate)) else "Sem data"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onBatchClick(batch.stockId) }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Val: $dateStr", style = MaterialTheme.typography.bodyMedium)
                                }
                                Text("${batch.quantity} un", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = Color.Black.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// 4. PREVIEW
// =====================================================================
@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    val dummyGroups = listOf(
        ProductStockGroup(
            productId = "1", productName = "Arroz Cigala", photoUrl = null, totalQuantity = 50,
            batches = listOf(StockBatchUi("s1", 20, 1709999999999), StockBatchUi("s2", 30, 1719999999999))
        ),
        ProductStockGroup(
            productId = "2", productName = "Azeite Gallo", photoUrl = null, totalQuantity = 3,
            batches = listOf(StockBatchUi("s3", 3, 1729999999999))
        ),
        ProductStockGroup(
            productId = "3", productName = "Leite Mimosa", photoUrl = null, totalQuantity = 0,
            batches = emptyList()
        )
    )

    MaterialTheme {
        ProductListContent(
            groupedStockList = dummyGroups,
            products = emptyList(),
            navItems = emptyList(),
            isLoading = false,
            onBackClick = {}, onBatchClick = {}, onNavigate = {},
            onAddProductClick = {}, onAddNewTypeClick = {},
            onDownloadReport = {}, onConfirmAddStock = {}, onConfirmCreateProduct = { _, _ -> }
        )
    }
}