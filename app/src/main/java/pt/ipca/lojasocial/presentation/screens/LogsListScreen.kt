package pt.ipca.lojasocial.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.AppLog
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.viewmodels.LogsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. O Ecrã "Stateful" (Ligado ao ViewModel)
@Composable
fun LogsListScreen(
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit = {},
    viewModel: LogsViewModel = hiltViewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // 1. Inicializar o Serviço de PDF
    // Usamos 'remember' para não criar um novo objeto a cada frame da UI
    val pdfService = remember { pt.ipca.lojasocial.utils.PdfLogsService(context) }

    LogsListContent(
        logs = logs,
        isLoading = isLoading,
        onBackClick = onBackClick,
        onDownloadClick = {
            if (logs.isNotEmpty()) {
                // 2. Mapeamento dos Dados (AppLog -> ItemRelatorioLog)
                val dadosParaPdf = logs.map { log ->
                    val sdf = java.text.SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        java.util.Locale.getDefault()
                    )
                    pt.ipca.lojasocial.domain.models.ItemRelatorioLog(
                        dataFormatada = sdf.format(java.util.Date(log.timestamp)),
                        acao = log.acao,
                        utilizador = log.utilizador,
                        detalhe = log.detalhe
                    )
                }

                // 3. Gerar o PDF
                pdfService.gerarRelatorioLogs(dadosParaPdf)

                // Feedback visual
                Toast.makeText(context, "A gerar relatório...", Toast.LENGTH_SHORT).show()
                onDownloadClick() // Mantém o teu callback original se tiveres lógica extra
            } else {
                Toast.makeText(context, "Não existem logs para exportar.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    )
}

// 2. O Ecrã "Stateless" (Pura UI)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsListContent(
    logs: List<AppLog>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Histórico de Ações",
                onBackClick = onBackClick
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // --- ÁREA DO BOTÃO DE DOWNLOAD ---
            // Colocamos num Row para gerir o alinhamento e padding
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Margem igual à do ProductListScreen
                horizontalArrangement = Arrangement.End, // Alinha à direita
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Texto opcional para explicar o botão (podes remover se quiseres só o botão)
                Text(
                    text = "Exportar Relatório",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )

                // --- BOTÃO DOWNLOAD (Estilo "Dropbox") ---
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)) // Mesmo arredondamento
                        .background(Color(0xFFF0F2F5)) // Mesma cor de fundo cinza
                        .clickable(onClick = onDownloadClick)
                        .padding(12.dp), // Mesmo padding interno
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Baixar Relatório",
                        tint = Color.Black, // Mesma cor de ícone (Preto)
                        modifier = Modifier.size(20.dp) // Mesmo tamanho de ícone
                    )
                }
            }

            // --- LISTA ---
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0XFF00713C))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(logs) { log ->
                        LogCard(log)
                    }
                }
            }
        }
    }
}

@Composable
fun LogCard(log: AppLog) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = sdf.format(Date(log.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone circular com a inicial da ação
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Color(0XFF00713C).copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val inicial = if (log.acao.isNotEmpty()) log.acao.take(1).uppercase() else "?"
                    Text(
                        text = inicial,
                        color = Color(0XFF00713C),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = log.acao,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Black
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = log.detalhe,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray
                )

                Text(
                    text = "Por: ${log.utilizador}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0XFF00713C),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// ================= PREVIEWS =================

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LogsListScreenPreview() {
    val dummyLogs = listOf(
        AppLog(
            id = "1",
            timestamp = System.currentTimeMillis(),
            acao = "APROVAÇÃO",
            detalhe = "Requerimento #123 aprovado",
            utilizador = "Admin"
        ),
        AppLog(
            id = "2",
            timestamp = System.currentTimeMillis() - 86400000,
            acao = "LOGIN",
            detalhe = "Entrada no sistema",
            utilizador = "Staff Maria"
        )
    )

    MaterialTheme {
        LogsListContent(
            logs = dummyLogs,
            isLoading = false,
            onBackClick = {},
            onDownloadClick = {}
        )
    }
}