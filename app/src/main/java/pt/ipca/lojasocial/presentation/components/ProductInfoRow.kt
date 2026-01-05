package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

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
            .padding(vertical = 16.dp),
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


@Composable
fun ProductDetailsListPreview(
    type: String,
    quantity: String,
    lastDelivery: String,
    expiry: String,
    code: String,
    campaign: String?
) {
    val highlightColor = Color(0xFFFF9800)

    Column(modifier = Modifier.padding(16.dp)) {

        HorizontalDivider()
        ProductInfoRow(
            icon = Icons.Filled.Category,
            label = "Tipo Produto",
            value = type
        )

        HorizontalDivider()
        ProductInfoRow(
            icon = Icons.Filled.Inventory2,
            label = "Quantidade",
            value = quantity,
            valueColor = highlightColor
        )

        HorizontalDivider()
        ProductInfoRow(
            icon = Icons.Filled.AccessTimeFilled,
            label = "Última Entrega",
            value = lastDelivery
        )

        HorizontalDivider()
        ProductInfoRow(
            icon = Icons.Filled.Event,
            label = "Validade",
            value = expiry
        )

        HorizontalDivider()
        ProductInfoRow(
            icon = Icons.Filled.Numbers,
            label = "Código Produto",
            value = code
        )

        HorizontalDivider()
        ProductInfoRow(
            icon = Icons.Filled.Link,
            label = "Campanha Associada",
            value = campaign?.takeLast(6) ?: "Nenhuma"
        )

        HorizontalDivider()
    }
}

@Composable
fun EditableProductInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unitLabel: String = "Units",
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Row(
        modifier = modifier.padding(vertical = 6.dp),
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
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .offset(y = (-8).dp)
            ) {
                AppTextField(
                    value = value,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) {
                            onValueChange(it)
                        }
                    },
                    label = "",
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.width(64.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = unitLabel,
                color = Color(0XFF00713C),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}





