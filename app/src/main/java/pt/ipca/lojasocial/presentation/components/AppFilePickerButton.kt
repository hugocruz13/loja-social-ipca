package pt.ipca.lojasocial.presentation.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppFilePickerField(
    description: String,
    fileName: String?,
    onSelectFile: (Uri?) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    errorMessage: String? = null // NOVO: Suporte para validação
) {
    val isError = errorMessage != null
    val accentColor = if (isError) MaterialTheme.colorScheme.error else Color(0XFF00713C)
    val buttonBgColor = accentColor.copy(alpha = 0.05f)

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        // Label/Descrição
        Text(
            text = description,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
            color = if (isError) MaterialTheme.colorScheme.error else Color.DarkGray
        )

        // Botão Customizado para parecer um campo de input
        OutlinedButton(
            onClick = { if (enabled) onSelectFile(null) },
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = if (isError) 2.dp else 1.dp,
                color = if (isError) MaterialTheme.colorScheme.error else Color(0xFFE0E0E0)
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.05f) else buttonBgColor,
                contentColor = accentColor
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = if (isError) Icons.Rounded.ErrorOutline else Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = accentColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = if (fileName.isNullOrEmpty()) "Selecionar Ficheiro" else fileName,
                    color = accentColor,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }

        // Mensagem de Erro Animada
        AnimatedVisibility(
            visible = isError,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    )
                )
            }
        }
    }
}