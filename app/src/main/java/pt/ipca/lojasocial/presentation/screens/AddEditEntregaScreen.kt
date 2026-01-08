@file:OptIn(ExperimentalMaterial3Api::class)

package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppSearchBar
import pt.ipca.lojasocial.presentation.components.AppTextField
import pt.ipca.lojasocial.presentation.components.AppTopBar
import pt.ipca.lojasocial.presentation.components.BottomNavItem
import pt.ipca.lojasocial.presentation.components.DeliveryProduct
import pt.ipca.lojasocial.presentation.components.DeliveryProductHeader
import pt.ipca.lojasocial.presentation.components.ProductPickerDialog
import pt.ipca.lojasocial.presentation.components.QuantitySelectorItem
import pt.ipca.lojasocial.presentation.viewmodels.AddEditEntregaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEntregaScreen(
    entregaId: String? = null,
    isCollaborator: Boolean = true,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit,
    viewModel: AddEditEntregaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val productList = uiState.selectedProducts.mapNotNull { (productId, quantity) ->
        uiState.availableProducts.find { it.id == productId }?.let { product ->
            DeliveryProduct(id = product.id, name = product.name, quantity = quantity)
        }
    }

    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    // --- Navigation Effect ---
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveClick()
        }
    }

    // --- Date Picker State ---
    val datePickerState = rememberDatePickerState()
    if (uiState.isDatePickerDialogVisible) {
        DatePickerDialog(
            onDismissRequest = viewModel::hideDatePickerDialog,
            confirmButton = {
                Button(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        viewModel.onDateChange(sdf.format(Date(millis)))
                    }
                    viewModel.hideDatePickerDialog()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = viewModel::hideDatePickerDialog) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- Time Picker State ---
    val timePickerState = rememberTimePickerState()
    if (uiState.isTimePickerDialogVisible) {
        AlertDialog(
            onDismissRequest = viewModel::hideTimePickerDialog,
            title = { Text("Selecionar Hora") },
            text = {
                TimePicker(state = timePickerState)
            },
            confirmButton = {
                Button(onClick = {
                    val time = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    viewModel.onTimeChange(time)
                    viewModel.hideTimePickerDialog()
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = viewModel::hideTimePickerDialog) {
                    Text("Cancelar")
                }
            }
        )
    }

    LaunchedEffect(entregaId) {
        if (entregaId != null) {
            viewModel.loadDelivery(entregaId)
        }
    }

    if (uiState.isProductPickerDialogVisible) {
        ProductPickerDialog(
            products = uiState.availableProducts,
            selectedProducts = uiState.selectedProducts,
            stockLimits = uiState.productStockLimits, // Pass the limits here
            onProductQuantityChange = viewModel::onProductQuantityChange,
            onDismiss = viewModel::hideProductPickerDialog,
            onConfirm = viewModel::hideProductPickerDialog
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (entregaId == null) "Agendar Entrega" else "Editar Entrega",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isCollaborator) {
                Text("Beneficiário", fontWeight = FontWeight.Bold)

                if (entregaId == null) {
                    // MODO CRIAÇÃO: Permite pesquisar
                    AppSearchBar(
                        query = uiState.beneficiaryQuery,
                        onQueryChange = viewModel::onBeneficiaryQueryChange,
                        placeholder = "Procurar beneficiário"
                    )
                    if (uiState.searchedBeneficiaries.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                            items(uiState.searchedBeneficiaries) { beneficiary ->
                                Text(
                                    text = beneficiary.name,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.onBeneficiarySelected(beneficiary) }
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                } else {
                    // MODO EDIÇÃO: Apenas leitura
                    AppTextField(
                        value = uiState.beneficiaryQuery, // O loadDelivery preenche isto com o nome
                        onValueChange = {},
                        label = "Nome do Beneficiário",
                        placeholder = "", // Adicionado placeholder obrigatório
                        readOnly = true,
                        enabled = false // Visualmente desativado
                    )
                }
            }

            Text("Data & Repetição", fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box {
                        AppTextField(
                            label = "Data",
                            value = uiState.date,
                            onValueChange = {},
                            placeholder = "dd/mm/yyyy",
                            readOnly = true
                        )
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(onClick = viewModel::showDatePickerDialog)
                        )
                    }
                    Box {
                        AppTextField(
                            label = "Hora",
                            value = uiState.time,
                            onValueChange = {},
                            placeholder = "HH:mm",
                            readOnly = true
                        )
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(onClick = viewModel::showTimePickerDialog)
                        )
                    }

                    if (isCollaborator) {
                        Text("Repetição", style = MaterialTheme.typography.labelMedium)
                        val repeticoes = listOf("Não repetir", "Mensalmente", "Bimensal", "Semestral")

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Primeira Linha
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeticoes.take(2).forEach { repo ->
                                    val isSelected = uiState.repetition == repo // Verifica se está selecionado

                                    AppButton(
                                        text = repo,
                                        onClick = { viewModel.onRepetitionChange(repo) },
                                        enabled = entregaId == null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp),
                                        // Fundo: Se selecionado usa Verde, se não usa um Cinza mais escuro que o anterior (E2E8F0)
                                        containerColor = if (isSelected) accentGreen else Color(0xFFE2E8F0),
                                        // Texto: Se selecionado usa Branco, se não usa um Cinza Escuro/Preto para contraste
                                        contentColor = if (isSelected) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }

                            // Segunda Linha
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeticoes.takeLast(2).forEach { repo ->
                                    val isSelected = uiState.repetition == repo

                                    AppButton(
                                        text = repo,
                                        onClick = { viewModel.onRepetitionChange(repo) },
                                        enabled = entregaId == null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp),
                                        // Mesma lógica de cores aqui
                                        containerColor = if (isSelected) accentGreen else Color(0xFFE2E8F0),
                                        contentColor = if (isSelected) Color.White else Color(0xFF1E293B)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // WARNING FOR IMMEDIATE DELIVERY
            if (entregaId == null && isCollaborator && uiState.isImmediateDelivery) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), // Light Orange
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Aviso",
                            tint = Color(0xFFE65100) // Dark Orange
                        )
                        Text(
                            text = "Atenção: Data de hoje selecionada. A entrega será marcada como REALIZADA e o stock será descontado imediatamente.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            DeliveryProductHeader(onAddProductClick = viewModel::showProductPickerDialog)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    productList.forEachIndexed { index, product ->
                        QuantitySelectorItem(
                            product = product,
                            onQuantityChange = { newQty ->
                                viewModel.onProductQuantityChange(product.id, newQty)
                            },
                            onRemove = { viewModel.onProductQuantityChange(product.id, 0) },
                            showDivider = index < productList.lastIndex
                        )
                    }
                }
            }

            Text("Notas", fontWeight = FontWeight.Bold)
            AppTextField(
                value = uiState.observations,
                onValueChange = viewModel::onObservationsChange,
                label = "",
                placeholder = "Introduza uma nota...",
                modifier = Modifier.height(120.dp),
            )

            AppButton(
                text = if (entregaId == null) "Agendar Entrega" else "Guardar Alterações",
                onClick = viewModel::saveDelivery,
                containerColor = accentGreen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}
