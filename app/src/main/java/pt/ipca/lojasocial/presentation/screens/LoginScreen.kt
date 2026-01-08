package pt.ipca.lojasocial.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AppAuthSwitcher
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppLogo
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.viewmodels.AuthViewModel
import pt.ipca.lojasocial.utils.RequestNotificationPermission

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    RequestNotificationPermission()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    // Lógica de animação de entrada
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6

    val emailError =
        if (emailTouched && !isEmailValid && email.isNotEmpty()) "Email inválido" else null
    val passwordError =
        if (passwordTouched && !isPasswordValid && password.isNotEmpty()) "Mínimo 6 caracteres" else null

    LaunchedEffect(state.isLoggedIn, state.isLoading) {
        if (state.isLoggedIn && !state.isLoading) {
            onLoginSuccess()
        }
    }

    Scaffold(containerColor = Color.White) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- CABEÇALHO ANIMADO (Logo e Boas-vindas) ---
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(
                    animationSpec = tween(
                        600
                    )
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Box(modifier = Modifier.size(300.dp)) {
                        AppLogo()
                    }

                    Text(
                        text = "Bem-vindo(a)!",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Inicie sessão para continuar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- FORMULÁRIO ANIMADO ---
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(initialOffsetY = { 40 }) + fadeIn(
                    animationSpec = tween(
                        800
                    )
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppAuthSwitcher(
                        onNavigateToRegister = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    AppTextField(
                        value = email,
                        onValueChange = { email = it; emailTouched = true },
                        label = "Email",
                        placeholder = "exemplo@email.com",
                        keyboardType = KeyboardType.Email,
                        errorMessage = emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = password,
                        onValueChange = { password = it; passwordTouched = true },
                        label = "Palavra-passe",
                        placeholder = "Introduza a sua password",
                        keyboardType = KeyboardType.Password,
                        errorMessage = passwordError
                    )
                }
            }

            // --- FEEDBACK DE ERRO DO SERVIDOR ---
            AnimatedVisibility(
                visible = state.errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- BOTÃO DE LOGIN ANIMADO ---
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1000)) + expandVertically()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = accentGreen)
                } else {
                    AppButton(
                        text = "Entrar",
                        onClick = { viewModel.login(email, password) },
                        containerColor = accentGreen,
                        enabled = isEmailValid && isPasswordValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}