package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    placeholder: String = "Selecione uma opção"
) {
    var expanded by remember { mutableStateOf(false) }
    val fieldShape = RoundedCornerShape(8.dp)

    val displayValue = selectedValue.ifEmpty { placeholder }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {

            OutlinedTextField(
                value = displayValue,
                onValueChange = {  },
                readOnly = true,
                singleLine = true,
                shape = fieldShape,

                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },

                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "Abrir seleção"
                    )
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            onOptionSelected(selectionOption)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppDropdownFieldLevelsPreview() {
    var selectedLevel by remember { mutableStateOf("") }
    val levels = listOf("Ensino Básico", "Ensino Secundário", "Ensino Superior")

    Surface(modifier = Modifier.padding(16.dp)) {
        AppDropdownField(
            label = "Nível de Ensino",
            selectedValue = selectedLevel,
            options = levels,
            onOptionSelected = { selectedLevel = it },
            placeholder = "Selecione nível de ensino"
        )
    }
}