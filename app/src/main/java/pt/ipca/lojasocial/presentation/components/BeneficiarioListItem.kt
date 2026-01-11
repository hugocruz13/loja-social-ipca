package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pt.ipca.lojasocial.domain.models.BeneficiaryStatus

@Composable
fun BeneficiarioListItem(
    fullName: String,
    status: BeneficiaryStatus, // Alterado de beneficiaryId para status
    avatarUrl: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val primaryGreen = Color(0xFF00713C)
    val lightSurface = Color(0xFFFBFDFB)

    // Definição dinâmica de cores baseada no estado
    val statusColor = when (status) {
        BeneficiaryStatus.ATIVO -> Color(0xFF2E7D32)    // Verde
        BeneficiaryStatus.ANALISE -> Color(0xFFF9A825)  // Amarelo
        BeneficiaryStatus.INATIVO -> Color(0xFFC62828)  // Vermelho
    }

    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp, pressedElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.White, lightSurface)
                    )
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Container
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryGreen.copy(alpha = 0.1f),
                                primaryGreen.copy(alpha = 0.2f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (avatarUrl.isNullOrBlank()) {
                    val initials = fullName.split(" ")
                        .filter { it.isNotEmpty() }
                        .let {
                            if (it.size >= 2) "${it[0][0]}${it[1][0]}"
                            else if (it.isNotEmpty()) "${it[0][0]}"
                            else "?"
                        }.uppercase()

                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Conteúdo de Texto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.1.sp
                    ),
                    color = Color(0xFF1A1C1E),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Badge de Estado (Status)
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        // Pequeno ponto colorido para reforçar o estado
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = status.name.lowercase().replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString()
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = statusColor
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver Detalhes",
                tint = Color.LightGray.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BeneficiarioListItemStatusPreview() {
    Column(Modifier.padding(16.dp)) {
        BeneficiarioListItem(
            fullName = "João Silva",
            status = BeneficiaryStatus.ATIVO,
            onClick = {}
        )
        BeneficiarioListItem(
            fullName = "Maria Antónia",
            status = BeneficiaryStatus.ANALISE,
            onClick = {}
        )
        BeneficiarioListItem(
            fullName = "Ricardo Pereira",
            status = BeneficiaryStatus.INATIVO,
            onClick = {}
        )
    }
}