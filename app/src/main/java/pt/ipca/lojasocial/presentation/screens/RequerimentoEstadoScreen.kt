package pt.ipca.lojasocial.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AppTopBar

// Mapeamento amigável das chaves da BD para Títulos
val docLabels = mapOf(
    "identificacao" to "Cartão de Cidadão",
    "agregado" to "Comprovativo Agregado",
    "morada" to "Comprovativo de Morada",
    "rendimento" to "Comprovativo de Rendimentos",
    "matricula" to "Comprovativo de Matrícula",
    "anexo" to "Documento Adicional"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequerimentoEstadoScreen(
    onBackClick: () -> Unit,
    status: StatusType,
    beneficiaryName: String,
    cc: String,
    observations: String = "",
    documents: Map<String, String?> = emptyMap(), // Recebe o mapa de documentos
    uploadingDocKey: String?,
    onResubmitDoc: (String, Uri) -> Unit = { _, _ -> } // Callback para o ViewModel
) {
    // Gestão do Upload de ficheiros
    var selectedDocKey by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && selectedDocKey != null) {
            onResubmitDoc(selectedDocKey!!, uri)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Estado do Requerimento", onBackClick = onBackClick)
        },
        containerColor = Color(0xFFF9FAFB) // Fundo cinza claro
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Cabeçalho
            ProfileHeaderCard(name = beneficiaryName, cc = cc)

            // 2. Cartão de Estado (Sem opção de Aprovado)
            StatusCard(status = status)

            // --- LÓGICA CONDICIONAL ---

            // CASO A: REJEITADA -> Mostra Observações
            if (status == StatusType.REJEITADA) {
                ObservationCard(observations)
            }

            // CASO B: DOCS INCORRETOS -> Mostra Lista de Documentos para corrigir
            if (status == StatusType.DOCS_INCORRETOS) {
                Text(
                    text = "Documentos Enviados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Itera sobre os documentos esperados
                // Se o mapa documents vier vazio, usamos as chaves padrão, senão usamos as do mapa
                val keysToShow = if (documents.isNotEmpty()) documents.keys else docLabels.keys

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    keysToShow.forEach { key ->
                        val url = documents[key]
                        // Se URL for null ou vazio, é inválido -> Precisa alterar
                        val isValid = !url.isNullOrBlank()
                        val label = docLabels[key] ?: "Documento"

                        val isUploading = (key == uploadingDocKey)

                        DocumentStatusRow(
                            name = label,
                            isValid = isValid,
                            isUploading = isUploading,
                            onUploadClick = {
                                selectedDocKey = key
                                launcher.launch("application/pdf") // Ou "*/*"
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- COMPONENTES VISUAIS ---

@Composable
fun DocumentStatusRow(
    name: String,
    isValid: Boolean,
    isUploading: Boolean,
    onUploadClick: () -> Unit
) {
    // Configuração de Estilo
    val bgColor = Color.White
    val borderColor =
        if (isValid) Color(0xFFE0E0E0) else Color(0xFFFFCDD2) // Vermelho claro se erro

    // Ícone
    val iconBg = if (isValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val iconColor = if (isValid) Color(0xFF00713C) else Color(0xFFD32F2F)
    val iconVector = if (isValid) Icons.Default.Check else Icons.Default.Description

    // Textos
    val statusText = if (isValid) "Validado" else "Alteração Necessária"
    val statusTextColor = if (isValid) Color(0xFF00713C) else Color(0xFFD32F2F)

    Card(
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Ícone Circular
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                val statusText = when {
                    isUploading -> "A enviar..."
                    isValid -> "Validado"
                    else -> "Alteração Necessária"
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusTextColor,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botão
            if (isUploading) {
                // MOSTRA SPINNER
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(36.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF00713C),
                        strokeWidth = 2.dp
                    )
                }
            } else {
                // MOSTRA BOTÃO ou CHECK
                if (!isValid) {
                    Button(
                        onClick = onUploadClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00713C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submeter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Botão Cinza (Já submetido)
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Check,
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enviado", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ObservationCard(observations: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Warning, null, tint = Color.Red)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Motivo da Recusa",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (observations.isNotBlank()) observations else "Sem detalhes adicionais.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.8f)
            )
        }
    }
}

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
    val containerColor = when (status) {
        StatusType.ANALISE -> Color(0xFFFFF8E1)
        StatusType.DOCS_INCORRETOS -> Color(0xFFFFEBEE)
        StatusType.REJEITADA -> Color(0xFFFFEBEE)
        else -> Color.White // Fallback
    }

    val contentColor = when (status) {
        StatusType.ANALISE -> Color(0xFFF57C00)
        StatusType.DOCS_INCORRETOS -> Color(0xFFD32F2F)
        StatusType.REJEITADA -> Color(0xFFD32F2F)
        else -> Color.Black
    }

    val title = when (status) {
        StatusType.ANALISE -> "Em Análise"
        StatusType.DOCS_INCORRETOS -> "Ação Necessária"
        StatusType.REJEITADA -> "Pedido Recusado"
        else -> "Estado Desconhecido"
    }

    val description = when (status) {
        StatusType.ANALISE -> "O processo encontra-se em validação pelos serviços."
        StatusType.DOCS_INCORRETOS -> "Alguns documentos precisam de ser corrigidos. Verifique a lista abaixo."
        StatusType.REJEITADA -> "Infelizmente o seu pedido não foi aceite."
        else -> ""
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}