package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.EditableProductInfoRow
import pt.ipca.lojasocial.presentation.components.ProductInfoRow
import pt.ipca.lojasocial.presentation.viewmodels.ProductViewModel
import pt.ipca.lojasocial.presentation.viewmodels.StockViewModel

@Composable
fun AddEditProductScreen(
    stockId: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    stockViewModel: StockViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    val stock by stockViewModel.selectedStockItem.collectAsState()
    val product by productViewModel.selectedProduct.collectAsState()

    var quantity by remember { mutableStateOf("") }

    LaunchedEffect(stockId) {
        stockViewModel.loadStockItemById(stockId)
    }

    LaunchedEffect(stock) {
        stock?.let {
            quantity = it.quantity.toString()
            productViewModel.loadProductById(it.productId)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Editar Quantidade",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { onNavigate(it.route) }
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
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                    HorizontalDivider()
                    ProductInfoRow(
                        icon = Icons.Filled.Category,
                        label = "Tipo Produto",
                        value = product?.type?.name ?: "-"
                    )

                    HorizontalDivider()
                    EditableProductInfoRow(
                        icon = Icons.Filled.Inventory2,
                        label = "Quantidade",
                        value = quantity,
                        onValueChange = { quantity = it }
                    )

                    HorizontalDivider()
                    ProductInfoRow(
                        icon = Icons.Filled.AccessTimeFilled,
                        label = "Última Entrega",
                        value = stock?.entryDate?.let { formatDate(it) } ?: "-"
                    )

                    HorizontalDivider()
                    ProductInfoRow(
                        icon = Icons.Filled.Event,
                        label = "Validade",
                        value = stock?.expiryDate?.let { formatDate(it) } ?: "-"
                    )

                    HorizontalDivider()
                    ProductInfoRow(
                        icon = Icons.Filled.Numbers,
                        label = "Código Produto",
                        value = product?.id ?: "-"
                    )

                    HorizontalDivider()
                    ProductInfoRow(
                        icon = Icons.Filled.Link,
                        label = "Campanha Associada",
                        value = stock?.campaignId?.takeLast(6) ?: "Nenhuma"
                    )

                    HorizontalDivider()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "Guardar",
                onClick = {
                    val parsedQuantity = quantity.toIntOrNull() ?: return@AppButton
                    stock?.let {
                        stockViewModel.updateStockQuantity(
                            itemId = it.id,
                            newQuantity = parsedQuantity
                        )
                        onSaveClick()
                    }
                },
                containerColor = accentGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
