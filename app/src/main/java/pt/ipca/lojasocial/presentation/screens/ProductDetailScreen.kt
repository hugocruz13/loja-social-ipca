package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProductDetailScreen(
    productId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    stockViewModel: StockViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    val stock by stockViewModel.selectedStockItem.collectAsState()
    val product by productViewModel.selectedProduct.collectAsState()
    val isLoading by stockViewModel.isLoading.collectAsState()

    LaunchedEffect(productId) {
        stockViewModel.loadStockItemById(productId)
    }

    LaunchedEffect(stock?.productId) {
        stock?.productId?.let { productId ->
            productViewModel.loadProductById(productId)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detalhe Produto",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditClick(productId) },
                containerColor = accentGreen,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar Produto")
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
                    ) {
                        if (product?.photoUrl != null) {
                            AsyncImage(
                                model = product?.photoUrl,
                                contentDescription = product?.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray)
                            )
                        }
                    }


                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = product?.name ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                ProductDetailsListPreview(
                    type = product?.type?.name ?: "-",
                    quantity = "${stock?.quantity ?: 0} Units",
                    lastDelivery = stock?.entryDate?.let { formatDate(it) } ?: "-",
                    expiry = stock?.expiryDate?.let { formatDate(it) } ?: "-",
                    code = product?.id ?: "-",
                    campaign = stock?.campaignId?.takeIf { it.isNotBlank() }
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
