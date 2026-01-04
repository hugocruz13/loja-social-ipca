package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.models.StockUiModel
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel
import pt.ipca.lojasocial.utils.PdfValidadeService


@Composable
fun ProductListScreen(
    onBackClick: () -> Unit,
    onProductClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    onDownloadReportClick: () -> Unit,
    stockViewModel: StockViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val pdfService = remember { PdfValidadeService(context) }

    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    var selectedYear by remember { mutableStateOf("2024-2025") }
    var selectedStatus by remember { mutableStateOf("") }

    val years = listOf("2023-2024", "2024-2025", "2025-2026")
    val statusOptions = listOf("Ativo", "Inativo", "Pendente")

    var showAddProductSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }

    var selectedProduct by remember { mutableStateOf<pt.ipca.lojasocial.domain.models.Product?>(null) }

    val backgroundColor = Color(0xFFF8F9FA)

    val stockList by stockViewModel.stockList.collectAsState()
    val products by productViewModel.filteredProducts.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        stockViewModel.loadStock()
        productViewModel.loadProducts()
    }

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

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Produtos",
                onBackClick = onBackClick,

            )
        },
        floatingActionButton = {
            AdicionarButton(
                onClick = {
                    productViewModel.loadProducts()
                    showAddProductSheet = true
                }
            )
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
                    // Alinhamento vertical importante para ficarem todos na mesma linha base
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Dropdown Ano
                    AppFilterDropdown(
                        label = "Ano",
                        selectedValue = selectedYear,
                        options = years,
                        onOptionSelected = { selectedYear = it },
                        leadingIcon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )

                    // Dropdown Status
                    AppFilterDropdown(
                        label = "Status",
                        selectedValue = selectedStatus,
                        options = statusOptions,
                        onOptionSelected = { selectedStatus = it },
                        leadingIcon = Icons.Default.Tune,
                        modifier = Modifier.weight(1f)
                    )

                    // --- BOTÃO DOWNLOAD (Estilo "Dropbox" igual aos filtros) ---
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp)) // Mesmo arredondamento
                            .background(Color(0xFFF0F2F5)) // Mesma cor de fundo cinza
                            .clickable(onClick = {

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
                                    android.widget.Toast.makeText(context, "Sem dados para gerar relatório", android.widget.Toast.LENGTH_SHORT).show()
                                }

                                onDownloadReportClick()
                            })
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Baixar Relatório",
                            tint = Color.Black, // Mesma cor de ícone (Preto)
                            modifier = Modifier.size(20.dp) // Mesmo tamanho de ícone (20.dp)
                        )
                    }
                    // -----------------------------------------------------------
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
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

        // LISTA DE PRODUTOS
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

        // ADICIONAR STOCK
        if (showAddStockDialog && selectedProduct != null) {
            AddStockDialog(
                product = selectedProduct!!,
                campaignId = null,
                onDismiss = { showAddStockDialog = false },
                onConfirm = { stock ->
                    stockViewModel.addStockItem(stock)
                    showAddStockDialog = false
                }
            )
        }

        // CRIAR PRODUTO
        if (showCreateProductDialog) {
            AddNewProductDialog(
                onDismiss = { showCreateProductDialog = false },
                onConfirm = { newProduct, imageUri ->
                    productViewModel.addProduct(
                        product = newProduct,
                        imageUri = imageUri
                    )
                    productViewModel.loadProducts()
                    showCreateProductDialog = false
                    showAddProductSheet = false
                }
            )
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProductListScreenPreview() {
    val dummyNavItems = listOf(
        BottomNavItem("home", Icons.Filled.Home, "Home"),
        BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
        BottomNavItem("settings", Icons.Filled.Settings, "Configurações")
    )

    MaterialTheme {
        ProductListScreen(
            onBackClick = { },
            onProductClick = { },
            onDownloadReportClick = { },
            navItems = dummyNavItems,
            onNavigate = { }
        )
    }
}