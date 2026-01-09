package pt.ipca.lojasocial.presentation.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.domain.models.StatusType
import pt.ipca.lojasocial.presentation.viewmodels.BeneficiariesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiaryDetailScreen(
    beneficiaryId: String,
    onBackClick: () -> Unit,
    viewModel: BeneficiariesViewModel = hiltViewModel()
) {
    val beneficiary by viewModel.selectedBeneficiary.collectAsState()
    val request by viewModel.selectedRequest.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(beneficiaryId) {
        viewModel.loadBeneficiaryDetail(beneficiaryId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Perfil do Beneficiário", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00713C))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                beneficiary?.let { ben ->
                    // --- CARD 1: IDENTIFICAÇÃO (DADOS DO BENEFICIÁRIO) ---
                    DetailCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00713C).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    ben.name.take(1).uppercase(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = Color(0xFF00713C),
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    ben.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                // Corrigido: idAnoLetivo
                                Text(
                                    "Ano Letivo: ${ben.schoolYearId}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                                StatusBadge(status = ben.status.name)
                            }
                        }

                        HorizontalDivider(Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                        // Corrigido: Nomes das propriedades para baterem com o modelo da DB
                        InfoRow(Icons.Default.Badge, "CC / Identificação", ben.ccNumber)
                        InfoRow(Icons.Default.Email, "Email de contacto", ben.email)
                        InfoRow(Icons.Default.Phone, "Telemóvel", ben.phoneNumber.toString())
                        InfoRow(
                            Icons.Default.Cake,
                            "Data de Nascimento",
                            formatTimestamp(ben.birthDate.toLong())
                        )
                    }
                }

                request?.let { req ->
                    // --- CARD 2: REQUERIMENTO ---
                    DetailCard(title = "Requerimento Atual", icon = Icons.Default.Assignment) {
                        Text(
                            text = "Estado: ${req.status}",
                            color = if (req.status == StatusType.ANALISE) Color(0xFFF9A825) else Color(
                                0xFF00713C
                            ),
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Submetido em: ${formatTimestamp(req.submissionDate)}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Surface(
                            color = Color(0xFFF1F3F4),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    "Observações do Requerimento:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(req.observations, style = MaterialTheme.typography.bodyMedium)
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Documentos submetidos:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )

                        // Mapeamento amigável dos nomes dos documentos
                        req.documents.forEach { (tipo, url) ->
                            val labelFormatado = when (tipo) {
                                "identificacao" -> "Documento de Identificação"
                                "agregado" -> "Comprovativo do Agregado"
                                "morada" -> "Comprovativo de Morada"
                                "rendimento" -> "Comprovativo de Rendimentos"
                                "matricula" -> "Comprovativo de Matrícula"
                                else -> tipo.replaceFirstChar { it.uppercase() }
                            }

                            DocumentRow(
                                label = labelFormatado,
                                onOpen = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailCard(
    title: String? = null,
    icon: ImageVector? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            if (title != null && icon != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = Color(0xFF00713C), modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(Modifier.height(16.dp))
            }
            content()
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun DocumentRow(label: String, onOpen: () -> Unit) {
    Button(
        onClick = onOpen,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF1F8F5),
            contentColor = Color(0xFF00713C)
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Description, null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(label, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.OpenInNew, null, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status) {
        "ATIVO" -> Color(0xFF2E7D32)
        "ANALISE" -> Color(0xFFF9A825)
        else -> Color.Red
    }
    Surface(color = color.copy(alpha = 0.1f), shape = CircleShape) {
        Text(
            status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// Auxiliares de Formatação
fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    // Verifica se o timestamp está em segundos ou milissegundos
    val date = if (timestamp < 1000000000000L) Date(timestamp * 1000) else Date(timestamp)
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
}