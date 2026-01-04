package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DeliveryProductItem(
    productName: String,
    quantity: Int,
    unit: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = productName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "$quantity $unit",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0XFF6A7280)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DeliveryProductListPreview() {
    Column(modifier = Modifier.padding(16.dp)) {

        Text("Produtos na Entrega", style = MaterialTheme.typography.titleLarge)

        DeliveryProductItem(
            productName = "Arroz (5kg)",
            quantity = 2,
            unit = "sacos"
        )

        Divider(
            modifier = Modifier
                .height(1.dp)
                .padding(horizontal = 10.dp)
        )

        DeliveryProductItem(
            productName = "Leite UHT",
            quantity = 10,
            unit = "litros"
        )

        Divider(
            modifier = Modifier
                .height(1.dp)
                .padding(horizontal = 8.dp)
        )

        DeliveryProductItem(
            productName = "Sabão para Roupas",
            quantity = 1,
            unit = "embalagem"
        )
    }
}