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
import android.net.Uri // 💥 IMPORTANTE: Necessário para Uri?

@Composable
fun AppFilePickerButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    isSelected: Boolean = false
) {
    val accentColor = Color(0XFF00713C)
    val buttonBgColor = accentColor.copy(alpha = 0.1f)

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        border = BorderStroke(width = 2.dp, color = accentColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = buttonBgColor,
            contentColor = accentColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FileUpload,
                contentDescription = "Selecionar Ficheiro",
                tint = accentColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                color = accentColor
            )
        }
    }
}

@Composable
fun AppFilePickerField(
    description: String,
    fileName: String?,
    onSelectFile: (Uri?) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )


        AppFilePickerButton(
            onClick = {
                onSelectFile(null)
            },

            label = if (fileName.isNullOrEmpty()) "Selecionar Ficheiro" else fileName,
            isSelected = !fileName.isNullOrEmpty()
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
            onSelectFile = {  }
        )
    }
}