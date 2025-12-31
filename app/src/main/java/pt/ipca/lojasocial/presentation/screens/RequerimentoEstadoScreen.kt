package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.StatusType

data class DocumentItemState(
    val name: String,
    val fileSize: String,
    val fileType: String, // PDF, JPG
    val isValid: Boolean // Se true = verde/validado, Se false = vermelho/submeter
)

// --- ECRÃ PRINCIPAL ---

@Composable
fun RequerimentoEstadoScreen(
    onBackClick: () -> Unit,
    status: StatusType,
    beneficiaryName: String,
    cc: String,
    observations: String = ""
) {
    // Dados de exemplo para os documentos (apenas visualização)
    val documents = listOf(
        DocumentItemState("Comprovativo de Morada", "2.4 MB", "PDF", isValid = false),
        DocumentItemState("Cartão de Cidadão", "1.8 MB", "JPG", isValid = true),
        DocumentItemState("Certificado Habilitações", "5.1 MB", "PDF", isValid = true)
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Estado do Requerimento",
                onBackClick = onBackClick
            )
        },
        containerColor = Color(0xFFF9FAFB)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Cabeçalho com dados dinâmicos
            ProfileHeaderCard(
                name = beneficiaryName,
                cc = cc
            )

            // 2. Cartão de Estado (Simples e "UI Friendly")
            StatusCard(status = status)

            // 3. Lista de Documentos (SÓ aparece se houver erros)
            if (status == StatusType.REJEITADA || status == StatusType.DOCS_INCORRETOS) {

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Motivo / Observações",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (observations.isNotBlank()) observations else "Sem detalhes adicionais.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
private fun ProfileHeaderCard(name: String, cc: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                // Tenta mostrar a inicial do nome se existir
                if (name.isNotBlank()) {
                    Text(
                        text = name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name.ifBlank { "Sem Nome" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CC: ${cc.ifBlank { "N/A" }}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun StatusCard(status: StatusType) {
    // Configuração de Cores e Texto baseada no estado
    val containerColor = if (status == StatusType.ANALISE) Color(0xFFFFF8E1) else Color(0xFFFFEBEE)
    val contentColor = if (status == StatusType.ANALISE) Color(0xFFF57C00) else Color(0xFFD32F2F)
    val borderColor = if (status == StatusType.ANALISE) Color(0xFFFFB74D) else Color(0xFFEF9A9A)

    val title = if (status == StatusType.ANALISE) "Em Análise" else "Atenção Necessária"
    val icon = if (status == StatusType.ANALISE) Icons.Outlined.Timer else Icons.Outlined.Warning

    val description = if (status == StatusType.ANALISE)
        "O processo encontra-se em validação pelos serviços."
    else
        "Detetámos problemas em alguns documentos. Verifique abaixo."

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Barra lateral
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(38.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(contentColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = icon, contentDescription = null, tint = contentColor)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun DocumentRowItem(document: DocumentItemState) {
    val isValid = document.isValid

    val iconBgColor = if (isValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val iconTint = if (isValid) Color(0xFF00713C) else Color(0xFFD32F2F)
    val statusText = if (isValid) "Validado" else "Reenviar Documento"
    val statusColor = if (isValid) Color(0xFF00713C) else Color(0xFFD32F2F)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isValid) Icons.Default.Check else Icons.Outlined.Description,
                    contentDescription = null,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }

            if (!isValid) {
                Button(
                    onClick = { /* Upload */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00713C)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Submeter", fontSize = 12.sp)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "OK",
                    tint = Color(0xFF00713C),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// --- PREVIEWS ---

@Preview(showBackground = true)
@Composable
fun PreviewIncorretos() {
    RequerimentoEstadoScreen(
        onBackClick = {},
        status = StatusType.DOCS_INCORRETOS,
        beneficiaryName = "Ana Pereira",
        cc = "32132132 2ZX0"
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEmAnalise() {
    RequerimentoEstadoScreen(
        onBackClick = {},
        status = StatusType.DOCS_INCORRETOS,
        beneficiaryName = "João Silva",
        cc = "32132132 2ZX0"
    )
}