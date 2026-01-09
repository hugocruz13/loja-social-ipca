package pt.ipca.lojasocial.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.lojasocial.domain.models.Beneficiary
import pt.ipca.lojasocial.domain.models.UserRole
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.viewmodels.BeneficiariesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: BeneficiariesViewModel,
    currentUser: Beneficiary?,
    userRole: UserRole,
    onLogout: () -> Unit,
    onBackClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    val accentGreen = Color(0XFF00713C)
    val backgroundLight = Color(0xFFF8F9FA)

    // 1. Observar o beneficiário completo vindo da Base de Dados
    val remoteUser by viewModel.selectedBeneficiary.collectAsState()

    // 2. Carregar dados frescos assim que o ecrã abre
    LaunchedEffect(currentUser?.id) {
        if (currentUser?.id != null) {
            viewModel.loadBeneficiaryDetail(currentUser.id)
        }
    }

    // 3. Determinar qual user usar (damos preferência ao que vem da BD, se não houver, usa o do Auth)
    val userDisplay = remoteUser ?: currentUser

    var isEditing by remember { mutableStateOf(false) }

    // 4. Estados dos campos (Atualizam quando o remoteUser chega)
    var nome by remember(userDisplay) { mutableStateOf(userDisplay?.name ?: "") }
    var email by remember(userDisplay) { mutableStateOf(userDisplay?.email ?: "") }

    // CORREÇÃO DO ZERO: Se for 0, mostra vazio
    var numero by remember(userDisplay) {
        mutableStateOf(
            if (userDisplay?.phoneNumber == 0) "" else userDisplay?.phoneNumber?.toString() ?: ""
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "O Meu Perfil",
                onBackClick = onBackClick,
                actions = {
                    if (!isEditing) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = accentGreen
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            isEditing = false
                            // Reset aos valores originais
                            nome = userDisplay?.name ?: ""
                            email = userDisplay?.email ?: ""
                            numero =
                                if (userDisplay?.phoneNumber == 0) "" else userDisplay?.phoneNumber?.toString()
                                    ?: ""
                        }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancelar",
                                tint = Color.Red
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "profile",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        containerColor = backgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- 1. CABEÇALHO ---
            ProfileHeader(
                name = nome,
                role = userRole,
                accentColor = accentGreen
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. FORMULÁRIO ---
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "Dados Pessoais",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1E293B)
                    )

                    ProfileTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = "Nome Completo",
                        icon = Icons.Default.Person,
                        isEditing = isEditing && (userRole == UserRole.STAFF),
                        accentColor = accentGreen
                    )

                    ProfileTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Endereço de Email",
                        icon = Icons.Default.Email,
                        isEditing = isEditing,
                        keyboardType = KeyboardType.Email,
                        accentColor = accentGreen
                    )

                    ProfileTextField(
                        value = numero,
                        onValueChange = { numero = it },
                        label = "Nº Telemóvel",
                        icon = Icons.Default.Phone,
                        isEditing = isEditing,
                        keyboardType = KeyboardType.Phone,
                        accentColor = accentGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- 3. AÇÕES ---
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = {
                        userDisplay?.let { original ->
                            val modified = original.copy(
                                name = nome,
                                email = email,
                                // Converte para Int e protege contra strings vazias
                                phoneNumber = numero.toIntOrNull() ?: 0
                            )
                            viewModel.updateBeneficiaryProfile(userRole, original, modified)
                        }
                        isEditing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar Alterações", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (!isEditing) {
                OutlinedButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(Icons.Outlined.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Terminar Sessão", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Loja Social IPCA v1.0 • ID: ${userDisplay?.id?.takeLast(6)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun ProfileHeader(name: String, role: UserRole, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Avatar Grande com Borda
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, accentColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (name.isNotEmpty()) name.take(1).uppercase() else "?",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name.ifBlank { "Utilizador" },
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Badge de Role
        Surface(
            color = accentColor.copy(alpha = 0.1f),
            shape = RoundedCornerShape(50),
            modifier = Modifier.height(30.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = if (role == UserRole.STAFF) "COLABORADOR" else "BENEFICIÁRIO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    ),
                    color = accentColor
                )
            }
        }
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isEditing: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    accentColor: Color
) {
    val containerColor = if (isEditing) Color(0xFFF8FAFC) else Color.Transparent

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = isEditing,
            leadingIcon = { Icon(icon, null, tint = if (isEditing) accentColor else Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
                disabledBorderColor = Color(0xFFE2E8F0),
                disabledTextColor = Color(0xFF1E293B),
                focusedBorderColor = accentColor,
                unfocusedBorderColor = Color(0xFFE2E8F0)
            )
        )
    }
}