package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.domain.models.StatusType

@Composable
fun AnoLetivoListItem(
    yearLabel: String,
    isCurrentYear: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val iconColor = if (isCurrentYear) Color(0XFF00713C) else MaterialTheme.colorScheme.onSurfaceVariant
    val circleBackgroundColor = if (isCurrentYear) {
        Color(0X3000713C)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    circleBackgroundColor

    val currentYearStatus = StatusType.ATUAL

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(circleBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = "Ano Letivo",
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = yearLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isCurrentYear) {
                    AppStatusBadge(status = currentYearStatus)
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Selecionar ou Ver Detalhes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnoLetivoListItemCurrentPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AnoLetivoListItem(
            yearLabel = "2024/2025",
            isCurrentYear = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnoLetivoListItemCompletedPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AnoLetivoListItem(
            yearLabel = "2023/2024",
            isCurrentYear = false,
            onClick = {}
        )
    }
}