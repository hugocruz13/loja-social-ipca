package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.lojasocial.presentation.AuthViewModel
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val accentGreen = Color(0XFF00713C)
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }

    // 1. Criar estados para os campos (inicializados com os valores do state)
    // Nota: Numa aplicação real, o ideal é atualizar o ViewModel diretamente
    var nome by remember{ mutableStateOf("ASD") }
    var email by remember{ mutableStateOf("ASD@ASD.COM") }
    var nif by remember { mutableStateOf("123456789") }
    var numero by remember { mutableStateOf("121212") }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "O Meu Perfil",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            val navItems = listOf(
                BottomNavItem("home", Icons.Filled.Home, "Home"),
                BottomNavItem("notification", Icons.Filled.Notifications, "Notificações"),
                BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
            )
            AppBottomBar(
                navItems = navItems,
                currentRoute = "home",
                onItemSelected = { }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar com a inicial do nome editado
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(accentGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if(nome.isNotEmpty()) nome.take(1).uppercase() else "?",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentGreen,
                        fontSize = 40.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isEditing) "Cancelar Edição" else "Editar Perfil",
                color = if (isEditing) Color.Red else accentGreen,
                style = MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.clickable {
                    if (isEditing) {
                        // Resetar valores ao cancelar se necessário
                        nome = "123"
                        email = state.email
                    }
                    isEditing = !isEditing
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Ligar o 'value' e o 'onValueChange' às variáveis criadas
            AppTextField(
                value = nome,
                onValueChange = { nome = it },
                label = "Nome",
                placeholder = "O seu nome",
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = numero,
                onValueChange = { numero = it },
                label = "Nº",
                placeholder = "121212",
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "email@email.com",
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = nif,
                onValueChange = { nif = it },
                label = "NIF",
                placeholder = "Seu NIF",
                enabled = isEditing,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (isEditing) {
                AppButton(
                    text = "Guardar Alterações",
                    onClick = {
                        // 3. Aqui chamarias o viewModel para gravar permanentemente
                        // viewModel.updateProfile(nome, email, nif, numero)
                        isEditing = false
                    },
                    containerColor = accentGreen,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            } else {
                AppButton(
                    text = "Terminar Sessão",
                    onClick = onLogout,
                    containerColor = Color.Red,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    }
}