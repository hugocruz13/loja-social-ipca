package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

@Composable
fun AddStockDialog(
    product: Product,
    campaignId: String?,
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

                AppTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "Quantidade",
                    placeholder = "Insira a quantidade",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppDatePickerField(
                    label = "Validade",
                    selectedValue = expiryDate,
                    onDateSelected = { newDate ->
                        expiryDate = newDate
                    },
                    placeholder = "dd/mm/yyyy",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                AppTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = "Observações",
                    placeholder = "Observações (opcional)",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                AppButton(
                    text = "Confirmar",
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
                    enabled = quantity.isNotBlank() && expiryDate.isNotBlank(),
                    containerColor = Color(0XFF00713C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}

fun dateStringToMillis(date: String): Long {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.parse(date)?.time
        ?: throw IllegalArgumentException("Data inválida")
}

