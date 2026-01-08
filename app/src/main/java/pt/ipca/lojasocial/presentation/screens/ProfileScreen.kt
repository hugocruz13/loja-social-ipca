package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.BeneficiariesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: BeneficiariesViewModel, // Trocamos AuthViewModel por BeneficiariesViewModel
    currentUser: Beneficiary?,         // Dados do utilizador logado
    userRole: UserRole,                // Cargo (Staff ou Beneficiary)
    onLogout: () -> Unit,
    onBackClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    val accentGreen = Color(0XFF00713C)
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }


    var nome by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var email by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var numero by remember(currentUser) {
        mutableStateOf(
            currentUser?.phoneNumber?.toString() ?: ""
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "O Meu Perfil", onBackClick = onBackClick)
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "profile",
                onItemSelected = { item -> onNavigate(item.route) }
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
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(accentGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (nome.isNotEmpty()) nome.take(1).uppercase() else "?",
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
                        // Cancelar: repõe os valores originais
                        nome = currentUser?.name ?: ""
                        email = currentUser?.email ?: ""
                        numero = currentUser?.phoneNumber?.toString() ?: ""
                    }
                    isEditing = !isEditing
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            val isStaff = userRole == UserRole.STAFF

            AppTextField(
                value = nome,
                onValueChange = { nome = it },
                label = "Nome",
                placeholder = "O seu nome",
                enabled = isEditing && isStaff,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = numero,
                onValueChange = { numero = it },
                label = "Nº Telemóvel", // Alterei Label para bater certo
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

            Spacer(modifier = Modifier.height(32.dp))

            if (isEditing) {
                AppButton(
                    text = "Guardar Alterações",
                    onClick = {
                        currentUser?.let { original ->
                            val modified = original.copy(
                                name = nome,
                                email = email,
                                phoneNumber = numero.toIntOrNull() ?: 0
                            )
                            viewModel.updateBeneficiaryProfile(userRole, original, modified)
                        }
                        isEditing = false
                    },
                    containerColor = accentGreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            } else {
                AppButton(
                    text = "Terminar Sessão",
                    onClick = onLogout,
                    containerColor = Color.Red,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }
        }
    }
}