package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Numbers

@Composable
fun ProductInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {

    Row(
        modifier = modifier
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))


        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )


        Spacer(modifier = Modifier.weight(1f))


        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight // Negrito leve para o valor
            ),
            color = valueColor
        )
    }


}



@Preview(showBackground = true)
@Composable
fun ProductDetailsListPreview() {
    val highlightColor = Color(0xFFFF9800)

    Column(modifier = Modifier.padding(16.dp)) {

        Text(text = "Arroz", style = MaterialTheme.typography.headlineLarge)

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        ProductInfoRow(
            icon = Icons.Filled.Category,
            label = "Tipo Produto",
            value = "Comida"
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        ProductInfoRow(
            icon = Icons.Filled.Inventory2,
            label = "Quantidade",
            value = "32 unidades",
            valueColor = highlightColor
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        ProductInfoRow(
            icon = Icons.Filled.AccessTimeFilled,
            label = "Validade",
            value = "Expira a 31/12/2025",
            valueColor = highlightColor
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

        ProductInfoRow(
            icon = Icons.Filled.Numbers,
            label = "Código Produto",
            value = "FSB-0012-C4"
        )

        Divider(modifier = Modifier.padding(vertical = 4.dp))

    }
}