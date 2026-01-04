package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import pt.ipca.lojasocial.presentation.components.AppAuthSwitcher
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppLogo
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel


@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedIn, state.isLoading) {
        // Só avança se estiver logado E o load do perfil tiver acabado
        if (state.isLoggedIn && !state.isLoading) {
            onLoginSuccess()
        }
    }

    Scaffold(
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AppLogo()

            Text(
                text = "Bem-vindo(a)!",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(32.dp))

            AppAuthSwitcher(
                onNavigateToRegister = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Introduza o seu email",
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = state.errorMessage != null
            )
            AppTextField(
                value = password,
                onValueChange = { password = it },
                label = "Palavra-passe",
                placeholder = "Introduza a sua password",
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                isError = state.errorMessage != null
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }


            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(56.dp),
                    color = Color(0XFF00713C)
                )
            } else {
                AppButton(
                    text = "Login",
                    onClick = { viewModel.login(email, password) },
                    containerColor = Color(0XFF00713C),
                    enabled = email.isNotBlank() && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    }
}