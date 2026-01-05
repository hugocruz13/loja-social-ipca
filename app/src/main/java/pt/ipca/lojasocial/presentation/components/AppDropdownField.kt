package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                onValueChange = { },
                readOnly = true,
                singleLine = true,
                shape = fieldShape,

                modifier = Modifier
                    .fillMaxWidth(),

                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "Abrir seleção"
                        )
                    }
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = true }
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