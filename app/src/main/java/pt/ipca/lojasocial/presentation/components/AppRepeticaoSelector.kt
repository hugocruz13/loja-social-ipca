package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class FrequencyOption(val label: String) {
    NAO("Não repetir"),
    SEMANAL("Semanalmente"),
    MENSAL("Mensalmente"),
    SEMESTRAL("Semestral")
}

@Composable
fun AppRepeticaoSelector(
    title: String = "Repetição",
    selectedOption: FrequencyOption,
    onOptionSelected: (FrequencyOption) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val accentGreen = Color(0XFF00713C)

    Column(
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FrequencyButton(
                    option = FrequencyOption.NAO,
                    isSelected = selectedOption == FrequencyOption.NAO,
                    onSelect = onOptionSelected,
                    accentColor = accentGreen,
                    modifier = Modifier.weight(1f)
                )

                FrequencyButton(
                    option = FrequencyOption.SEMANAL,
                    isSelected = selectedOption == FrequencyOption.SEMANAL,
                    onSelect = onOptionSelected,
                    accentColor = accentGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FrequencyButton(
                    option = FrequencyOption.MENSAL,
                    isSelected = selectedOption == FrequencyOption.MENSAL,
                    onSelect = onOptionSelected,
                    accentColor = accentGreen,
                    modifier = Modifier.weight(1f)
                )

                FrequencyButton(
                    option = FrequencyOption.SEMESTRAL,
                    isSelected = selectedOption == FrequencyOption.SEMESTRAL,
                    onSelect = onOptionSelected,
                    accentColor = accentGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FrequencyButton(
    option: FrequencyOption,
    isSelected: Boolean,
    onSelect: (FrequencyOption) -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onSelect(option) },
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) accentColor else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // Sem sombra
    ) {
        Text(
            text = option.label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AppRepeticaoSelectorPreview() {
    val selected = remember { mutableStateOf(FrequencyOption.MENSAL) }

    Surface(modifier = Modifier.padding(16.dp)) {
        AppRepeticaoSelector(
            selectedOption = selected.value,
            onOptionSelected = { selected.value = it }
        )
    }
}