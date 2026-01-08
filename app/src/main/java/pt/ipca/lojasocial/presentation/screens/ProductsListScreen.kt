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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.Stock
import pt.ipca.lojasocial.presentation.components.AddNewProductDialog
import pt.ipca.lojasocial.presentation.components.AddProductDialog
import pt.ipca.lojasocial.presentation.components.AddStockDialog
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.models.ProductStockGroup
import pt.ipca.lojasocial.presentation.models.StockBatchUi
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel
import pt.ipca.lojasocial.utils.PdfValidadeService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val stockList by stockViewModel.stockList.collectAsState()
    val filteredProducts by productViewModel.filteredProducts.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()
    val searchQuery by productViewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        stockViewModel.loadStock()
        productViewModel.loadProducts()
    }

    val stockMap = remember(stockList) { stockList.groupBy { it.productId } }
    val groupedStockList = remember(stockMap, filteredProducts) {
        filteredProducts.map { product ->
            val productBatches = stockMap[product.id] ?: emptyList()
            ProductStockGroup(
                productId = product.id,
                productName = product.name,
                photoUrl = product.photoUrl,
                totalQuantity = productBatches.sumOf { it.quantity },
                batches = productBatches.map {
                    StockBatchUi(
                        it.id,
                        it.quantity,
                        it.expiryDate ?: 0L
                    )
                }.sortedBy { it.expiryDate }
            )
        }.sortedBy { it.totalQuantity }
    }

    ProductListContent(
        groupedStockList = groupedStockList,
        products = filteredProducts,
        navItems = navItems,
        isLoading = isLoading,
        searchQuery = searchQuery,
        onSearchQueryChange = { productViewModel.onSearchQueryChange(it) },
        onBackClick = onBackClick,
        onBatchClick = onProductClick,
        onNavigate = onNavigate,
        onDownloadReport = {
            val reportData = stockList.mapNotNull { stock ->
                filteredProducts.firstOrNull { it.id == stock.productId }?.let {
                    pt.ipca.lojasocial.domain.models.ItemRelatorioValidade(
                        it.name,
                        stock.quantity,
                        stock.expiryDate ?: 0L
                    )
                }
            }
            if (reportData.isNotEmpty()) pdfService.gerarRelatorio(reportData)
            else Toast.makeText(context, "Sem dados", Toast.LENGTH_SHORT).show()
        },
        onConfirmAddStock = { stockViewModel.addStockItem(it) },
        onConfirmCreateProduct = { np, uri -> productViewModel.addProduct(np, uri) }
    )
}

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
    onDownloadReport: () -> Unit,
    onConfirmAddStock: (Stock) -> Unit,
    onConfirmCreateProduct: (Product, android.net.Uri?) -> Unit
) {
    val productViewModel: ProductViewModel = hiltViewModel()
    val selectedType by productViewModel.selectedType.collectAsState()

    var showAddProductSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Stock e Produtos", onBackClick = onBackClick) },
        bottomBar = {
            // BARRA COM O BOTÃO ACOPLADO
            AppBottomBar(
                navItems = navItems,
                currentRoute = "product_list",
                onItemSelected = { onNavigate(it.route) },
                fabContent = {
                    FloatingActionButton(
                        onClick = { showAddProductSheet = true },
                        containerColor = Color(0XFF00713C),
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.fillMaxSize() // Preenche o Box da AppBottomBar
                    ) {
                        Icon(Icons.Default.Add, "Adicionar Stock")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(Color(0xFFF8F9FA))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Surface(color = Color.White, shadowElevation = 2.dp) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppSearchBar(
                            query = searchQuery,
                            onQueryChange = onSearchQueryChange,
                            placeholder = "Procurar produto..."
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AppFilterDropdown(
                                label = "Tipo",
                                selectedValue = selectedType,
                                options = listOf("HYGIENE", "FOOD", "CLEANING", "OTHER"),
                                onOptionSelected = { productViewModel.onTypeSelected(it) },
                                leadingIcon = Icons.Default.Tune,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                onClick = onDownloadReport,
                                color = Color(0xFFF1F8F5),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Download,
                                        null,
                                        tint = Color(0XFF00713C)
                                    )
                                }
                            }
                        }
                    }
                }

                if (isLoading) {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Color(0XFF00713C)) }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(
                            top = 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 120.dp
                        )
                    ) {
                        items(groupedStockList, key = { it.productId }) { group ->
                            StockListItem(
                                group.productName,
                                group.totalQuantity,
                                group.photoUrl,
                                group.batches,
                                onBatchClick
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddProductSheet) {
        AddProductDialog(
            products,
            { showAddProductSheet = false },
            { p -> selectedProduct = p; showAddProductSheet = false; showAddStockDialog = true },
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
            { np, uri -> onConfirmCreateProduct(np, uri); showCreateProductDialog = false })
    }
}

@Composable
fun StockListItem(
    name: String,
    totalQuantity: Int,
    imageUrl: String?,
    batches: List<StockBatchUi>,
    onBatchClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(if (expanded) 180f else 0f)
    val statusColor =
        if (totalQuantity == 0) Color(0xFFB3261E) else if (totalQuantity < 5) Color(0xFFE2A000) else Color(
            0xFF00713C
        )

    Card(
        colors = CardDefaults.cardColors(containerColor = if (totalQuantity == 0) Color(0xFFFFF0F0) else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    if (!imageUrl.isNullOrBlank()) AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                    else Icon(
                        Icons.Default.Inventory,
                        null,
                        tint = Color.LightGray,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = name, fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
                    Text(
                        text = if (totalQuantity == 0) "Sem stock" else "Stock total",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (totalQuantity == 0) statusColor else Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$totalQuantity",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusColor
                    )
                    Icon(
                        Icons.Default.ExpandMore,
                        null,
                        modifier = Modifier
                            .rotate(rotationState)
                            .size(20.dp),
                        tint = Color.LightGray
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F3F4))
                        .padding(16.dp)
                ) {
                    batches.forEach { batch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onBatchClick(batch.stockId) }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Validade: ${
                                    SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                    ).format(Date(batch.expiryDate))
                                }"
                            )
                            Text("${batch.quantity} un", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}