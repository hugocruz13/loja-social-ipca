package pt.ipca.lojasocial.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.ErrorOutline
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    // Removido o visualTransformation fixo para ser gerido internamente
) {
    val isError = errorMessage != null
    val fieldShape = RoundedCornerShape(12.dp)

    // Lógica para Password
    var passwordVisible by remember { mutableStateOf(false) }
    val isPasswordField = keyboardType == KeyboardType.Password

    // Define a transformação visual (Bolinhas ou Texto)
    val visualTransformation = if (isPasswordField && !passwordVisible) {
        PasswordVisualTransformation()
    } else {
        VisualTransformation.None
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
            color = if (isError) MaterialTheme.colorScheme.error else Color.DarkGray
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            singleLine = true,
            shape = fieldShape,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            trailingIcon = {
                if (isError) {
                    Icon(Icons.Rounded.ErrorOutline, null, tint = MaterialTheme.colorScheme.error)
                } else if (isPasswordField) {
                    // Ícone do Olho para Password
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = "Toggle Password")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0XFF00713C),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.05f),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        AnimatedVisibility(visible = isError, enter = fadeIn() + expandVertically(), exit = fadeOut() + shrinkVertically()) {
            Row(modifier = Modifier.padding(top = 6.dp, start = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MaterialTheme.colorScheme.error))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.2.sp)
                )
            }
        }
    }
}

@Preview(name = "Campo Bloqueado (Read Only)", showBackground = true)
@Composable
fun AppTextFieldDisabledPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AppTextField(
            value = "Gustavo Santos",
            onValueChange = { },
            label = "Nome Completo",
            placeholder = "",
            enabled = false
        )
    }
}

@Preview(name = "Campo Nome", showBackground = true)
@Composable
fun AppTextFieldFixedLabelPreview() {
    var name by remember { mutableStateOf("") }
    Surface(modifier = Modifier.padding(16.dp)) {
        AppTextField(
            value = name,
            onValueChange = { name = it },
            label = "Nome Completo",
            placeholder = "Insira o seu nome"
        )
    }
}

@Preview(name = "Campo CC", showBackground = true)
@Composable
fun AppTextFieldFilledFixedLabelPreview() {
    var cc by remember { mutableStateOf("00000000 ZZO") }
    Surface(modifier = Modifier.padding(16.dp)) {
        AppTextField(
            value = cc,
            onValueChange = { cc = it },
            label = "Cartão de Cidadão",
            placeholder = "00000000 ZZO"
        )
    }
}

@Preview(name = "Campo Password com Toggle", showBackground = true)
@Composable
fun AppTextFieldPasswordTogglePreview() {
    var password by remember { mutableStateOf("minhasenha123") }
    Surface(modifier = Modifier.padding(16.dp)) {
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Introduza a sua password",
            keyboardType = KeyboardType.Password
        )
    }
}
