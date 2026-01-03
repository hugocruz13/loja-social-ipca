package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import pt.ipca.lojasocial.domain.models.Product
import pt.ipca.lojasocial.domain.models.Stock
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AddStockDialog(
    product: Product,
    campaignId: String,
    onDismiss: () -> Unit,
    onConfirm: (Stock) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F9FA)
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Adicionar Stock",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(12.dp))

                AppDatePickerField(
                    label = "Validade",
                    selectedValue = expiryDate,
                    onDateSelected = { newDate ->
                        expiryDate = newDate
                    },
                    placeholder = "dd/mm/yyyy",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )


                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Observações") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    maxLines = 3
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val stock = Stock(
                            id = UUID.randomUUID().toString(),
                            productId = product.id,
                            campaignId = campaignId,
                            quantity = quantity.toInt(),
                            entryDate = System.currentTimeMillis(),
                            expiryDate = dateStringToMillis(expiryDate),
                            observations = observations.ifBlank { null }
                        )
                        onConfirm(stock)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = quantity.isNotBlank() && expiryDate != null
                ) {
                    Text("Confirmar")
                }
            }
        }
    }
}

fun dateStringToMillis(date: String): Long {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.parse(date)?.time
        ?: throw IllegalArgumentException("Data inválida")
}

