package pt.ipca.lojasocial.presentation.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import pt.ipca.lojasocial.presentation.components.AddNewProductDialog
import pt.ipca.lojasocial.presentation.components.AddProductDialog
import pt.ipca.lojasocial.presentation.components.AddStockDialog
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel
import pt.ipca.lojasocial.utils.PdfValidadeService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// =====================================================================
// MODELOS AUXILIARES
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
    onProductClick: (String) -> Unit,
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
    val filteredProducts by productViewModel.filteredProducts.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()

    val searchQuery by productViewModel.searchQuery.collectAsState()
    val selectedType by productViewModel.selectedType.collectAsState()

    // Carregar dados iniciais
    // Estado local para a SearchBar
    var localSearchQuery by remember { mutableStateOf("") }

    // Carregar dados
    LaunchedEffect(Unit) {
        stockViewModel.loadStock()
        productViewModel.loadProducts()
    }

    // --- LÓGICA DE AGRUPAMENTO (CORRIGIDA) ---

    // 1. Criar um mapa de stock para acesso rápido (Isto retorna um Map<String, List<Stock>>)
    val stockMap = remember(stockList) {
        stockList.groupBy { it.productId }
    }

    // 2. Criar a lista final baseada nos PRODUTOS (Isto retorna List<ProductStockGroup>)
    val groupedStockList = remember(stockMap, filteredProducts) {
        filteredProducts.map { product ->
            // Vai buscar o stock deste produto (ou lista vazia se não houver)
            val productBatches = stockMap[product.id] ?: emptyList()

            val totalQty = productBatches.sumOf { it.quantity }

            val batchUiList = productBatches.map {
                StockBatchUi(it.id, it.quantity, it.expiryDate ?: 0L)
            }.sortedBy { it.expiryDate }

            // Retorna o objeto (a última linha do map é o que conta)
            ProductStockGroup(
                productId = product.id,
                productName = product.name,
                photoUrl = product.photoUrl,
                totalQuantity = totalQty, // Será 0 se productBatches for vazio
                batches = batchUiList
            )
        }
    }

    // Chama a UI Pura passando os dados e as ações
    ProductListContent(
        groupedStockList = groupedStockList,
        products = filteredProducts,
        navItems = navItems,
        isLoading = isLoading,
        searchQuery = localSearchQuery,
        onSearchQueryChange = { newQuery ->
            localSearchQuery = newQuery
            productViewModel.onSearchQueryChange(newQuery)
        },
        onBackClick = onBackClick,
        onBatchClick = onProductClick,
        onNavigate = onNavigate,
        onAddProductClick = onAddProductClick,
        onAddNewTypeClick = onAddNewTypeClick,
        onDownloadReport = {
            val dadosParaRelatorio = stockList.mapNotNull { stock ->
                val produto = filteredProducts.firstOrNull { it.id == stock.productId }
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
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onBatchClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onAddNewTypeClick: () -> Unit,
    onDownloadReport: () -> Unit,
    onConfirmAddStock: (pt.ipca.lojasocial.domain.models.Stock) -> Unit,
    onConfirmCreateProduct: (Product, android.net.Uri?) -> Unit
) {
    val productViewModel: ProductViewModel = hiltViewModel()
    val selectedType by productViewModel.selectedType.collectAsState()

    val typeOptions = listOf("HYGIENE", "FOOD", "CLEANING", "OTHER")

    // Estados locais de UI (Filtros, Dialogs, FAB)
    var searchQuery by remember { mutableStateOf("") }
    var expandedFab by remember { mutableStateOf(false) }

    var showAddProductSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val backgroundColor = Color(0xFFF8F9FA)

    Scaffold(
        topBar = { AppTopBar(title = "Produtos", onBackClick = onBackClick) },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            // Filtros e Pesquisa
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholder = "Procurar produto"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppFilterDropdown(
                        label = "Tipo",
                        selectedValue = selectedType,
                        options = typeOptions,
                        onOptionSelected = { productViewModel.onTypeSelected(it) },
                        leadingIcon = Icons.Default.Tune,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F2F5))
                            .clickable(onClick = onDownloadReport)
                            .padding(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Download,
                            "Relatório",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(color = Color(0XFF00713C)) }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Filtrar e Listar Grupos (Usa a searchQuery que vem do parametro)
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
            AddProductDialog(
                products,
                { showAddProductSheet = false },
                { p ->
                    selectedProduct = p; showAddProductSheet = false; showAddStockDialog = true
                },
                { showAddProductSheet = false; showCreateProductDialog = true })
        }
        if (showAddStockDialog && selectedProduct != null) {
            AddStockDialog(
                selectedProduct!!,
                null,
                { showAddStockDialog = false },
                { s -> onConfirmAddStock(s); showAddStockDialog = false })
        }
        if (showCreateProductDialog) {
            AddNewProductDialog(
                { showCreateProductDialog = false },
                { np, uri ->
                    onConfirmCreateProduct(np, uri); showCreateProductDialog =
                    false; showAddProductSheet = false
                })
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
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "rot"
    )

    val containerColor = when {
        totalQuantity == 0 -> Color(0xFFFFDAD6)
        totalQuantity < 5 -> Color(0xFFFFF9C4)
        else -> Color.White
    }
    val quantityColor = if (totalQuantity == 0) Color(0xFFB3261E) else Color.Black

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cabeçalho
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                                    .crossfade(true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Default.ShoppingCart,
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$totalQuantity un",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = quantityColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ExpandMore,
                        null,
                        modifier = Modifier.rotate(rotationState),
                        tint = Color.Gray
                    )
                }
            }

            // Lista de Validades
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.03f))
                        .padding(16.dp)
                ) {
                    if (batches.isEmpty()) {
                        Text(
                            "Sem stock registado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Red
                        )
                    } else {
                        Text(
                            "Validades / Lotes:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        batches.forEach { batch ->
                            val dateStr = if (batch.expiryDate > 0) SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(Date(batch.expiryDate)) else "Sem data"

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onBatchClick(batch.stockId) }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Val: $dateStr",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    "${batch.quantity} un",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Divider(color = Color.Black.copy(alpha = 0.05f))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductListScreenPreview() {
    MaterialTheme {
        ProductListContent(
            groupedStockList = emptyList(),
            products = emptyList(),
            navItems = emptyList(),
            isLoading = false,
            searchQuery = "",
            onSearchQueryChange = {},
            onBackClick = {}, onBatchClick = {}, onNavigate = {},
            onAddProductClick = {}, onAddNewTypeClick = {},
            onDownloadReport = {}, onConfirmAddStock = {}, onConfirmCreateProduct = { _, _ -> }
        )
    }
}