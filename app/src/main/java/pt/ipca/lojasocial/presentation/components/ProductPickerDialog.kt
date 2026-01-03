package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import pt.ipca.lojasocial.domain.models.Product

@Composable
fun ProductPickerDialog(
    products: List<Product>,
    selectedProducts: Map<String, Int>,
    stockLimits: Map<String, Int>, // New parameter
    onProductQuantityChange: (productId: String, quantity: Int) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var tempSelectedProducts by remember { mutableStateOf(selectedProducts) }
    val accentGreen = Color(0XFF00713C)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Selecionar Produtos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(products) { product ->
                        val quantity = tempSelectedProducts[product.id] ?: 0
                        val stockLimit = stockLimits[product.id] ?: 0

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = product.photoUrl,
                                    contentDescription = product.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                    Text(product.type.name, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            if (quantity > 0) {
                                                val updatedMap = tempSelectedProducts.toMutableMap()
                                                updatedMap[product.id] = quantity - 1
                                                if (updatedMap[product.id] == 0) {
                                                    updatedMap.remove(product.id)
                                                }
                                                tempSelectedProducts = updatedMap
                                            }
                                        },
                                        enabled = quantity > 0
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Remover")
                                    }
                                    Text(quantity.toString(), modifier = Modifier.padding(horizontal = 4.dp))
                                    IconButton(
                                        onClick = {
                                            val updatedMap = tempSelectedProducts.toMutableMap()
                                            updatedMap[product.id] = quantity + 1
                                            tempSelectedProducts = updatedMap
                                        },
                                        enabled = quantity < stockLimit // Disable if limit is reached
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Adicionar",
                                            tint = if (quantity < stockLimit) accentGreen else Color.Gray
                                        )
                                    }
                                }
                            }
                            // Warning message
                            if (quantity >= stockLimit) {
                                Text(
                                    "Máximo de ${stockLimit} atingido",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = {
                            tempSelectedProducts.forEach { (productId, quantity) ->
                                onProductQuantityChange(productId, quantity)
                            }
                            selectedProducts.keys.forEach { productId ->
                                if (!tempSelectedProducts.containsKey(productId)) {
                                    onProductQuantityChange(productId, 0)
                                }
                            }
                            onConfirm()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = accentGreen)
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}
