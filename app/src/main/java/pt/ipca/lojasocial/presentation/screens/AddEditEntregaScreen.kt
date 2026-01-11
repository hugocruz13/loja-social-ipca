@file:OptIn(ExperimentalMaterial3Api::class)

package pt.ipca.lojasocial.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.AppBottomBar
import pt.ipca.lojasocial.presentation.components.AppButton
import pt.ipca.lojasocial.presentation.components.AppSearchBar
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

    // Preparar lista de produtos para UI
    val productList = uiState.selectedProducts.mapNotNull { (productId, quantity) ->
        uiState.availableProducts.find { it.id == productId }?.let { product ->
            DeliveryProduct(id = product.id, name = product.name, quantity = quantity)
        }
    }

    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)
    val backgroundLight = Color(0xFFF8F9FA)

    // --- Navigation Effect ---
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveClick()
        }
    }

    // --- Carregamento Inicial ---
    LaunchedEffect(entregaId) {
        if (entregaId != null) {
            viewModel.loadDelivery(entregaId)
        }
    }

    // --- DIALOGS (Date, Time, Product) ---
    // Mantive a lógica dos dialogs separada para não poluir o layout principal
    HandleDialogs(
        uiState = uiState,
        viewModel = viewModel
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = if (entregaId == null) "Nova Entrega" else "Editar Entrega",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppBottomBar(
                navItems = navItems,
                currentRoute = "",
                onItemSelected = { item -> onNavigate(item.route) }
            )
        },
        containerColor = backgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp) // Mais espaço entre secções
        ) {

            // --- SECÇÃO 1: BENEFICIÁRIO (Apenas Colaborador) ---
            if (isCollaborator) {
                FormSection(title = "Beneficiário") {
                    if (entregaId == null) {
                        // MODO CRIAÇÃO: Pesquisa
                        Column {
                            AppSearchBar(
                                query = uiState.beneficiaryQuery,
                                onQueryChange = viewModel::onBeneficiaryQueryChange,
                                placeholder = "Pesquisar por nome ou nº..."
                            )

                            // Lista de Resultados (Animada)
                            AnimatedVisibility(visible = uiState.searchedBeneficiaries.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(4.dp)
                                ) {
                                    Column {
                                        uiState.searchedBeneficiaries.forEach { beneficiary ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.onBeneficiarySelected(
                                                            beneficiary
                                                        )
                                                    }
                                                    .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(accentGreen.copy(alpha = 0.1f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.Person,
                                                        null,
                                                        tint = accentGreen,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Spacer(Modifier.width(12.dp))
                                                Text(
                                                    beneficiary.name,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                            HorizontalDivider(color = Color(0xFFF1F5F9))
                                        }
                                    }
                                }
                            }

                            uiState.beneficiaryError?.let {
                                Text(
                                    it,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        // MODO EDIÇÃO: Read-only visualmente distinto
                        OutlinedTextField(
                            value = uiState.beneficiaryQuery,
                            onValueChange = {},
                            label = { Text("Nome do Beneficiário") },
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.LightGray,
                                disabledLabelColor = accentGreen
                            ),
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = accentGreen) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // --- SECÇÃO 2: QUANDO? (Data & Hora) ---
            FormSection(title = "Agendamento") {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Data Picker
                            ClickableInput(
                                value = uiState.date,
                                label = "Data",
                                placeholder = "dd/mm/aaaa",
                                icon = Icons.Outlined.CalendarMonth,
                                onClick = viewModel::showDatePickerDialog,
                                error = uiState.dateError,
                                modifier = Modifier.weight(1.5f)
                            )

                            // Hora Picker
                            ClickableInput(
                                value = uiState.time,
                                label = "Hora",
                                placeholder = "HH:mm",
                                icon = Icons.Outlined.Schedule,
                                onClick = viewModel::showTimePickerDialog,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Repetição (Só colaborador)
                        if (isCollaborator) {
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Text(
                                "Repetir Entrega",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Gray
                            )

                            val repeticoes =
                                listOf("Não repetir", "Mensalmente", "Bimensal", "Semestral")

                            // Layout de Chips para Repetição
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val chunked = repeticoes.chunked(2)
                                chunked.forEach { rowItems ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        rowItems.forEach { repo ->
                                            val isSelected = uiState.repetition == repo
                                            SelectableChip(
                                                text = repo,
                                                isSelected = isSelected,
                                                onClick = {
                                                    if (entregaId == null) viewModel.onRepetitionChange(
                                                        repo
                                                    )
                                                },
                                                accentColor = accentGreen,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- WARNING CARD (Se for entrega imediata) ---
            AnimatedVisibility(visible = entregaId == null && isCollaborator && uiState.isImmediateDelivery) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)), // Laranja muito suave
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCC80)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.WarningAmber, "Aviso", tint = Color(0xFFEA580C))
                        Column {
                            Text(
                                "Entrega Imediata",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF9A3412),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Ao selecionar a data de hoje, o stock será descontado imediatamente após guardar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9A3412)
                            )
                        }
                    }
                }
            }

            // --- SECÇÃO 3: O QUÊ? (Produtos) ---
            FormSection(title = "Produtos") {
                DeliveryProductHeader(onAddProductClick = viewModel::showProductPickerDialog)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(1.dp) // Elevação subtil
                ) {
                    if (productList.isEmpty()) {
                        // EMPTY STATE
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Outlined.ShoppingCart,
                                null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Nenhum produto adicionado", color = Color.Gray)
                            Text(
                                "Clique no + acima para adicionar",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                    } else {
                        // LISTA DE PRODUTOS
                        Column {
                            productList.forEachIndexed { index, product ->
                                QuantitySelectorItem(
                                    product = product,
                                    onQuantityChange = { newQty ->
                                        viewModel.onProductQuantityChange(
                                            product.id,
                                            newQty
                                        )
                                    },
                                    onRemove = { viewModel.onProductQuantityChange(product.id, 0) },
                                    showDivider = index < productList.lastIndex
                                )
                            }
                        }
                    }
                }
                uiState.productsError?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            // --- SECÇÃO 4: NOTAS ---
            FormSection(title = "Observações") {
                OutlinedTextField(
                    value = uiState.observations,
                    onValueChange = viewModel::onObservationsChange,
                    placeholder = { Text("Alguma nota sobre a entrega? (Opcional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentGreen,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTÃO GUARDAR ---
            AppButton(
                text = if (entregaId == null) "Agendar Entrega" else "Guardar Alterações",
                onClick = viewModel::saveDelivery,
                containerColor = if (uiState.isFormValid) accentGreen else Color(0xFFCBD5E1), // Desativado mais bonito
                contentColor = Color.White,
                enabled = uiState.isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

// =============================================================================
// COMPONENTES AUXILIARES PARA LIMPEZA DE CÓDIGO
// =============================================================================

@Composable
fun FormSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF334155),
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        content()
    }
}

@Composable
fun ClickableInput(
    value: String,
    label: String,
    placeholder: String,
    icon: ImageVector,
    onClick: () -> Unit,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {}, // ReadOnly
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            readOnly = true,
            enabled = false, // Desativado para input teclado, mas usamos o Box click
            trailingIcon = { Icon(icon, null, tint = Color(0xFF64748B)) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color(0xFF1E293B),
                disabledBorderColor = if (error != null) MaterialTheme.colorScheme.error else Color(
                    0xFFCBD5E1
                ),
                disabledLabelColor = Color(0xFF64748B),
                disabledPlaceholderColor = Color(0xFF94A3B8),
                disabledContainerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
        if (error != null) {
            Text(
                error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50), // Fully rounded
        color = if (isSelected) accentColor else Color(0xFFF1F5F9),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE2E8F0)
        ),
        modifier = modifier.height(40.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                color = if (isSelected) Color.White else Color(0xFF475569)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HandleDialogs(
    uiState: pt.ipca.lojasocial.presentation.models.AddEditEntregaUiState,
    viewModel: AddEditEntregaViewModel
) {
    // Dialogs Code (Date, Time, Product) - Igual ao original, apenas extraído para limpar a view principal
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
                }) { Text("OK") }
            },
            dismissButton = { Button(onClick = viewModel::hideDatePickerDialog) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    val timePickerState = rememberTimePickerState()
    if (uiState.isTimePickerDialogVisible) {
        AlertDialog(
            onDismissRequest = viewModel::hideTimePickerDialog,
            title = { Text("Selecionar Hora") },
            text = { TimePicker(state = timePickerState) },
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
                }) { Text("OK") }
            },
            dismissButton = { Button(onClick = viewModel::hideTimePickerDialog) { Text("Cancelar") } }
        )
    }

    if (uiState.isProductPickerDialogVisible) {
        ProductPickerDialog(
            products = uiState.availableProducts,
            selectedProducts = uiState.selectedProducts,
            stockLimits = uiState.productStockLimits,
            onProductQuantityChange = viewModel::onProductQuantityChange,
            onDismiss = viewModel::hideProductPickerDialog,
            onConfirm = viewModel::hideProductPickerDialog
        )
    }
}