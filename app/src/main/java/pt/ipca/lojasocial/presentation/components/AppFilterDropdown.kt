package pt.ipca.lojasocial.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppFilterDropdown(
    label: String, // Texto a mostrar quando nada está selecionado ou título fixo
    selectedValue: String, // Valor atual selecionado
    options: List<String>, // Lista de opções
    onOptionSelected: (String) -> Unit, // Callback
    leadingIcon: ImageVector? = null, // Ícone da esquerda (opcional)
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Define o texto a mostrar: Se houver valor selecionado mostra o valor, senão mostra a label
    val displayText = selectedValue.ifEmpty { label }

    // Container Principal (Box para sobrepor o Menu)
    Box(modifier = modifier) {

        // A "Caixa" visual do filtro (Fundo cinza, cantos arredondados)
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)) // Arredondamento igual aos teus outros componentes
                .background(Color(0xFFF0F2F5)) // Um cinza claro semelhante à imagem
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 12.dp), // Espaçamento interno
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Ícone da esquerda (ex: Calendário ou Filtro)
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            // 2. Texto do Filtro
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            )

            // 3. Seta para baixo
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expandir",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }

        // Menu Dropdown
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            // Opção para "Limpar" ou mostrar o título original (opcional, mas útil em filtros)
            if (selectedValue.isNotEmpty()) {
                DropdownMenuItem(
                    text = { Text("Limpar filtro", color = MaterialTheme.colorScheme.error) },
                    onClick = {
                        onOptionSelected("") // Retorna vazio para limpar
                        expanded = false
                    }
                )
                HorizontalDivider()
            }

            // Lista de Opções
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            // Destacar a opção selecionada a negrito
                            fontWeight = if (option == selectedValue) FontWeight.Bold else FontWeight.Normal,
                            color = if (option == selectedValue) Color(0XFF00713C) else Color.Unspecified
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ================= PREVIEWS =================

@Preview(name = "Filtro Ano Letivo", showBackground = true)
@Composable
fun AppFilterDropdownDatePreview() {
    // Exemplo igual ao da esquerda na imagem (2024-2025)
    var selectedYear by remember { mutableStateOf("2024-2025") }
    val years = listOf("2023-2024", "2024-2025", "2025-2026")

    Surface(modifier = Modifier.padding(16.dp)) {
        AppFilterDropdown(
            label = "Ano Letivo",
            selectedValue = selectedYear,
            options = years,
            onOptionSelected = { selectedYear = it },
            leadingIcon = Icons.Default.CalendarToday
        )
    }
}

@Preview(name = "Filtro Status", showBackground = true)
@Composable
fun AppFilterDropdownStatusPreview() {
    // Exemplo igual ao da direita na imagem (Status)
    // Aqui simulamos que ainda nada foi selecionado, por isso mostra "Status"
    var selectedStatus by remember { mutableStateOf("") }
    val statusList = listOf("Ativo", "Inativo", "Pendente")

    Surface(modifier = Modifier.padding(16.dp)) {
        AppFilterDropdown(
            label = "Status",
            selectedValue = selectedStatus,
            options = statusList,
            onOptionSelected = { selectedStatus = it },
            leadingIcon = Icons.Default.Tune // Ícone parecido com as barras de filtro
        )
    }
}