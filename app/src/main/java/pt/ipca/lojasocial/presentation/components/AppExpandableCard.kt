package pt.ipca.lojasocial.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun AppExpandableCard(
    title: String,
    content: @Composable () -> Unit,
    initialExpanded: Boolean = false,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isExpanded) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    Box(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
    }
}

@Preview(showBackground = true)
@Composable
fun AppExpandableCardScreenPreview() {
    Column(modifier = Modifier.padding(16.dp)) {

        AppExpandableCard(
            title = "Informação Pessoal",
            initialExpanded = true,
            content = {
                Column {
                    InfoRow(label = "Contacto", value = "912 345 678")
                    InfoRow(label = "Email", value = "email@email.com")
                    InfoRow(label = "Data Nasc.", value = "24 Julho 1985")
                    InfoRow(label = "CC", value = "00000000 00Z")
                }
            }
        )

        AppExpandableCard(
            title = "Tipo Pedido / Dados Académicos",
            initialExpanded = false,
            content = {
                Text("Conteúdo de Dados Académicos...")
            }
        )

        AppExpandableCard(
            title = "Documentos Carregados",
            initialExpanded = false,
            content = {
                Text("Conteúdo de Documentos...")
            }
        )
    }
}