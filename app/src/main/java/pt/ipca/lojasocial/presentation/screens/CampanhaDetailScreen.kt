package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.models.StockWithProductUiModel
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel

@Composable
fun CampanhaDetailScreen(
    campanhaId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    stockViewModel: StockViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()

    var showAddProductSheet by remember { mutableStateOf(false) }
    var showAddStockDialog by remember { mutableStateOf(false) }
    var showCreateProductDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    val accentGreen = Color(0XFF00713C)

    val stockList by stockViewModel.stockList.collectAsState()
    val products by productViewModel.filteredProducts.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()

    LaunchedEffect(campanhaId) {
        stockViewModel.loadStockByCampaign(campanhaId)
        productViewModel.loadProducts()
    }

    val campaignStock = remember(stockList, campanhaId) {
        stockList.filter { it.campaignId == campanhaId }
    }

    val stockWithProducts = remember(campaignStock, products) {
        campaignStock
            .groupBy { it.productId }
            .mapNotNull { (productId, stocksDoMesmoProduto) ->
                val product = products.find { it.id == productId }
                product?.let {
                    StockWithProductUiModel(
                        stockId = productId,
                        productName = it.name,
                        quantity = stocksDoMesmoProduto.sumOf { it.quantity }
                    )
                }
            }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detalhe Campanha",
                onBackClick = onBackClick
            )
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
                onItemSelected = { item ->
                    onNavigate(item.route)
                }
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
                        Spacer(Modifier.height(8.dp))
                        AppStatusBadge(status = StatusType.ATIVA)
                    }
                }
            }

            DetailSection(title = "Descrição") {
                Text(
                    text = "Esta campanha visa fornecer mantimentos essenciais a famílias vulneráveis.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }

            DetailSection(title = "Timeline") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TimelineItem("Data Início", "01 Dez, 2025")
                    TimelineItem("Data Fim", "10 Jan, 2026")
                }
            }

            SectionWithAdd(
                title = "Produtos Associados",
                onAddClick = {
                    productViewModel.loadProducts()
                    showAddProductSheet = true
                }
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    stockWithProducts.isEmpty() -> {
                        Text(
                            text = "Nenhum produto associado a esta campanha.",
                            color = Color.Gray
                        )
                    }

                    else -> {
                        Column {
                            stockWithProducts.forEachIndexed { index, item ->
                                DeliveryProductItem(
                                    productName = item.productName,
                                    quantity = item.quantity,
                                    unit = "unidades"
                                )

                                if (index < stockWithProducts.lastIndex) {
                                    HorizontalDivider(color = Color(0xFFF1F1F1))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            DetailSection(title = "Associações") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    AssociationItem("Linked to:", "Annual Food Drive")
                    AssociationItem("Partner:", "Global Aid Foundation")
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // 🔹 LISTA DE PRODUTOS
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

        // 🔹 ADICIONAR STOCK
        if (showAddStockDialog && selectedProduct != null) {
            AddStockDialog(
                product = selectedProduct!!,
                campaignId = campanhaId,
                onDismiss = { showAddStockDialog = false },
                onConfirm = { stock ->
                    stockViewModel.addStockItem(stock)
                    showAddStockDialog = false
                }
            )
        }


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
fun SectionWithAdd(
    title: String,
    onAddClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = Color(0XFF00713C)
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
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