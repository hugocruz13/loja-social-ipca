package pt.ipca.lojasocial.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipca.lojasocial.presentation.components.*

@Composable
fun AddEditEntregaScreen(
    entregaId: String? = null,
    isCollaborator: Boolean = true,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    navItems: List<BottomNavItem>,
    onNavigate: (String) -> Unit
) {
    var beneficiario by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var selectedRepeticao by remember { mutableStateOf("Mensalmente") }

    val productList = remember { mutableStateListOf<DeliveryProduct>() }

    val scrollState = rememberScrollState()
    val accentGreen = Color(0XFF00713C)

    LaunchedEffect(entregaId) {
        if (entregaId != null) {
            data = "09/15/2024"
            hora = "10:30 AM"
            notas = "Introduza uma nota..."
            productList.clear()
            productList.add(DeliveryProduct("1", "Cesta Básica", 1))
            productList.add(DeliveryProduct("2", "Kit Limpeza", 2))
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Agendar Entrega",
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
                    query = beneficiario,
                    onQueryChange = { beneficiario = it },
                    placeholder = "Procurar beneficiário"
                )
            }

            Text("Data & Repetição", fontWeight = FontWeight.Bold)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AppTextField(label = "Data", value = data, onValueChange = { data = it }, placeholder = "mm/dd/yyyy")
                    AppTextField(label = "Hora", value = hora, onValueChange = { hora = it }, placeholder = "10:30 AM")

                    if (isCollaborator) {
                        Text("Repetição", style = MaterialTheme.typography.labelMedium)
                        val repeticoes = listOf("Não repetir", "Semanalmente", "Mensalmente", "Semestral")
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeticoes.take(2).forEach { repo ->
                                    AppButton(
                                        text = repo,
                                        onClick = { selectedRepeticao = repo },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        containerColor = if(selectedRepeticao == repo) accentGreen else Color(0xFFF1F1F5),
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                repeticoes.takeLast(2).forEach { repo ->
                                    AppButton(
                                        text = repo,
                                        onClick = { selectedRepeticao = repo },
                                        modifier = Modifier.weight(1f).height(40.dp),
                                        containerColor = if(selectedRepeticao == repo) accentGreen else Color(0xFFF1F1F5),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            DeliveryProductHeader(onAddProductClick = {
                productList.add(DeliveryProduct(productList.size.toString(), "Novo Produto", 1))
            })

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
                                val idx = productList.indexOfFirst { it.id == product.id }
                                if (idx != -1) productList[idx] = productList[idx].copy(quantity = newQty)
                            },
                            onRemove = { productList.removeAll { it.id == product.id } },
                            showDivider = index < productList.lastIndex
                        )
                    }
                }
            }

            Text("Notas", fontWeight = FontWeight.Bold)
            AppTextField(
                value = notas,
                onValueChange = { notas = it },
                label = "",
                placeholder = "Introduza uma nota...",
                modifier = Modifier.height(120.dp),
            )

            AppButton(
                text = if (entregaId == null) "Agendar Entrega" else "Guardar Alterações",
                onClick = onSaveClick,
                containerColor = accentGreen,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}