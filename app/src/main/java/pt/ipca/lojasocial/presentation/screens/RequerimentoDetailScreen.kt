package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.*

@Composable
fun RequerimentoDetailScreen(
    requerimentoId: String,
    onBackClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: (String) -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    val accentGreen = Color(0XFF00713C)
    var showRejectModal by remember { mutableStateOf(false) }
    var justificacao by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    if (showRejectModal) {
        AlertDialog(
            onDismissRequest = { showRejectModal = false },
            title = { Text("Justificação de Rejeição") },
            text = {
                AppTextField(
                    value = justificacao,
                    onValueChange = { justificacao = it },
                    label = "Motivo",
                    placeholder = "Escreva aqui a razão da rejeição...",
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onReject(justificacao)
                        showRejectModal = false
                    },
                    enabled = justificacao.isNotBlank()
                ) {
                    Text("Confirmar Rejeição", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectModal = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Aceitar / Rejeitar Requerimento", onBackClick = onBackClick)
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Joana Filipa",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nº estudante: 12345",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Submetido: 15 Oct 2023",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppStatusBadge(status = StatusType.PENDENTE)
                }
            }

            AppExpandableCard(
                title = "Informação Pessoal",
                initialExpanded = true,
                content = {
                    Column {
                        InfoRow(label = "Contacto", value = "912 345 678")
                        InfoRow(label = "Email", value = "email@email.com")
                        InfoRow(label = "Data Nasc.", value = "24 Julho 1985")
                        InfoRow(label = "CC", value = "00000000 00Z")
                    }
                }
            )

            AppExpandableCard(
                title = "Tipo Pedido / Dados Académicos",
                content = {
                    Column {
                        InfoRow(label = "Tipo", value = "Apoio Alimentar")
                        InfoRow(label = "Curso", value = "Engenharia Informática")
                    }
                }
            )

            AppExpandableCard(
                title = "Documentos Carregados",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("• Comprovativo de Morada.pdf", color = accentGreen)
                        Text("• IRS_2023.pdf", color = accentGreen)
                        Text("• Cartao_Cidadao.pdf", color = accentGreen)
                    }
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppButton(
                    text = "Rejeitar",
                    onClick = { showRejectModal = true },
                    containerColor = Color.Red,
                    modifier = Modifier.weight(1f).height(56.dp)
                )
                AppButton(
                    text = "Aceitar",
                    onClick = onAccept,
                    containerColor = accentGreen,
                    modifier = Modifier.weight(1f).height(56.dp)
                )
            }
        }
    }
}