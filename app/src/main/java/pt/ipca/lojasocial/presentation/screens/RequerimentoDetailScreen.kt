package pt.ipca.lojasocial.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppExpandableCard
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.InfoRow
import pt.ipca.lojasocial.presentation.viewmodels.RequerimentoDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequerimentoDetailScreen(
    requerimentoId: String, // (Opcional, o ViewModel já pega pelo SavedStateHandle)
    onBackClick: () -> Unit,
    onNavigate: (String) -> Unit,
    // Injetamos o ViewModel
    viewModel: RequerimentoDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val accentGreen = Color(0XFF00713C)
    val warningOrange = Color(0xFFF57C00)

    // Estados dos Modais
    var showRejectModal by remember { mutableStateOf(false) }
    var showApproveModal by remember { mutableStateOf(false) } // NOVO: Modal de Aprovação
    var showDocsModal by remember { mutableStateOf(false) }

    var justificacao by remember { mutableStateOf("") }

    // Lista de chaves selecionadas (ex: "morada", "irs")
    val selectedDocKeys = remember { mutableStateListOf<String>() }

    // Mapa para "traduzir" as chaves da BD para texto bonito na UI
    val docLabels = mapOf(
        "identificacao" to "Doc. Identificação",
        "agregado" to "Doc. Agregado Familiar",
        "morada" to "Comprovativo de Morada",
        "rendimento" to "Comprovativo de Rendimentos",
        "matricula" to "Comprovativo de Matrícula"
    )

    // --- LOADING STATE ---
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = accentGreen)
        }
        return
    }

    // --- EMPTY/ERROR STATE ---
    val data = uiState
    if (data == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Erro ao carregar requerimento.")
            Button(onClick = onBackClick) { Text("Voltar") }
        }
        return
    }

    // --- MODAL DE REJEIÇÃO ---
    if (showRejectModal) {
        AlertDialog(
            onDismissRequest = { showRejectModal = false },
            title = { Text("Rejeitar Requerimento") },
            text = {
                Column {
                    Text(
                        "Esta ação irá marcar o pedido como Rejeitado.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppTextField(
                        value = justificacao,
                        onValueChange = { justificacao = it },
                        label = "Motivo",
                        placeholder = "Escreva a razão...",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.rejectRequest(justificacao)
                        showRejectModal = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red),
                    enabled = justificacao.isNotBlank()
                ) { Text("Confirmar Rejeição") }
            },
            dismissButton = {
                TextButton(onClick = { showRejectModal = false }) { Text("Cancelar") }
            }
        )
    }

    // --- MODAL DE APROVAÇÃO (NOVO) ---
    if (showApproveModal) {
        AlertDialog(
            onDismissRequest = { showApproveModal = false },
            title = { Text("Aprovar Requerimento") },
            text = { Text("Tem a certeza que deseja aprovar este requerimento?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.approveRequest()
                        showApproveModal = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentGreen)
                ) { Text("Confirmar Aprovação") }
            },
            dismissButton = {
                TextButton(onClick = { showApproveModal = false }) { Text("Cancelar") }
            }
        )
    }

    // --- MODAL DE DOCUMENTOS INCORRETOS ---
    if (showDocsModal) {
        AlertDialog(
            onDismissRequest = { showDocsModal = false; selectedDocKeys.clear() },
            title = { Text("Documentos Incorretos") },
            text = {
                Column {
                    Text(
                        "Selecione os documentos que serão apagados e solicitados novamente:",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Itera sobre o MAPA de documentos (Chave -> URL)
                    data.documents.forEach { (key, url) ->
                        // Só mostra se o documento existir (URL não nulo)
                        if (url != null) {
                            val label = docLabels[key] ?: key // Usa label bonito ou a chave

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (selectedDocKeys.contains(key)) selectedDocKeys.remove(
                                            key
                                        )
                                        else selectedDocKeys.add(key)
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Checkbox(
                                    checked = selectedDocKeys.contains(key),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) selectedDocKeys.add(key) else selectedDocKeys.remove(
                                            key
                                        )
                                    }
                                )
                                Text(text = label)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Envia as chaves (ex: "morada") para o ViewModel apagar
                        viewModel.markDocumentsIncorrect(data.id, selectedDocKeys.toList())
                        showDocsModal = false
                        onBackClick()
                    },
                    enabled = selectedDocKeys.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = warningOrange)
                ) { Text("Solicitar Correção") }
            },
            dismissButton = {
                TextButton(onClick = { showDocsModal = false }) { Text("Cancelar") }
            }
        )
    }

    // --- UI PRINCIPAL ---
    Scaffold(
        topBar = { AppTopBar(title = "Detalhe do Requerimento", onBackClick = onBackClick) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (data.beneficiaryName.isNotBlank()) data.beneficiaryName.take(1)
                            .uppercase() else "?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = data.beneficiaryName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "CC: ${data.cc}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Submetido: ${data.submissionDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Badge de Estado usando StatusType
                    val statusColor = when (data.status) {
                        StatusType.APROVADA -> accentGreen
                        StatusType.REJEITADA -> Color.Red
                        StatusType.DOCS_INCORRETOS -> warningOrange
                        else -> Color.Gray
                    }
                    Text(
                        text = data.status.name, // Ou uma função helper para traduzir o Enum
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Cards de Informação
            AppExpandableCard(title = "Informação Pessoal", initialExpanded = true, content = {
                Column {
                    InfoRow(label = "Email", value = data.email)
                    InfoRow(label = "Contacto", value = data.phone)
                }
            })

            AppExpandableCard(title = "Dados do Pedido", content = {
                Column {
                    InfoRow(label = "Tipo", value = data.type?.name ?: "ALL")
                }
            })

            AppExpandableCard(
                title = "Documentos",
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Verifica se todos os valores do mapa são nulos ou se o mapa está vazio
                        val hasDocuments = data.documents.any { it.value != null }

                        if (!hasDocuments) {
                            Text("Sem documentos submetidos.", color = Color.Gray)
                        } else {
                            // Itera sobre o Mapa
                            data.documents.forEach { (key, url) ->
                                // Apenas mostra se tiver URL
                                if (url != null) {
                                    val label = docLabels[key] ?: key

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        // Nome do Documento e Ícone
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = accentGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = label,
                                                color = Color.Black,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        // Botão de Download / Visualizar
                                        IconButton(
                                            onClick = {
                                                try {
                                                    val intent =
                                                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = "Baixar",
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botões de Ação (Só mostra se estiver PENDENTE ou EM ANALISE)
            // Se já estiver Aprovado ou Rejeitado, geralmente não queremos mexer mais
            if (data.status != StatusType.APROVADA && data.status != StatusType.REJEITADA) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppButton(
                        text = "Documentos Incorretos",
                        onClick = {
                            selectedDocKeys.clear()
                            showDocsModal = true
                        },
                        containerColor = warningOrange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AppButton(
                            text = "Rejeitar",
                            onClick = { showRejectModal = true },
                            containerColor = Color.Red,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                        AppButton(
                            text = "Aceitar",
                            onClick = { showApproveModal = true },
                            containerColor = accentGreen,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                    }
                }
            }
        }
    }
}