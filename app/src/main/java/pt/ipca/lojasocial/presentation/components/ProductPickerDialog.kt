package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.domain.models.Product

@Composable
fun ProductPickerDialog(
    products: List<Product>,
    selectedProducts: Map<String, Int>,
    onProductQuantityChange: (productId: String, quantity: Int) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    // A temporary state to hold changes until the user clicks "Confirm"
    var tempSelectedProducts by remember { mutableStateOf(selectedProducts) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Produtos") },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(products) { index, product ->
                    val quantity = tempSelectedProducts[product.id] ?: 0

                    // Create a DeliveryProduct on the fly to pass to the QuantitySelectorItem
                    val deliveryProduct = DeliveryProduct(
                        id = product.id,
                        name = product.name,
                        quantity = quantity
                    )

                    QuantitySelectorItem(
                        product = deliveryProduct,
                        onQuantityChange = { newQuantity ->
                            val updatedMap = tempSelectedProducts.toMutableMap()
                            if (newQuantity > 0) {
                                updatedMap[product.id] = newQuantity
                            } else {
                                updatedMap.remove(product.id)
                            }
                            tempSelectedProducts = updatedMap
                        },
                        // The remove button inside the selector will just set the quantity to 0
                        onRemove = {
                            val updatedMap = tempSelectedProducts.toMutableMap()
                            updatedMap.remove(product.id)
                            tempSelectedProducts = updatedMap
                        },
                        showDivider = index < products.lastIndex,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Apply all changes at once on confirm
                tempSelectedProducts.forEach { (productId, quantity) ->
                    onProductQuantityChange(productId, quantity)
                }
                // Also remove products that were deselected
                selectedProducts.keys.forEach { productId ->
                    if (!tempSelectedProducts.containsKey(productId)) {
                        onProductQuantityChange(productId, 0)
                    }
                }
                onConfirm()
            }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
