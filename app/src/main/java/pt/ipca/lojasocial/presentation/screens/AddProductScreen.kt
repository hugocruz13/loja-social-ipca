package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.*

@Composable
fun RegisterProductScreen(
    onBackClick: () -> Unit,
    onAddToStockClick: () -> Unit
) {
    // --- ESTADOS DO FORMULÁRIO ---
    var productType by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf("10/27/2023") }
    var expiryDate by remember { mutableStateOf("") }
    var campaignType by remember { mutableStateOf("Interna") } // Interna ou Externa
    var selectedCampaign by remember { mutableStateOf("Winter Drive 2023") }

    val productTypes = listOf("Alimentação", "Higiene", "Vestuário", "Limpeza")
    val campaigns = listOf("Winter Drive 2023", "Campanha de Primavera", "Recolha Local")

    val backgroundColor = Color(0xFFF8F9FA)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Registar Produto",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            val navItems = listOf(
                BottomNavItem("home", Icons.Filled.Home, "Home"),
                BottomNavItem("notifications", Icons.Filled.Notifications, "Notificações"),
                BottomNavItem("settings", Icons.Filled.Settings, "Configurações"),
            )
            AppBottomBar(
                navItems = navItems,
                currentRoute = "home",
                onItemSelected = { }
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- SECÇÃO: INFORMAÇÃO PRODUTO ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Informação Produto",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                AppDropdownField(
                    label = "Tipo de Produto",
                    selectedValue = productType,
                    options = productTypes,
                    onOptionSelected = { productType = it },
                    placeholder = "Selecione tipo"
                )

                AppTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = "Quantidade",
                    placeholder = "Introduza a quantidade em unidade",
                    keyboardType = KeyboardType.Number
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppDatePickerField(
                        label = "Data Entrega",
                        selectedValue = deliveryDate,
                        onDateSelected = { deliveryDate = it },
                        modifier = Modifier.weight(1f)
                    )
                    AppDatePickerField(
                        label = "Data Validade",
                        selectedValue = expiryDate,
                        onDateSelected = { expiryDate = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- SECÇÃO: ASSOCIAR CAMPANHA ---
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Associar Campanha",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Column {
                    Text(
                        text = "Associar a uma campanha?",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Seletor Interna/Externa (Segmented Control manual)
                    Surface(
                        tonalElevation = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                            val modifierWeight = Modifier.weight(1f).fillMaxHeight()

                            // Botão Interna
                            Button(
                                onClick = { campaignType = "Interna" },
                                modifier = modifierWeight,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (campaignType == "Interna") Color.White else Color.Transparent,
                                    contentColor = if (campaignType == "Interna") Color(0xFF00713C) else Color(0xFF64748B)
                                ),
                                elevation = if (campaignType == "Interna") ButtonDefaults.buttonElevation(2.dp) else null,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Interna", fontWeight = FontWeight.Medium)
                            }

                            // Botão Externa
                            Button(
                                onClick = { campaignType = "Externa" },
                                modifier = modifierWeight,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (campaignType == "Externa") Color.White else Color.Transparent,
                                    contentColor = if (campaignType == "Externa") Color(0xFF00713C) else Color(0xFF64748B)
                                ),
                                elevation = if (campaignType == "Externa") ButtonDefaults.buttonElevation(2.dp) else null,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Externa", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                AppDropdownField(
                    label = "Selecionar Campanha",
                    selectedValue = selectedCampaign,
                    options = campaigns,
                    onOptionSelected = { selectedCampaign = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÃO FINAL ---
            AppButton(
                onClick = onAddToStockClick,
                text = "Add to Stock",
                containerColor = Color(0xFF00713C),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterProductScreenPreview() {
    RegisterProductScreen(
        onBackClick = {},
        onAddToStockClick = {}
    )
}