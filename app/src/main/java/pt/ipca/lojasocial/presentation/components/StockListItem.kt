package pt.ipca.lojasocial.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pt.ipca.lojasocial.presentation.models.StockBatchUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StockListItem(
    name: String,
    totalQuantity: Int,
    imageUrl: String?,
    batches: List<StockBatchUi>, // Lista de lotes/validades
    onItemClick: (String) -> Unit // Clicar num lote específico para editar/apagar
) {
    // Estado para controlar se está expandido ou não
    var expanded by remember { mutableStateOf(false) }

    // Rotação da setinha
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = "rotation"
    )

    // Lógica de Cores baseada no TOTAL
    val containerColor = when {
        totalQuantity == 0 -> Color(0xFFFFDAD6) // Vermelho
        totalQuantity < 5 -> Color(0xFFFFF9C4)  // Amarelo
        else -> Color.White
    }

    val quantityColor = if (totalQuantity == 0) Color(0xFFB3261E) else Color.Black

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            // O clique no Card expande/colapsa
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // --- CABEÇALHO (Produto Genérico) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Esquerda: Imagem + Nome
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.weight(1f) // Texto ocupa o espaço disponível
                    )
                }

                // Direita: Quantidade Total + Seta
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$totalQuantity un",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = quantityColor
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Ícone da seta que roda
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Expandir",
                        modifier = Modifier.rotate(rotationState),
                        tint = Color.Gray
                    )
                }
            }

            // --- DETALHES (Lista de Validades) ---
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.05f)) // Fundo ligeiramente mais escuro
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (batches.isEmpty()) {
                        Text(
                            text = "Sem registos de stock detalhados.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Text(
                            text = "Lotes / Validades:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        batches.forEach { batch ->
                            BatchItemRow(batch = batch, onClick = { onItemClick(batch.stockId) })
                            Divider(color = Color.Black.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BatchItemRow(batch: StockBatchUi, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val dateStr =
        if (batch.expiryDate > 0) dateFormat.format(Date(batch.expiryDate)) else "Sem Validade"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Val: $dateStr",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = "${batch.quantity} un",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}