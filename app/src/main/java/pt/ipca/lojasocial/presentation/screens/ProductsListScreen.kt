package pt.ipca.lojasocial.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.presentation.components.AddNewProductDialog
import pt.ipca.lojasocial.presentation.components.AddProductDialog
import pt.ipca.lojasocial.presentation.components.AddStockDialog
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppFilterDropdown
import pt.ipca.lojasocial.presentation.components.AppProductListItem
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.models.StockUiModel
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel
import pt.ipca.lojasocial.utils.PdfValidadeService

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

    // Recolha de Estados dos ViewModels
    val stockList by stockViewModel.stockList.collectAsState()
    val products by productViewModel.filteredProducts.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()

    // Carregar dados iniciais
    LaunchedEffect(Unit) {
        stockViewModel.loadStock()
        productViewModel.loadProducts()
    }

    // Processamento de dados (Mapear Stock -> UI Model)
    val stockUiList = remember(stockList, products) {
        stockList.mapNotNull { stock ->
            val product = products.firstOrNull { it.id == stock.productId }
                ?: return@mapNotNull null

            StockUiModel(
                stockId = stock.id,
                productId = product.id,
                productName = product.name,
                quantity = stock.quantity
            )
        }
    }

    // Chama a UI Pura passando os dados e as ações
    ProductListContent(
        stockUiList = stockUiList,
        products = products,
        navItems = navItems,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onProductClick = onProductClick,
        onNavigate = onNavigate,
        onAddProductClick = onAddProductClick,
        onAddNewTypeClick = onAddNewTypeClick,
        // Callbacks de lógica (PDF, Adicionar Stock, Criar Produto)
        onDownloadReport = {
            val dadosParaRelatorio = stockList.mapNotNull { stock ->
                val produto = products.firstOrNull { it.id == stock.productId }
                if (produto != null) {
                    pt.ipca.lojasocial.domain.models.ItemRelatorioValidade(
                        nomeProduto = produto.name,
                        quantidade = stock.quantity,
                        dataValidade = stock.expiryDate ?: 0L
                    )
                } else {
                    null
                }
            }

            if (dadosParaRelatorio.isNotEmpty()) {
                pdfService.gerarRelatorio(dadosParaRelatorio)
            } else {
                Toast.makeText(context, "Sem dados para gerar relatório", Toast.LENGTH_SHORT).show()
            }
        },
        onConfirmAddStock = { stock ->
            stockViewModel.addStockItem(stock)
        },
        onConfirmCreateProduct = { newProduct, imageUri ->
            productViewModel.addProduct(product = newProduct, imageUri = imageUri)
            productViewModel.loadProducts()
        }
    )
}

// =====================================================================
// 2. STATELESS COMPOSABLE (Apenas UI - Sem Hilt)
// =====================================================================
@Composable
fun ProductListContent(
    stockUiList: List<StockUiModel>,
    products: List<Product>,
    navItems: List<BottomNavItem>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onAddNewTypeClick: () -> Unit,
    onDownloadReport: () -> Unit,
    onConfirmAddStock: (pt.ipca.lojasocial.domain.models.Stock) -> Unit,
    onConfirmCreateProduct: (Product, android.net.Uri?) -> Unit
) {
    // Estados locais de UI (Filtros, Dialogs, FAB)
    var searchQuery by remember { mutableStateOf("") }
    var expandedFab by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf("2024-2025") }
    var selectedStatus by remember { mutableStateOf("") }

    val years = listOf("2023-2024", "2024-2025", "2025-2026")
    val statusOptions = listOf("Ativo", "Inativo", "Pendente")

    var showAddProductSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val backgroundColor = Color(0xFFF8F9FA)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Produtos",
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (expandedFab) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            expandedFab = false
                            // Esta ação pode abrir o Sheet localmente ou navegar
                            // Assumindo lógica local baseada no teu código original:
                            showAddProductSheet = true
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                        text = { Text("Registar Stock") }
                    )
                }

                FloatingActionButton(
                    onClick = { expandedFab = !expandedFab },
                    containerColor = Color(0XFF00713C),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (expandedFab) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = "Menu Adicionar"
                    )
                }
            }
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Procurar produto"
                )

                // --- LINHA DE FILTROS E BOTÃO DOWNLOAD ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppFilterDropdown(
                        label = "Ano",
                        selectedValue = selectedYear,
                        options = years,
                        onOptionSelected = { selectedYear = it },
                        leadingIcon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )

                    AppFilterDropdown(
                        label = "Status",
                        selectedValue = selectedStatus,
                        options = statusOptions,
                        onOptionSelected = { selectedStatus = it },
                        leadingIcon = Icons.Default.Tune,
                        modifier = Modifier.weight(1f)
                    )

                    // --- BOTÃO DOWNLOAD ---
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F2F5))
                            .clickable(onClick = onDownloadReport)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Baixar Relatório",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0XFF00713C))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(stockUiList) { product ->
                        AppProductListItem(
                            productName = product.productName,
                            productId = product.stockId,
                            productIcon = Icons.Default.ShoppingCart,
                            onClick = { onProductClick(product.stockId) }
                        )
                    }
                }
            }
        }

        // --- DIALOGS ---

        if (showAddProductSheet) {
            AddProductDialog(
                products = products,
                onDismiss = { showAddProductSheet = false },
                onProductSelected = { product ->
                    selectedProduct = product
                    showAddProductSheet = false
                    showAddStockDialog = true
                },
                onAddProductClick = {
                    showAddProductSheet = false
                    showCreateProductDialog = true
                }
            )
        }

        if (showAddStockDialog && selectedProduct != null) {
            AddStockDialog(
                product = selectedProduct!!,
                campaignId = null,
                onDismiss = { showAddStockDialog = false },
                onConfirm = { stock ->
                    onConfirmAddStock(stock)
                    showAddStockDialog = false
                }
            )
        }

        if (showCreateProductDialog) {
            AddNewProductDialog(
                onDismiss = { showCreateProductDialog = false },
                onConfirm = { newProduct, imageUri ->
                    onConfirmCreateProduct(newProduct, imageUri)
                    showCreateProductDialog = false
                    showAddProductSheet = false // Ou true se quiseres voltar à lista
                }
            )
        }
    }
}

// =====================================================================
// 3. PREVIEW (Agora funciona porque chama o Stateless Content)
// =====================================================================
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductListScreenPreview() {

    // Dados fictícios para o preview
    val dummyStock = listOf(
        StockUiModel("1", "p1", "Arroz Cigala", 50),
        StockUiModel("2", "p2", "Azeite Gallo", 20),
        StockUiModel("3", "p3", "Leite Mimosa", 100)
    )

    MaterialTheme {
        ProductListContent(
            stockUiList = dummyStock,
            products = emptyList(),
            isLoading = false,
            onBackClick = { },
            onProductClick = { },
            onNavigate = { },
            onAddProductClick = { },
            onAddNewTypeClick = { },
            onDownloadReport = { },
            onConfirmAddStock = { },
            onConfirmCreateProduct = { _, _ -> },
            navItems = emptyList()
        )
    }
}