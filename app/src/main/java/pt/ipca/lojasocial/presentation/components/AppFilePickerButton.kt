package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppFilePickerButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    isSelected: Boolean = false
) {
    val buttonColor = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        border = BorderStroke(width = 2.dp, color = Color(0XFF00713C)),
        colors = ButtonDefaults.outlinedButtonColors(Color(0X2000713C))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.FileUpload else Icons.Default.FileUpload,
                contentDescription = "Selecionar Ficheiro",
                tint = Color(0XFF00713C)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                color = Color(0XFF00713C)
            )
        }
    }
}

@Composable
fun AppFilePickerField(
    description: String,
    fileName: String?,
    onSelectFile: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )


        AppFilePickerButton(
            onClick = onSelectFile,
            label = fileName ?: "Selecionar Ficheiro",
            isSelected = fileName != null
        )
    }
}

@Preview(name = "File Picker Vazio", showBackground = true)
@Composable
fun AppFilePickerEmptyPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AppFilePickerField(
            description = "a) Documento de identificação do candidato",
            fileName = null,
            onSelectFile = { }
        )
    }
}

@Preview(name = "File Picker Selecionado", showBackground = true)
@Composable
fun AppFilePickerSelectedPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AppFilePickerField(
            description = "b) Documento de identificação do agregado familiar",
            fileName = "cc_familia_2025.pdf",
            onSelectFile = { }
        )
    }
}