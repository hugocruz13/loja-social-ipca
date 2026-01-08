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
import java.util.Calendar
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

    // --- Lógica de Validação Local ---
    val qtyInt = quantity.toIntOrNull() ?: 0
    val isQtyValid = qtyInt > 0

    val expiryMillis = remember(expiryDate) {
        if (expiryDate.isNotBlank()) dateStringToMillis(expiryDate) else 0L
    }

    // Comparamos com o início do dia de hoje para permitir produtos que vencem hoje
    val isDateValid = remember(expiryMillis) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        expiryMillis >= today // Permite hoje ou datas futuras
    }

    val isFormValid = isQtyValid && isDateValid && expiryDate.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Text(
                    text = "Adicionar Stock",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(20.dp))

                // QUANTIDADE
                AppTextField(
                    value = quantity,
                    onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
                    label = "Quantidade",
                    placeholder = "Insira a quantidade",
                    keyboardType = KeyboardType.Number,
                    errorMessage = if (quantity.isNotEmpty() && !isQtyValid) "A quantidade deve ser maior que 0" else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // VALIDADE
                AppDatePickerField(
                    label = "Validade",
                    selectedValue = expiryDate,
                    onDateSelected = { expiryDate = it },
                    placeholder = "dd/mm/yyyy",
                    errorMessage = if (expiryDate.isNotEmpty() && !isDateValid) "O produto já está fora de validade" else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // OBSERVAÇÕES
                AppTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = "Observações",
                    placeholder = "Opcional",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                // BOTÃO CONFIRMAR
                AppButton(
                    text = "Confirmar",
                    onClick = {
                        if (isFormValid) {
                            val stock = Stock(
                                id = UUID.randomUUID().toString(),
                                productId = product.id,
                                campaignId = campaignId,
                                quantity = qtyInt,
                                entryDate = System.currentTimeMillis(),
                                expiryDate = expiryMillis,
                                observations = observations.ifBlank { null }
                            )
                            onConfirm(stock)
                        }
                    },
                    enabled = isFormValid,
                    containerColor = if (isFormValid) Color(0XFF00713C) else Color(0XFFC7C7C7),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}

// Helper robusto para conversão de data
fun dateStringToMillis(date: String): Long {
    return try {
        // Ajustado para o formato que o teu AppDatePickerField usa (dd/MM/yyyy)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.parse(date)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}