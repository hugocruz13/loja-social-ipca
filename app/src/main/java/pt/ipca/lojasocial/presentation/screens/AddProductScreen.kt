package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppDatePickerField
import pt.ipca.lojasocial.presentation.components.AppDropdownField
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterProductScreen(
    onBackClick: () -> Unit,
    onAddToStockClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
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
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item ->
                    onNavigate(item.route)
                }
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

                    Surface(
                        tonalElevation = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE2E8F0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp)
                        ) {
                            val modifierWeight = Modifier
                                .weight(1f)
                                .fillMaxHeight()

                            Button(
                                onClick = { campaignType = "Interna" },
                                modifier = modifierWeight,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (campaignType == "Interna") Color.White else Color.Transparent,
                                    contentColor = if (campaignType == "Interna") Color(0xFF00713C) else Color(
                                        0xFF64748B
                                    )
                                ),
                                elevation = if (campaignType == "Interna") ButtonDefaults.buttonElevation(
                                    2.dp
                                ) else null,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Interna", fontWeight = FontWeight.Medium)
                            }

                            Button(
                                onClick = { campaignType = "Externa" },
                                modifier = modifierWeight,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (campaignType == "Externa") Color.White else Color.Transparent,
                                    contentColor = if (campaignType == "Externa") Color(0xFF00713C) else Color(
                                        0xFF64748B
                                    )
                                ),
                                elevation = if (campaignType == "Externa") ButtonDefaults.buttonElevation(
                                    2.dp
                                ) else null,
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

            AppButton(
                onClick = onAddToStockClick,
                text = "Add to Stock",
                containerColor = Color(0xFF00713C),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

