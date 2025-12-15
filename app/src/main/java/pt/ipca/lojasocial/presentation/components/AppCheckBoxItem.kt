package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

enum class ProductCategory {
    ALIMENTARES, HIGIENE, LIMPEZA, TODOS
}

@Composable
fun AppRadioCardItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {

    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0X3000713C) else Color.White
        ),
        border = if (isSelected) BorderStroke(1.5.dp, Color(0XFF00713C)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Color(0XFF00713C),
                    unselectedColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) Color(0XFF00713C) else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AppRadioCardListPreview() {
    var selectedCategory by remember { mutableStateOf(ProductCategory.ALIMENTARES) }

    Column(modifier = Modifier.padding(16.dp)) {

        AppRadioCardItem(
            label = "Produtos Alimentares",
            isSelected = selectedCategory == ProductCategory.ALIMENTARES,
            onClick = { selectedCategory = ProductCategory.ALIMENTARES }
        )

        AppRadioCardItem(
            label = "Produtos de Higiene Pessoal",
            isSelected = selectedCategory == ProductCategory.HIGIENE,
            onClick = { selectedCategory = ProductCategory.HIGIENE }
        )

        AppRadioCardItem(
            label = "Produtos de Limpeza",
            isSelected = selectedCategory == ProductCategory.LIMPEZA,
            onClick = { selectedCategory = ProductCategory.LIMPEZA }
        )

        AppRadioCardItem(
            label = "Todos",
            isSelected = selectedCategory == ProductCategory.TODOS,
            onClick = { selectedCategory = ProductCategory.TODOS }
        )
    }
}