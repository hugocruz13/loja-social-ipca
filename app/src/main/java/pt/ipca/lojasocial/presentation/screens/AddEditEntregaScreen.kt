package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pt.ipca.lojasocial.presentation.components.*
import pt.ipca.lojasocial.presentation.viewmodels.AddEditEntregaViewModel

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

    LaunchedEffect(entregaId) {
        if (entregaId != null) {
            viewModel.loadDelivery(entregaId)
        }
    }

    if (uiState.isProductPickerDialogVisible) {
        ProductPickerDialog(
            products = uiState.availableProducts,
            selectedProducts = uiState.selectedProducts,
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
                onItemSelected = { item -> onNavigate(item.route)
                }
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
            // SEÇÃO: Beneficiário (Apenas Colaborador)
            if (isCollaborator) {
                Text("Beneficiário", fontWeight = FontWeight.Bold)
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
            }

            Text("Data & Repetição", fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(label = "Data", value = uiState.date, onValueChange = viewModel::onDateChange, placeholder = "dd/mm/yyyy")
                    AppTextField(label = "Hora", value = uiState.time, onValueChange = viewModel::onTimeChange, placeholder = "HH:mm")

                    if (isCollaborator) {
                        Text("Repetição", style = MaterialTheme.typography.labelMedium)
                        val repeticoes = listOf("Não repetir", "Semanalmente", "Mensalmente", "Semestral")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeticoes.take(2).forEach { repo ->
                                    AppButton(
                                        text = repo,
                                        onClick = { viewModel.onRepetitionChange(repo) },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        containerColor = if(uiState.repetition == repo) accentGreen else Color(0xFFF1F1F5),
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeticoes.takeLast(2).forEach { repo ->
                                    AppButton(
                                        text = repo,
                                        onClick = { viewModel.onRepetitionChange(repo) },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        containerColor = if(uiState.repetition == repo) accentGreen else Color(0xFFF1F1F5),
                                    )
                                }
                            }
                        }
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
                onClick = {
                    viewModel.saveDelivery()
                    onSaveClick()
                },
                containerColor = accentGreen,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}
