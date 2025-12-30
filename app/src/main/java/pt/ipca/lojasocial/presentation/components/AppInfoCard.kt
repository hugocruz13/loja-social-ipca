package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign // Ícone para Campanhas
import androidx.compose.material.icons.filled.LocalShipping // Ícone para Entregas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppInfoCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier.width(IntrinsicSize.Max).height(IntrinsicSize.Max),
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    // Usamos Card do Material 3 para elevação e forma
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = Color(0XFF64748B)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppInfoCardCampaignsPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AppInfoCard(
            title = "Campanhas Ativas",
            value = "5",
            icon = Icons.Filled.Campaign
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppInfoCardDeliveriesPreview() {
    Surface(modifier = Modifier.padding(16.dp)) {
        AppInfoCard(
            title = "Entregas Pendentes",
            value = "12",
            icon = Icons.Filled.LocalShipping
        )
    }
}