package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.Color

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    enabled: Boolean = true,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val fieldShape = RoundedCornerShape(8.dp)

    val isPasswordToggle = keyboardType == KeyboardType.Password
    var passwordVisible by remember { mutableStateOf(false) }

    val currentVisualTransformation = when {
        isPasswordToggle && !passwordVisible -> PasswordVisualTransformation()
        else -> visualTransformation
    }

    val trailingIcon = if (isPasswordToggle) {
        @Composable {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = "password visivel")
            }
        }
    } else {
        null
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
            // Se estiver desativado, mudamos a cor do label para parecer bloqueado
            color = if (enabled) Color.Unspecified else MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isError = isError,
            singleLine = true,
            shape = fieldShape,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = currentVisualTransformation,
            trailingIcon = trailingIcon,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0XFF00713C),
                focusedLabelColor = Color(0XFF00713C),
                cursorColor = Color(0XFF00713C),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                // Cores para quando o campo está bloqueado (enabled = false)
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledContainerColor = Color.Transparent
            )
        )
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