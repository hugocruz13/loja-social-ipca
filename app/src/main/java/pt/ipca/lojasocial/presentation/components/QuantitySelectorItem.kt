package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.CircleShape

@Composable
fun QuantitySelectorItem(
    product: DeliveryProduct,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit,
    showDivider: Boolean,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val accentGreen = Color(0XFF00713C)
    val lightGreen = Color(0xFFE0F0E6)
    val redColor = Color(0xFFE53935)

    Card(
        modifier = modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { if (product.quantity > 1) onQuantityChange(product.quantity - 1) },
                    enabled = product.quantity > 1,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0XFFF0F0F5))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Diminuir Quantidade",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = product.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                IconButton(
                    onClick = { onQuantityChange(product.quantity + 1) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0XFFBFDBCE))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Aumentar Quantidade",
                        tint = accentGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remover Produto",
                        tint = redColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (showDivider) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}
@Composable
fun DeliveryProductHeader(onAddProductClick: () -> Unit, modifier: Modifier = Modifier.fillMaxWidth()) {
    val accentGreen = Color(0XFF00713C)
    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Produtos para Entrega", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        Row(modifier = Modifier.clickable(onClick = onAddProductClick), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Adicionar Produto", tint = accentGreen, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Add Produto", style = MaterialTheme.typography.titleMedium, color = accentGreen)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DeliveryProductManagerOptimizedPreview() {
    val initialProducts = listOf(
        DeliveryProduct("1", "Comida Básica", 1),
        DeliveryProduct("2", "Kit Limpeza", 2)
    )
    val productList = remember { mutableStateListOf(*initialProducts.toTypedArray()) }

    fun updateQuantity(id: String, newQuantity: Int) {
        val index = productList.indexOfFirst { it.id == id }
        if (index != -1) {
            productList[index] = productList[index].copy(quantity = newQuantity)
        }
    }

    fun removeProduct(id: String) {
        productList.removeAll { it.id == id }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            DeliveryProductHeader(
                onAddProductClick = {  }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    productList.forEachIndexed { index, product ->
                        QuantitySelectorItem(
                            product = product,
                            onQuantityChange = { newQty -> updateQuantity(product.id, newQty) },
                            onRemove = { removeProduct(product.id) },
                            showDivider = index < productList.lastIndex
                        )
                    }
                }
            }
        }
    }
}
