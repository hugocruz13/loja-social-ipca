package pt.ipca.lojasocial.presentation.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    enabled: Boolean = true,
    placeholder: String = "mm/dd/yyyy"
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val dateFormat = { y: Int, m: Int, d: Int ->
        String.format(Locale.getDefault(), "%02d/%02d/%d", m + 1, d, y)
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d -> onDateSelected(dateFormat(y, m, d)) },
        year, month, day
    )

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = selectedValue.ifEmpty { placeholder },
            onValueChange = { },
            readOnly = true,
            enabled = enabled,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .then(if (enabled) Modifier.clickable { datePickerDialog.show() } else Modifier),

            trailingIcon = {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Abrir calendário",
                    modifier = if (enabled) Modifier.clickable { datePickerDialog.show() } else Modifier
                )
            },

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0XFF00713C),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
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