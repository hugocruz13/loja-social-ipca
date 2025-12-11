package pt.ipca.lojasocial.presentation.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.Locale

@Composable
fun AppDatePickerField(
    label: String,
    selectedValue: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    placeholder: String = "mm/dd/yyyy"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Obtém o ano, mês e dia atuais para definir a data padrão do seletor
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    // Formato de data para exibir
    val dateFormat = { y: Int, m: Int, d: Int ->
        String.format(Locale.getDefault(), "%02d/%02d/%d", m + 1, d, y)
    }

    // Função para mostrar o diálogo do seletor de data
    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            onDateSelected(dateFormat(y, m, d))
        },
        year, month, day
    )

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = selectedValue.ifEmpty { placeholder },
            onValueChange = { },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),

            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() },

            // Ícone de Calendário
            trailingIcon = {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Abrir calendário",
                    modifier = Modifier.clickable { datePickerDialog.show() }
                )
            },

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Preview(name = "Data de Nascimento", showBackground = true)
@Composable
fun AppDatePickerFieldEmptyPreview() {
    var selectedDate by androidx.compose.runtime.remember { mutableStateOf("") }
    Surface(modifier = Modifier.padding(16.dp)) {
        AppDatePickerField(
            label = "Data de Nascimento",
            selectedValue = selectedDate,
            onDateSelected = { selectedDate = it },
            placeholder = "mm/dd/yyyy"
        )
    }
}

@Preview(name = "Data Validade Preenchida", showBackground = true)
@Composable
fun AppDatePickerFieldFilledPreview() {
    var selectedDate by androidx.compose.runtime.remember { mutableStateOf("10/27/2023") }
    Surface(modifier = Modifier.padding(16.dp)) {
        AppDatePickerField(
            label = "Data Validade",
            selectedValue = selectedDate,
            onDateSelected = { selectedDate = it },
            placeholder = "mm/dd/yyyy"
        )
    }
}